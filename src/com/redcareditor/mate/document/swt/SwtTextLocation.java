package com.redcareditor.mate.document.swt;

import org.eclipse.jface.text.Position;
import org.eclipse.swt.custom.StyledText;

import com.redcareditor.mate.document.MateTextLocation;
import com.redcareditor.mate.document.MateTextLocationComparator;

public class SwtTextLocation extends Position implements MateTextLocation {

	private static final MateTextLocationComparator comperator = new MateTextLocationComparator();
	private SwtMateDocument document;

	public SwtTextLocation(int offset, SwtMateDocument document) {
		super(offset);
		this.document = document;
	}

	public SwtTextLocation(int line, int lineOffset, SwtMateDocument document) {
		super(computeOffset(line, lineOffset, document.styledText));
		this.document = document;
	}

	public SwtTextLocation(MateTextLocation location, SwtMateDocument document) {
		super(computeOffset(location.getLine(), location.getLineOffset(), document.styledText));
		this.document = document;
	}

	public int getLine() {
//		System.out.printf("getLine() (getOffset() = %d, charCount() = %d)\n", getOffset(), document.styledText.getCharCount());
		return document.styledText.getLineAtOffset(getOffset());
	}

	public int getLineOffset() {
		return offset - document.styledText.getOffsetAtLine(getLine());
	}

	public int compareTo(MateTextLocation o) {
		return comperator.compare(this, o);
	}

	private static int computeOffset(int line, int offset, StyledText text) {
		return text.getOffsetAtLine(line) + offset;
	}
}
