package com.redcareditor.mate;

import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Test;


public class MateTextUndoRedoTest {
	private MateText mateText;

	@Before
	public void setup(){
		Shell shell = new Shell();
		mateText = new MateText(new MateText(shell));
	}
	
	@Test
	public void testOneStepUndoRedo(){
		
	}
}
