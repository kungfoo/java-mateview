package com.redcareditor.mate.document.swt;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Position;
import org.eclipse.swt.custom.StyledText;

import com.redcareditor.mate.MateText;
import com.redcareditor.mate.document.MateDocument;
import com.redcareditor.mate.document.MateTextLocation;
import com.redcareditor.mate.document.MateTextFactory;
import com.redcareditor.mate.document.MateTextRange;

public class SwtMateDocument implements MateDocument, MateTextFactory {
	private MateText mateText;
	public StyledText styledText;
	
	
	
	public SwtMateDocument(MateText mateText) {
		this.mateText = mateText;
		this.styledText = mateText.getTextWidget();
	}

	public boolean addTextLocation(MateTextLocation location) {
		Position position = new SwtTextLocation(location,this);
		
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

	public MateTextLocation getTextLocation(int line, int offset) {
		return new SwtTextLocation(line,offset,this);
	}
	
	public MateTextRange getTextRange(MateTextLocation start,
			MateTextLocation end) {
		return new SwtTextRange(start,end,this);
	}
	
	public MateTextRange getTextRange() {
		return new SwtTextRange(this);
	}
}
