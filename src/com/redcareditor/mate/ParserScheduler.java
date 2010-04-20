package com.redcareditor.mate;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;

import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IViewportListener;
import org.eclipse.jface.text.JFaceTextUtil;
import org.eclipse.jface.text.DocumentEvent;

import com.redcareditor.mate.document.swt.SwtMateTextLocation;
import com.redcareditor.onig.Range;

public class ParserScheduler {
	int LOOK_AHEAD = 100;
	public static boolean synchronousParsing = false;
	public int lookAhead;
	public int lastVisibleLine;
	public boolean alwaysParseAll;
	public boolean enabled;
	public RangeSet changes;
	public int deactivationLevel;

	public int modifyStart, modifyEnd;
	public String modifyText;
	public ParseThunk thunk;
	public Parser parser;

	public int parsed_upto;	
	public SwtMateTextLocation parsedUpto;

	public ParserScheduler(Parser parser) {
		this.parser = parser;
		lookAhead = LOOK_AHEAD;
		lastVisibleLine = 0;
		changes = new RangeSet();
		deactivationLevel = 0;
		alwaysParseAll = false;
		modifyStart = -1;
		enabled = true;
		setParsedUpto(0);
		attachListeners();
	}
	
	public void close() {
		if (thunk != null) {
			thunk.stop();
			thunk = null;
		}
		removeListeners();
	}

	private VerifyListener verifyListener;
	private ModifyListener modifyListener;
	private IViewportListener viewportListener;
	private IDocumentListener documentListener;
	
	public void attachListeners() {
		viewportListener = new IViewportListener() {
			public void viewportChanged(int verticalOffset) {
				viewportScrolledCallback();
			}
		};
		
		parser.mateText.viewer.addViewportListener(viewportListener);
		
		documentListener = new IDocumentListener() {
			public void documentAboutToBeChanged(DocumentEvent e) {
				verifyEventCallback(e.fOffset, e.fOffset + e.fLength, e.fText);
			}
			
			public void documentChanged(DocumentEvent e) {
				modifyEventCallback();
			}
		};
		parser.mateText.getDocument().addDocumentListener(documentListener);
	}
	
	public void removeListeners() {
		parser.mateText.viewer.removeViewportListener(viewportListener);
		parser.mateText.getDocument().removeDocumentListener(documentListener);
	}

	public void verifyEventCallback(int start, int end, String text) {
		if (enabled) {
			modifyStart = start;
			modifyEnd   = end;
			modifyText  = text;
		}
	}
	
	public void modifyEventCallback() {
		if (enabled) {
			changes.add(
				parser.getLineAtOffset(modifyStart), 
				parser.getLineAtOffset(modifyStart + modifyText.length())
			);
			modifyStart = -1;
			modifyEnd   = -1;
			modifyText  = null;
			if (deactivationLevel == 0)
				processChanges();
		}
	}

	public void viewportScrolledCallback() {
		if (enabled) {
			lastVisibleLineChanged(JFaceTextUtil.getBottomIndex(parser.mateText.getTextWidget()));
		}
	}
	
	public void deactivate() {
		deactivationLevel++;
	}
	
	public void reactivate() {
		deactivationLevel--;
		if (deactivationLevel < 0) 
			deactivationLevel = 0;
		if (deactivationLevel == 0)
			processChanges();
	}
	
	// Process all change ranges.
	public void processChanges() {
		int thisParsedUpto = -1;
		// System.out.printf("process_changes (lastVisibleLine: %d) (charCount = %d)\n", lastVisibleLine, document.getLength());
		for (Range range : changes) {
			if (range.end > thisParsedUpto && range.start <= lastVisibleLine + lookAhead) {
				int rangeEnd = Math.min(lastVisibleLine + lookAhead, range.end);
				thisParsedUpto = parseRange(range.start, rangeEnd);
			}
			int startOffset = parser.getOffsetAtLine(range.start);
			int endOffset   = parser.getOffsetAtLine(range.end);
			parser.styledText.redrawRange(startOffset, endOffset - startOffset, false);
		}
		//		System.out.printf("%s\n", root.pretty(0));
		changes.ranges.clear();
	}

	// Parse from from_line to *at least* to_line. Will parse
	// more if necessary. Returns the index of the last line
	// parsed.
	public int parseRange(int fromLine, int toLine) {
		// System.out.printf("parse_range(%d, %d)\n", fromLine, toLine);

		int lineIx = fromLine;
		boolean scopeChanged = false;
		boolean scopeEverChanged = false;
		while (lineIx <= toLine) {
			scopeChanged = parser.parseLine(lineIx);
			if (scopeChanged) {
				scopeEverChanged = true;
				setParsedUpto(lineIx);
			}
			lineIx++;
		}
		if (thunk != null) {
			thunk.delayAndUpdate(lineIx);
		}
		else {
			if (scopeEverChanged && lineIx <= parser.getLineCount() - 1) {
				thunk = new ParseThunk(parser, lineIx);
				if (ParserScheduler.synchronousParsing) {
					thunk.execute();
				}
			}
		}

		return toLine;
	}
	
	public void parseOnwards(int fromLine) {
		// The widget can be disposed between the Thunk being created and being executed.
		if (parser.styledText.isDisposed())
			return;
		int lineIx = fromLine;
		int lineCount = parser.getLineCount();
		int lastLine = Math.min(lastVisibleLine + 100, lineCount - 1);
		while (lineIx <= lastLine) {
			parser.parseLine(lineIx);
			setParsedUpto(lineIx);
			parser.redrawLine(lineIx);
			lineIx++;
		}
	}
	
	public void lastVisibleLineChanged(int newLastVisibleLine) {
		//System.out.printf("lastVisibleLineChanged(%d)\n", newLastVisibleLine);
		this.lastVisibleLine = newLastVisibleLine;
		// System.out.printf("lastVisibleLine: %d, lookAhead: %d, getParsedUpto: %d\n", lastVisibleLine, lookAhead, getParsedUpto());
		if (lastVisibleLine + lookAhead >= getParsedUpto()) {
			int endRange = Math.min(parser.getLineCount() - 1, lastVisibleLine + lookAhead);
			parseRange(getParsedUpto(), endRange);
		}
	}
	
	public void setParsedUpto(int line_ix) {
		if (parsedUpto == null) {
			parsedUpto = (SwtMateTextLocation) parser.mateDocument.getTextLocation(0, 0);
			parser.mateDocument.addTextLocation("lefts", parsedUpto);
		}
		parsedUpto.offset = parser.getOffsetAtLine(line_ix);
	}
	
	public int getParsedUpto() {
		// System.out.printf("parsedUpto %d,%d (/%d)\n", parsedUpto.getOffset(), parsedUpto.getLength(), parser.getCharCount());
		return parser.getLineAtOffset(parsedUpto.getOffset());
	}
	
}