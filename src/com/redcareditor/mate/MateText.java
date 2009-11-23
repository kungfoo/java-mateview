package com.redcareditor.mate;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.redcareditor.mate.colouring.Colourer;
import com.redcareditor.mate.colouring.swt.SwtColourer;
import com.redcareditor.mate.document.MateDocument;
import com.redcareditor.mate.document.MateTextLocation;
import com.redcareditor.mate.document.swt.SwtMateDocument;
import com.redcareditor.mate.undo.MateTextUndoManager;
import com.redcareditor.mate.undo.swt.SwtMateTextUndoManager;
import com.redcareditor.onig.NullRx;
import com.redcareditor.onig.Rx;
import com.redcareditor.theme.Theme;
import com.redcareditor.theme.ThemeManager;

public class MateText extends Composite {
	public Parser parser;
	public Colourer colourer;

	/* components plugged together */
	public SourceViewer viewer;
	private IDocument document;
	private CompositeRuler gutter;
	private LineNumberRulerColumn lineNumbers;
	private SwtMateDocument mateDocument;

	private MateTextUndoManager undoManager;
	private List<IGrammarListener> grammarListeners;

	public MateText(Composite parent) {
		super(parent, SWT.NONE);
		document = new Document();
		gutter = constructRuler();
		viewer = new SourceViewer(this, gutter, SWT.FULL_SELECTION | SWT.HORIZONTAL | SWT.VERTICAL);
		viewer.setDocument(document);
		setLayout(new FillLayout());
		colourer = new SwtColourer(this);
		undoManager = new SwtMateTextUndoManager(this);
		mateDocument = new SwtMateDocument(this);
		grammarListeners = new ArrayList<IGrammarListener>();
		getTextWidget().setLeftMargin(5);
	}

	private CompositeRuler constructRuler() {
		CompositeRuler ruler = new CompositeRuler(0);
		lineNumbers = new LineNumberRulerColumn();
		ruler.addDecorator(0, lineNumbers);
		return ruler;
	}

	public void undo() {
		undoManager.undo();
	}

	public void redo() {
		undoManager.redo();
	}

	public boolean isDirty() {
		return undoManager.isDirty();
	}

	public void attachUpdater() {

	}

	public StyledText getTextWidget() {
		return viewer.getTextWidget();
	}

	public IDocument getDocument() {
		return document;
	}

	public MateDocument getMateDocument() {
		return mateDocument;
	}

	public StyledText getControl() {
		return viewer.getTextWidget();
	}

	public boolean shouldColour() {
		return parser.shouldColour();
	}
	
	// Sets the grammar explicitly by name.
	// TODO: restore the uncolouring stuff
	public boolean setGrammarByName(String name) {
		if (this.parser != null && this.parser.grammar.name.equals(name))
			return true;

		for (Bundle bundle : Bundle.getBundles()) {
			for (Grammar grammar : bundle.getGrammars()) {
				if (grammar.name.equals(name)) {
					// int parsed_upto = 150;
					Theme theme;
					// if (this.parser != null) {
					// 	this.parser.close();
					// }
					this.parser = new Parser(grammar, this);
					getMateDocument().reparseAll();
					// this.parser.parseRange(0, getControl().getLineCount()-1);
					// this.parser.last_visible_line_changed(parsed_upto);
					// GLib.Signal.emit_by_name(this, "grammar_changed",
					// gr.name);
					// if (theme != null)
					// this.parser.change_theme(theme);
					for (IGrammarListener grammarListener : grammarListeners) {
						grammarListener.grammarChanged(grammar.name);
					}
					return true;
				}
			}
		}
		return false;
	}

	// Sets the grammar by the file extension. If unable to find
	// a grammar, sets the grammar to null. Returns the grammar
	// name or null.
	public String setGrammarByFilename(String fileName) {
		String bestName = null;
		long bestLength = 0;
		for (Bundle bundle : Bundle.getBundles()) {
			for (Grammar grammar : bundle.getGrammars()) {
				if (grammar.fileTypes != null) {
					for (String ext : grammar.fileTypes) {
						if (fileName.endsWith(ext) && (bestName == null || ext.length() > bestLength)) {
							bestName = grammar.name;
							bestLength = ext.length();
						}
					}
				}
			}
		}
		if (bestName != null) {
			if (this.parser == null || this.parser.grammar.name != bestName) {
				setGrammarByName(bestName);
			}
			return bestName;
		}
		return null;
	}

	// Sets the grammar by examining the first line. If unable to find
	// a grammar, sets the grammar to null. Returns the grammar
	// name or null.
	public String setGrammarByFirstLine(String firstLine) {
		Rx re;
		for (Bundle bundle : Bundle.getBundles()) {
			for (Grammar grammar : bundle.getGrammars()) {
				re = grammar.firstLineMatch;
				if (re instanceof NullRx) {
				} else {
					if (re.search(firstLine, 0, (int) firstLine.length()) != null) {
						System.out.printf("matched: %s '%s' %s\n", grammar.name, firstLine, re.pattern);
						setGrammarByName(grammar.name);
						return grammar.name;
					}
				}
			}
		}
		return null;
	}

	public boolean setThemeByName(String name) {
		for (Theme theme : ThemeManager.themes) {
			if (theme.name.equals(name)) {
				this.colourer.setTheme(theme);
				return true;
			}
		}
		return false;
	}

	public void setFont(String name, int size) {
		Font font = new Font(Display.getCurrent(), name, size, 0);
		viewer.getTextWidget().setFont(font);
		lineNumbers.setFont(font);
	}

	@SuppressWarnings("unchecked")
	public void setGutterBackground(Color color) {
		lineNumbers.setBackground(color);
	}

	public void setGutterForeground(Color color) {
		lineNumbers.setForeground(color);
	}
	
	public void addGrammarListener(IGrammarListener listener) {
		grammarListeners.add(listener);
	}
}
