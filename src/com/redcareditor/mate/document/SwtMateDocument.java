package com.redcareditor.mate.document;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Position;
import org.eclipse.swt.custom.StyledText;

import com.redcareditor.mate.MateText;
import com.redcareditor.mate.TextLocation;

public class SwtMateDocument implements MateDocument {
	private MateText mateText;
	private StyledText styledText;
	
	
	
	public SwtMateDocument(MateText mateText) {
		this.mateText = mateText;
		this.styledText = mateText.getTextWidget();
	}

	public boolean addTextLocation(TextLocation location) {
		sanatizeTextLocaition(location);
		Position position = convertTextLocation(location);
		
		try {
			mateText.getDocument().addPosition(position);
			return true;
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}

	public int getLineCount() {
		return styledText.getLineCount();
	}

	public int getLineLength(int line) {
		int startOffset = styledText.getOffsetAtLine(line);
		int endOffset;
		
		if(line+1 < getLineCount()){
			endOffset = styledText.getOffsetAtLine(line+1);
		}else{
			endOffset = styledText.getCharCount();
		}
		
		return endOffset - startOffset;
	}

	private void sanatizeTextLocaition(TextLocation location){
		if(location.line >= getLineCount()){
			location.line = getLineCount()-1;
		}
		
		if(location.lineOffset >= getLineLength(location.line)){
			location.lineOffset = getLineLength(location.line)-1;
		}
	}
	
	private Position convertTextLocation(TextLocation location){
		int line = location.line;
		int offset = location.lineOffset;
		return new TextLocationPosition(styledText.getOffsetAtLine(line)+offset,location,styledText);
	}
	
}
