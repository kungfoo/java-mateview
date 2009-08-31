package com.redcareditor.mate;

import java.util.ArrayList;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;

import com.redcareditor.onig.Range;

public class Parser {
	public Grammar grammar;
	public Colourer colourer;
	public MateText mateText;
	public StyledText styledText;
	
	public int parsed_upto;	
	public int lookAhead;
	public int lastVisibleLine;
	public int deactivationLevel;
	public int parsedUpto;
	public boolean alwaysParseAll;
	
	public RangeSet changes;
	public Scope root;
	
	// temporary stores for the modifications to the mateText
	private int modifyStart, modifyEnd;
	private String modifyText;
	
	public Parser(Grammar g, MateText m) {
		g.initForUse();
		grammar = g;
		mateText = m;
		styledText = m.getTextWidget();
		lookAhead = 100;
		lastVisibleLine = 0;
//		tags = new Sequence<TextTag>(null);
		changes = new RangeSet();
		colourer = new Colourer(m);
		deactivationLevel = 0;
		makeRoot();
		attachListeners();
		parsedUpto = 0;
		alwaysParseAll = false;
	}
	
	public void makeRoot() {
		this.root = new Scope(mateText, this.grammar.scopeName);
		this.root.isOpen = true;
//		this.root.setStartPos(0, 0, true);
//		int lineIx = styledText.getLineCount()-1;
//		this.root.setEndPos(lineIx, 
//							styledText.getCharCount() - styledText.getOffsetAtLine(lineIx), false);
		DoublePattern dp = new DoublePattern();
		dp.name = this.grammar.name;
		dp.patterns = this.grammar.allPatterns;
		dp.grammar = this.grammar;
		this.root.pattern = dp;
	}
	
	public void attachListeners() {
		mateText.getTextWidget().addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent e) {
				verifyEventCallback(e.start, e.end, e.text);
			}
		});
		
		mateText.getTextWidget().addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				modifyEventCallback();
			}
		});
	}

	public void verifyEventCallback(int start, int end, String text) {
		modifyStart = start;
		modifyEnd   = end;
		modifyText  = text;
	}
	
	public void modifyEventCallback() {
		changes.add(mateText.getTextWidget().getLineAtOffset(modifyStart), 
					mateText.getTextWidget().getLineAtOffset(modifyEnd));
		System.out.printf("modifying %d - %d, %d, %s\n", modifyStart, modifyEnd, mateText.getTextWidget().getLineAtOffset(modifyStart), modifyText);
		processChanges();
	}

	// Process all change ranges.
	public void processChanges() {
		int thisParsedUpto = -1;
		System.out.printf("process_changes (lastVisibleLine: %d)\n", lastVisibleLine);
		for (Range range : changes) {
			if (range.end > thisParsedUpto && range.start <= lastVisibleLine + lookAhead) {
				int rangeEnd = Math.min(lastVisibleLine + lookAhead, range.end);
				thisParsedUpto = parseRange(range.start, rangeEnd);
			}
		}
		System.out.printf("%s\n", root.pretty(0));
		changes.ranges.clear();
	}

	// Parse from from_line to *at least* to_line. Will parse
	// more if necessary. Returns the index of the last line
	// parsed.
	private int parseRange(int fromLine, int toLine) {
		System.out.printf("parse_range(%d, %d)\n", fromLine, toLine);
		int lineIx = fromLine;
		boolean scopeChanged = false;
		boolean scopeEverChanged = false;
		int endLine = Math.min(lastVisibleLine + 100, mateText.getTextWidget().getLineCount() - 1);
		while (lineIx <= toLine || scopeEverChanged && lineIx <= endLine) {
			scopeChanged = parseLine(lineIx++);
			if (scopeChanged) {
				scopeEverChanged = true;
				// In the old scheme this wasn't necessary because 
				// the scope_at used a simple scan from the front. The GSequences
				// on the other hand can get confused if the later scopes
				// are inconsistent with earler ones. So we have to clear everything.
				// TODO: figure out a way to OPTIMIZE this again.
				root.clearAfter(lineIx, -1);
				removeColourAfter(lineIx, 0);
				this.parsedUpto = lineIx;
			}
			// stdout.printf("parse_line returned: %s\n", scope_changed ? "true" : "false");
			//stdout.printf("pretty:\n%s\n", root.pretty(2));
		}
		return toLine;
	}
	
	public void sleep(int ms) {
		try{
		  //do what you want to do before sleeping
		  Thread.currentThread().sleep(ms);//sleep for 1000 ms
		  //do what you want to do after sleeptig
		}
		catch(InterruptedException ie){
		//If this thread was intrrupted by
		}
	}
	
	private boolean parseLine(int lineIx) {
		String line = styledText.getLine(lineIx);
		int length = line.length();
		System.out.printf("p%d, ", lineIx);
		if (lineIx > this.parsedUpto)
			this.parsedUpto = lineIx;
		Scope startScope = this.root.scopeAt(lineIx, 0);
		if (startScope != null) {
			System.out.printf("startScope is: %s\n", startScope.name);
			startScope = startScope.containingDoubleScope(lineIx);
		}
		System.out.printf("startScope is: %s\n", startScope.name);
		Scope endScope1 = this.root.scopeAt(lineIx, Integer.MAX_VALUE);
		if (endScope1 != null)
			endScope1 = endScope1.containingDoubleScope(lineIx);
		 System.out.printf("endScope1: %s\n", endScope1.name);
		Scanner scanner = new Scanner(startScope, line);
		ArrayList<Scope> allScopes = new ArrayList<Scope>();
		allScopes.add(startScope);
		ArrayList<Scope> closedScopes = new ArrayList<Scope>();
		ArrayList<Scope> removedScopes = new ArrayList<Scope>();
		allScopes.add(startScope);
		for (Marker m : scanner) {
			sleep(500);
			Scope expectedScope = getExpectedScope(scanner.getCurrentScope(), lineIx, scanner.position);
			if (expectedScope != null)
				System.out.printf("expectedScope: %s (%d, %d)\n", expectedScope.name, expectedScope.startLoc().line, 
					           expectedScope.startLoc().lineOffset);
			else
				System.out.printf("no expected scope\n");
			System.out.printf("  scope: %s (%d, %d) (line length: %d)\n", 
								m.pattern.name, m.from, m.match.getCapture(0).end, length);
			if (m.isCloseScope) {
				System.out.printf("     (closing)\n");
				closeScope(scanner, expectedScope, lineIx, line, length, m, 
							allScopes, closedScopes, removedScopes);
			}
			else if (m.pattern instanceof DoublePattern) {
				System.out.printf("     (opening)\n");
				openScope(scanner, expectedScope, lineIx, line, length, m, 
						   allScopes, closedScopes, removedScopes);
			}
			else {
				System.out.printf("     (single)\n");
				singleScope(scanner, expectedScope, lineIx, line, length, m, 
							 allScopes, closedScopes, removedScopes);
			}
			System.out.printf("pretty:\n%s\n", root.pretty(2));
			scanner.position = m.match.getCapture(0).end;
		}
		clearLine(lineIx, startScope, allScopes, closedScopes, removedScopes);
		Scope endScope2 = this.root.scopeAt(lineIx, Integer.MAX_VALUE);
		if (endScope2 != null)
			endScope2 = endScope2.containingDoubleScope(lineIx);
		// System.out.printf("end_scope2: %s\n", endScope2.name);
		// System.out.printf("%s\n", this.root.pretty(0));
		if (colourer != null) {
			// System.out.printf("before_uncolour_scopes\n");
			colourer.uncolourScopes(removedScopes);
			// System.out.printf("before_colour_line_with_scopes\n");
			colourer.colourLineWithScopes(allScopes);
			// System.out.printf("after_colour_line_with_scopes\n");
		}
		else {
			// stdout.printf("no colourer");
		}
		return (endScope1 != endScope2);
	}

	public Scope getExpectedScope(Scope currentScope, int line, int lineOffset) {
		System.out.printf("get_expected_scope(%s, %d, %d)\n", currentScope.name, line, lineOffset);
		Scope expectedScope = currentScope.firstChildAfter(new TextLocation(line, lineOffset));
//		System.out.printf("first_child_after: %s\n", expectedScope.name);
		assert(expectedScope != currentScope);
		if (expectedScope != null) {
			if (expectedScope.startLine() != line)
				expectedScope = null;
			while (expectedScope != null && expectedScope.isCapture) {
				expectedScope = expectedScope.parent;
			}
		}
		return expectedScope;
	}
	private void removeColourAfter(int lineIx, int something) {
		// TODO: port this function
	}
	
	private void closeScope(Scanner scanner, Scope expectedScope, int lineIx, String line, 
							int length, Marker m, ArrayList<Scope> allScopes, 
							ArrayList<Scope> closedScopes, ArrayList<Scope> removedScopes) {
		// TODO: port this function
	}
	
	private void openScope(Scanner scanner, Scope expectedScope, int lineIx, String line, 
							int length, Marker m, ArrayList<Scope> allScopes, 
							ArrayList<Scope> closedScopes, ArrayList<Scope>removedScopes) {
		// TODO: port this function
	}

	public void setStartPosSafely(Scope scope, Marker m, int lineIx, int length, int cap) {
		int to = m.match.getCapture(cap).start;
		if (to == length && this.mateText.getTextWidget().getLineCount() > lineIx+1) 
			scope.setStartPos(lineIx+1, 0, false);
		else
			scope.setStartPos(lineIx, Math.min(to, length), false);
	}

	public void setEndPosSafely(Scope scope, Marker m, int lineIx, int length, int cap) {
		int to = m.match.getCapture(cap).end;
		if (to == length && this.mateText.getTextWidget().getLineCount() > lineIx+1) {
			scope.setEndPos(lineIx, length-1, true);
		}
		else {
			scope.setEndPos(lineIx, Math.min(to, length), true);
		}
	}
	
	private void singleScope(Scanner scanner, Scope expectedScope, int lineIx, 
							String line, int length, Marker m, 
							ArrayList<Scope> allScopes, ArrayList<Scope> closedScopes, 
							ArrayList<Scope> removedScopes) {
		Scope s = new Scope(this.mateText, m.pattern.name);
		s.pattern = m.pattern;
		s.openMatch = m.match;
		setStartPosSafely(s, m, lineIx, length, 0);
		setEndPosSafely(s, m, lineIx, length, 0);
		s.isOpen = false;
		s.isCapture = false;
		s.beginMatchString = line.substring(m.from, m.match.getCapture(0).end);
		// System.out.printf("_match_string: '%s'\n", s.beginMatch_Sring);
		s.parent = scanner.getCurrentScope();
		Scope newScope = s;
		if (expectedScope != null) {
			System.out.printf("aaa\n");
			if (s.surfaceIdenticalTo(expectedScope)) {
				newScope = expectedScope;
				for (Scope child : expectedScope.children) {
					closedScopes.add(child);
				}
			}
			else {
				handleCaptures(lineIx, length, line, s, m, allScopes, closedScopes);
				if (s.overlapsWith(expectedScope)) {
					// System.out.printf("%s overlaps with expected %s (current: %s)\n", s.name, expectedScope.name, scanner.currentScope.name);
					if (expectedScope == scanner.getCurrentScope()) {
						// we expected this scope to close, but it doesn't
					}
					else {
						scanner.getCurrentScope().removeChild(expectedScope);
						// removed_scopes << expectedScope
						removedScopes.add(expectedScope);
					}
				}
				scanner.getCurrentScope().addChild(s);
			}
		}
		else {
			System.out.printf("bbb\n");
			handleCaptures(lineIx, length, line, s, m, allScopes, closedScopes);
			scanner.getCurrentScope().addChild(s);
		}
		allScopes.add(newScope);
		closedScopes.add(newScope);
	}

	// Opens scopes for captures AND creates closing regexp from
	// captures if necessary.
	public void handleCaptures(int lineIx, int length, String line, 
			Scope scope, Marker m, ArrayList<Scope> allScopes, ArrayList<Scope> closedScopes) {
//		make_closing_regex(line, scope, m);
//		collect_child_captures(line_ix, length, scope, m, all_scopes, closed_scopes);
	}
	
	private void clearLine(int lineIx, Scope startScope, ArrayList<Scope> allScopes, 
							ArrayList<Scope> closedScopes, ArrayList<Scope> removedScopes) {
		// TODO: port this function
	}
}