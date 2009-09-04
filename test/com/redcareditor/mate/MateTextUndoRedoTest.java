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
	public void testCleanDocumentThenDirty(){
		checkNotDirty();
		text.replaceTextRange(0, 0, "hello");
		checkDirty();
		mateText.undo();
		checkNotDirty();
	}

	private void checkNotDirty() {
		assertFalse(mateText.isDirty());
	}

	private void checkDirty() {
		assertTrue(mateText.isDirty());
	}

	@Test
	public void testOneStepUndoRedo() {
		String string = "Hey, I entered this text just now!";
		text.replaceTextRange(0, 0, string);
		mateText.undo();
		checkTextIsEmpty();
		mateText.redo();
		assertEquals(string, text.getText());
		mateText.redo();
		assertEquals(string, text.getText());
		mateText.undo();
		checkTextIsEmpty();
	}

	private void checkTextIsEmpty() {
		assertEquals("", text.getText());
	}
	
	@Test
	public void testDoubleEntryUndoRedo(){
		String string1 = "I entered this some time ago\n";
		String string2 = "I entered this just now!";
		text.replaceTextRange(0, 0, string1);
		mateText.undo();
		checkTextIsEmpty();
		
		text.replaceTextRange(0, 0, string1);
		text.replaceTextRange(string1.length(), 0, string2);
		
		String combined = string1 + string2;
		assertEquals(combined, text.getText());
		mateText.undo();
		assertEquals(string1, text.getText());
		mateText.redo();
		assertEquals(combined, text.getText());
		mateText.undo();
		assertEquals(string1, text.getText());
		mateText.undo();
		checkTextIsEmpty();
		checkNotDirty();
		mateText.redo();
		mateText.redo();
		assertEquals(combined, text.getText());
	}
}
