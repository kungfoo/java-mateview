package com.redcareditor.mate;

import java.util.Stack;

import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.StyledText;

public class MateTextUndoManager implements ExtendedModifyListener {
	private Stack<UndoRedoStep> undoStack;
	private Stack<UndoRedoStep> redoStack;

	private StyledText styledText;

	public MateTextUndoManager(MateText matetext) {
		styledText = matetext.getTextWidget();
		undoStack = new Stack<UndoRedoStep>();
		redoStack = new Stack<UndoRedoStep>();
		styledText.addExtendedModifyListener(this);
	}
	
	/**
	 * this method will get called once the text widget is modified.
	 */
	public void modifyText(ExtendedModifyEvent e) {
		String currentText = styledText.getText();
		String newText = currentText.substring(e.start, e.start + e.length);
		
		if(isTextReplaceEvent(e)){
			undoStack.push(new ReplaceUndoRedoStep(e.start, e.replacedText));
		}
		if(isTextEntryEvent(newText)){
			undoStack.push(new EntryUndoRedoStep(e.start, newText));
		}
	}
	
	public void undo(){
		if(!undoStack.isEmpty()){
			undoStack.pop().undo();
		}
	}
	
	public void redo(){
		if(!redoStack.isEmpty()){
			redoStack.pop().redo();
		}
	}
	
	private boolean isTextEntryEvent(String newText) {
		return newText != null && newText.length() > 0;
	}

	private boolean isTextReplaceEvent(ExtendedModifyEvent e) {
		return e.replacedText != null && e.replacedText.length() > 0;
	}

	
	/* here come the classes that are on the stacks... */
	private abstract class UndoRedoStep {
		public int location;
		public String data;

		public UndoRedoStep(int location, String data) {
			super();
			this.location = location;
			this.data = data;
		}
		
		public abstract void undo();
		public abstract void redo();
	}

	private class ReplaceUndoRedoStep extends UndoRedoStep {
		public ReplaceUndoRedoStep(int location, String data) {
			super(location, data);
		}

		public void redo() {
			
		}

		public void undo() {
			
		}
	}

	private class EntryUndoRedoStep extends UndoRedoStep {
		public EntryUndoRedoStep(int location, String data) {
			super(location, data);
		}

		public void redo() {
			
		}

		public void undo() {
			
		}
	}
}
