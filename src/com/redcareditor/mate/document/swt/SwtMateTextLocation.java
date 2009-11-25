package com.redcareditor.mate.document.swt;

import org.eclipse.jface.text.Position;
import org.eclipse.swt.custom.StyledText;

import com.redcareditor.mate.document.MateDocument;
import com.redcareditor.mate.document.MateTextLocation;
import com.redcareditor.mate.document.MateTextLocationComparator;

public class SwtMateTextLocation extends Position implements MateTextLocation {

	private static final MateTextLocationComparator comperator = new MateTextLocationComparator();
	private SwtMateDocument document;

	public SwtMateTextLocation(int offset, SwtMateDocument document) {
		super(offset);
		this.document = document;
	}

	public SwtMateTextLocation(int line, int lineOffset, SwtMateDocument document) {
		super(computeOffset(line, lineOffset, document.styledText));
		this.document = document;
	}

	public SwtMateTextLocation(MateTextLocation location, SwtMateDocument document) {
		super(computeOffset(location.getLine(), location.getLineOffset(), document.styledText));
		this.document = document;
	}

	public void setDocument(MateDocument document) {
		this.document = (SwtMateDocument) document;
	}

	public int getLine() {
//		System.out.printf("getLine() (getOffset() = %d, charCount() = %d)\n", getOffset(), document.styledText.getCharCount());
		
		return document.styledText.getLineAtOffset(getOffset());
	}

	public int getLineOffset() {
		return getOffset() - document.styledText.getOffsetAtLine(getLine());
	}

	public int compareTo(MateTextLocation o) {
		return comperator.compare(this, o);
	}
	
	@Override
	public int getOffset() {
		
		return this.offset < document.styledText.getCharCount() ? this.offset : document.styledText.getCharCount();
	}
	
	private static int computeOffset(int line, int offset, StyledText text){
		line = line < 0 ? 0 : line;
		
		int result = text.getOffsetAtLine(line)+offset;
		
		result = result < 0 ? 0 : result;
		result = result > text.getCharCount() ? text.getCharCount() : result; 
		return result;
	}
	
	@Override
	public boolean equals(Object other) {
		if(other instanceof MateTextLocation){
			return compareTo((MateTextLocation) other) == 0;
		}
		return false;
	}
}
