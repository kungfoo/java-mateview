package com.redcareditor.mate;

import static org.junit.Assert.*;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Test;


public class MateTextUndoRedoTest {
	private MateText mateText;
	private StyledText text;

	@Before
	public void setup(){
		Shell shell = new Shell();
		mateText = new MateText(new MateText(shell));
		text = mateText.getTextWidget();
	}
	
	@Test
	public void testOneStepUndoRedo(){
		String string = "Hey, I entered this text just now!";
		text.replaceTextRange(0, 0, string);
		mateText.undo();
		assertEquals("", text.getText());
		mateText.redo();
		assertEquals(string, text.getText());
	}
}
