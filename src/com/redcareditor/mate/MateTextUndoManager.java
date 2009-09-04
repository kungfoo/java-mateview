package com.redcareditor.mate;

import java.util.Stack;

import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.StyledText;

/**
 * this class can be attached to {@link MateText} widgets to provide undo/redo.<br>
 * It will plug into the event handling of the text editing widget of
 * {@link MateText} which is currently a {@link StyledText} inside a
 * {@link SourceViewer}.
 */
public class MateTextUndoManager implements ExtendedModifyListener {
	// TODO: maybe these stacks needs limits, if we want unlimited undo/redo, there you go...
	private Stack<UndoRedoStep> undoStack;
	private Stack<UndoRedoStep> redoStack;
	private StyledText styledText;

	/**
	 * this is needed so that undo/redo can disattach/reattach the listener.
	 */
	private MateTextUndoManager self;

	public MateTextUndoManager(MateText matetext) {
		styledText = matetext.getTextWidget();
		undoStack = new Stack<UndoRedoStep>();
		redoStack = new Stack<UndoRedoStep>();
		self = this;
		styledText.addExtendedModifyListener(this);
	}

	/**
	 * this method will get called once the text widget is modified.
	 */
	public void modifyText(ExtendedModifyEvent e) {
		String currentText = styledText.getText();
		String newText = currentText.substring(e.start, e.start + e.length);

		if (isTextReplaceEvent(e)) {
			System.out.println("pushing replaceEvent : " + e.replacedText);
			undoStack.push(new ReplaceUndoRedoStep(e.start, e.replacedText));
		}
		if (isTextEntryEvent(newText)) {
			System.out.println("pushing EntryEvent : " + newText);
			undoStack.push(new EntryUndoRedoStep(e.start, newText));
		}
	}

	public void undo() {
		if (!undoStack.isEmpty()) {
			undoStack.pop().undo();
		}
	}

	public void redo() {
		if (!redoStack.isEmpty()) {
			redoStack.pop().redo();
		}
	}

	public boolean isDirty() {
		return !undoStack.empty();
	}

	private boolean isTextEntryEvent(String newText) {
		return newText != null && newText.length() > 0;
	}

	private boolean isTextReplaceEvent(ExtendedModifyEvent e) {
		return e.replacedText != null && e.replacedText.length() > 0;
	}

	
	
	
	
	/*
	 * these are private classes, because the outside world doesn't need or
	 * understand them. Plus we can easily access and juggle around the instance
	 * variables from here.
	 */
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
	
	/**
	 * When text is replaced or deleted. Deleting is considered replacing it with ''
	 */
	private class ReplaceUndoRedoStep extends UndoRedoStep {
		public ReplaceUndoRedoStep(int location, String data) {
			super(location, data);
		}

		public void redo() {

		}

		public void undo() {
			String changedText = styledText.getText().substring(location, location + data.length());
			redoStack.push(new EntryUndoRedoStep(location, changedText));
			styledText.removeExtendedModifyListener(self);
			styledText.replaceTextRange(location, data.length(), "");
			styledText.setCaretOffset(location + data.length());
			styledText.addExtendedModifyListener(self);
		}
	}

	/**
	 * Represents text that has been entered.
	 */
	private class EntryUndoRedoStep extends UndoRedoStep {
		public EntryUndoRedoStep(int location, String data) {
			super(location, data);
		}

		public void redo() {

		}

		public void undo() {
			redoStack.push(new ReplaceUndoRedoStep(location, data));
			styledText.removeExtendedModifyListener(self);
			styledText.replaceTextRange(location, 0, data);
			styledText.setCaretOffset(location + data.length());
			styledText.addExtendedModifyListener(self);
		}
	}
}
