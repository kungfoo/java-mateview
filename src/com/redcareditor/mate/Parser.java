package com.redcareditor.mate;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;

import com.redcareditor.onig.Range;

public class Parser {
	public Grammar grammar;
	public Colourer colourer;
	public MateText mateText;
	
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
//		this.root.start_mark_set(0, 0, true);
//		this.root.end_mark_set(buffer.end_iter().get_line(), buffer.end_iter().get_line_index(), false);
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
	
	private boolean parseLine(int lineIx) {
		return true;
	}

	private void removeColourAfter(int lineIx, int something) {
		
	}
}
