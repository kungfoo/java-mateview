package com.redcareditor.mate.document.swt;

import org.eclipse.jface.text.Position;
import org.eclipse.swt.custom.StyledText;

import com.redcareditor.mate.document.MateTextLocation;
import com.redcareditor.mate.document.MateTextLocationComparator;

public class SwtTextLocation extends Position implements MateTextLocation {

	private static final MateTextLocationComparator comperator = new MateTextLocationComparator();
	private SwtMateDocument document;

	public SwtTextLocation(int line, int lineOffset, SwtMateDocument document) {
		super(computeOffset(line, lineOffset, document.styledText));
		this.document = document;
	}

	public SwtTextLocation(MateTextLocation location, SwtMateDocument document) {
		super(computeOffset(location.getLine(), location.getLineOffset(), document.styledText));
		this.document = document;
	}

	public int getLine() {
		return document.styledText.getLineAtOffset(getOffset());
	}

	public int getLineOffset() {
		return offset - document.styledText.getOffsetAtLine(getLine());
	}

	public int compareTo(MateTextLocation o) {
		return comperator.compare(this, o);
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
