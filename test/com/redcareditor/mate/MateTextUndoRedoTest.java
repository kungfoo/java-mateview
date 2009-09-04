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
	public void setup() {
		Shell shell = new Shell();
		mateText = new MateText(new MateText(shell));
		text = mateText.getTextWidget();
	}

	@Test
	public void testOneStepUndoRedo() {
		String string = "Hey, I entered this text just now!";
		text.replaceTextRange(0, 0, string);
		mateText.undo();
		assertEquals("", text.getText());
		mateText.redo();
		assertEquals(string, text.getText());
		mateText.redo();
		assertEquals(string, text.getText());
	}
	
	@Test
	public void testDoubleEntryUndoRedo(){
		String string1 = "I entered this some time ago\n";
		String string2 = "I entered this just now!";
		text.replaceTextRange(0, 0, string1);
		mateText.undo();
		assertEquals("", text.getText());
		
		text.replaceTextRange(0, 0, string1);
		text.replaceTextRange(string1.length(), 0, string2);
		assertEquals(string1 + string2, text.getText());
		mateText.undo();
		assertEquals(string1, text.getText());
		mateText.redo();
		assertEquals(string1 + string2, text.getText());
	}
}
