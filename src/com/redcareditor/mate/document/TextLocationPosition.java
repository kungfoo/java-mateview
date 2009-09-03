package com.redcareditor.mate.document;

import org.eclipse.jface.text.Position;
import org.eclipse.swt.custom.StyledText;

import com.redcareditor.mate.TextLocation;

public class TextLocationPosition extends Position {
	StyledText styledText;
	TextLocation location;
	
	public TextLocationPosition(int offset,TextLocation location,StyledText styledText) {
		super(offset);
		this.location = location;
		this.styledText = styledText;
	}
	
	@Override
	public void setOffset(int offset) {
		location.line = styledText.getLineAtOffset(offset);
		location.lineOffset = offset - styledText.getOffsetAtLine(location.line);
		super.setOffset(offset);
	}
}
