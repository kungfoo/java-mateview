package com.redcareditor.mate.document.swt;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultPositionUpdater;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.text.Position;
import org.eclipse.swt.custom.StyledText;

import com.redcareditor.mate.MateText;
import com.redcareditor.mate.document.MateDocument;
import com.redcareditor.mate.document.MateTextFactory;
import com.redcareditor.mate.document.MateTextLocation;
import com.redcareditor.mate.document.MateTextRange;

public class SwtMateDocument implements MateDocument, MateTextFactory {
	private MateText mateText;
	public StyledText styledText;
	private IPositionUpdater positionUpdater;

	public SwtMateDocument(MateText mateText) {
		this.mateText = mateText;
		this.styledText = mateText.getTextWidget();
//		this.positionUpdater = new DefaultPositionUpdater(Document.DEFAULT_CATEGORY);
//		this.mateText.getDocument().addPositionUpdater(this.positionUpdater);
		this.mateText.getDocument().addDocumentListener(new IDocumentListener() {
			public void documentAboutToBeChanged(DocumentEvent event) {
				System.out.printf("DocumentEvent caught in documentAboutToBeChanged\n");
			}
			public void documentChanged(DocumentEvent event) {
				documentChangedCallback(event);
			}
		});
	}

	public void documentChangedCallback(DocumentEvent event) {
		System.out.printf("DocumentEvent caught in documentChanged: '%s'\n", event.getText());
	}
	
	public void replace(int start, int length, String text) {
		System.out.printf("calling replace on SwtMateDocument(%d, %d, '%s');\n", start, length, text);
		try {
			this.mateText.getDocument().replace(start, length, text);
			SwtTextLocation startLocation = new SwtTextLocation(start, this);
			SwtTextLocation endLocation = new SwtTextLocation(start + length, this);
			this.mateText.parser.changes.add(startLocation.getLine(), endLocation.getLine());
			this.mateText.parser.processChanges();
		}
		catch (BadLocationException e) {
			// TODO: SwtMateDocument should throw it's own Exception here
		}
	}
	
	public boolean addTextLocation(MateTextLocation location) {
//		SwtTextLocation position = new SwtTextLocation(location, this);

		try {
			mateText.getDocument().addPosition((SwtTextLocation) location);
			return true;
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
//		catch (BadPositionCategoryException e) {
//			e.printStackTrace();
//		}

		return false;
	}

	public int getLineCount() {
		return styledText.getLineCount();
	}

	public int getLineLength(int line) {
		int startOffset = styledText.getOffsetAtLine(line);
		int endOffset;

		if (line + 1 < getLineCount()) {
			endOffset = styledText.getOffsetAtLine(line + 1);
		} else {
			endOffset = styledText.getCharCount();
		}

		return endOffset - startOffset;
	}

	public MateTextLocation getTextLocation(int line, int offset) {
		return new SwtTextLocation(line, offset, this);
	}

	public MateTextRange getTextRange(MateTextLocation start, MateTextLocation end) {
		return new SwtTextRange(start, end, this);
	}

	public MateTextRange getTextRange() {
		return new SwtTextRange(this);
	}
}
