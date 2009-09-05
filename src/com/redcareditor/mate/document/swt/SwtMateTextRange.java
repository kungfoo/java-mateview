package com.redcareditor.mate.document.swt;

import com.redcareditor.mate.document.MateTextLocation;
import com.redcareditor.mate.document.MateTextRange;

public class SwtMateTextRange implements MateTextRange {
	private SwtMateTextLocation start;
	private SwtMateTextLocation end;
	private SwtMateDocument document;

	public SwtMateTextRange(SwtMateDocument document) {
		this.document = document;
	}

	public SwtMateTextRange(MateTextLocation start, MateTextLocation end, SwtMateDocument document) {
		super();
		this.start = sanatize(start);
		this.end = sanatize(end);
		this.document = document;
	}

	public int getLength() {
		return end.getOffset() - start.getOffset();
	}

	public MateTextLocation getStart() {
		if (start != null) {
			return start;
		} else {
			return document.getTextLocation(0, 0);
		}
	}

	public void setStart(MateTextLocation location) {
		start = sanatize(location);
	}

	public MateTextLocation getEnd() {
		if (end != null) {
			return end;
		} else {// Return end of Document if not set
			int lastLine = document.getLineCount() - 1;
			int lastLineOffset = document.styledText.getCharCount() - document.styledText.getOffsetAtLine(lastLine);
			return document.getTextLocation(lastLine, lastLineOffset);
		}
	}

	public void setEnd(MateTextLocation location) {
		end = sanatize(location);
	}

	private SwtMateTextLocation sanatize(MateTextLocation location) {
		if (location instanceof SwtMateTextLocation) {
			return (SwtMateTextLocation) location;
		}
		return (SwtMateTextLocation) document.getTextLocation(location.getLine(), location.getLineOffset());
	}

	public boolean conatains(MateTextLocation location) {
		return getStart().compareTo(location) <= 0 && getEnd().compareTo(location) > 0;
	}

	public boolean overlaps(MateTextRange range) {
		if(getStart().compareTo(range.getStart()) >= 0){
			return getStart().compareTo(range.getEnd()) <= 0;
		}else{
			return getEnd().compareTo(range.getStart()) >= 0;
		}
	}
}
