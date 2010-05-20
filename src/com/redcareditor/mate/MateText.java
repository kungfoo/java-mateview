package com.redcareditor.mate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Handler;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

import org.eclipse.jface.text.*;
import org.eclipse.jface.text.source.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.*;

import com.redcareditor.mate.colouring.Colourer;
import com.redcareditor.mate.colouring.swt.SwtColourer;
import com.redcareditor.mate.document.MateDocument;
import com.redcareditor.mate.document.MateTextLocation;
import com.redcareditor.mate.document.swt.SwtMateTextLocation;
import com.redcareditor.mate.document.swt.SwtMateDocument;
import com.redcareditor.mate.undo.MateTextUndoManager;
import com.redcareditor.mate.undo.swt.SwtMateTextUndoManager;
import com.redcareditor.mate.WhitespaceCharacterPainter;
import com.redcareditor.mate.LineNumberRulerColumn;
import com.redcareditor.onig.NullRx;
import com.redcareditor.onig.Rx;
import com.redcareditor.theme.Theme;
import com.redcareditor.theme.ThemeManager;
import com.redcareditor.util.SingleLineFormatter;

public class MateText {
	public Parser parser;
	public Colourer colourer;
	public Logger logger;

	static private Handler _consoleHandler;
	static public Handler consoleHandler() {
		if (_consoleHandler == null) {
			_consoleHandler = new ConsoleHandler();
			_consoleHandler.setFormatter(new SingleLineFormatter());
		}
		return _consoleHandler;
	}

	/* components plugged together */
	public SourceViewer viewer;
	private IDocument document;
	private CompositeRuler compositeRuler;
	private OverviewRuler overviewRuler;
    private AnnotationRulerColumn annotationRuler;
	private LineNumberRulerColumn lineNumbers;
	private SwtMateDocument mateDocument;

	private MateTextUndoManager undoManager;
	private List<IGrammarListener> grammarListeners;
	
	private boolean singleLine;
	private WhitespaceCharacterPainter whitespaceCharacterPainter;
    private boolean showingInvisibles;
    
    public static String ERROR_TYPE = "error.type";
    public static Image ERROR_IMAGE;
    public static final RGB ERROR_RGB = new RGB(255, 0, 0);

    // annotation model
    private AnnotationModel fAnnotationModel = new AnnotationModel();
    private IAnnotationAccess fAnnotationAccess;
    private ColorCache cc;
    
	public MateText(Composite parent) {
		this(parent, false);
	}

	public MateText(Composite parent, boolean thisSingleLine) {
		singleLine = thisSingleLine;
		document = new Document();
		if (singleLine)
			createSingleLineSourceViewer(parent);
		else
			createSourceViewer(parent);
		
		whitespaceCharacterPainter = new WhitespaceCharacterPainter(viewer);
		showingInvisibles = false;
		
		colourer = new SwtColourer(this);
		mateDocument = new SwtMateDocument(this);
		grammarListeners = new ArrayList<IGrammarListener>();
		getTextWidget().setLeftMargin(5);
		logger = Logger.getLogger("JMV.MateText");
		logger.setUseParentHandlers(false);
		logger.setLevel(Level.SEVERE);
		for (Handler h : logger.getHandlers()) {
			logger.removeHandler(h);
		}
		logger.addHandler(MateText.consoleHandler());
		logger.info("Created MateText");
	}
	
	private void createSingleLineSourceViewer(Composite parent) {
		viewer = new SourceViewer(parent, null, SWT.FULL_SELECTION | SWT.HORIZONTAL | SWT.VERTICAL | SWT.SINGLE);
		viewer.setDocument(document);
	}
	
	private void createSourceViewer(Composite parent) {
		ERROR_IMAGE = new Image(Display.getDefault(), "/Users/danlucraft/Desktop/little-star.png");

		fAnnotationAccess = new AnnotationMarkerAccess();

		cc = new ColorCache();

		compositeRuler = new CompositeRuler();
		overviewRuler = new OverviewRuler(fAnnotationAccess, 12, cc);
		annotationRuler = new AnnotationRulerColumn(fAnnotationModel, 16, fAnnotationAccess);
		compositeRuler.setModel(fAnnotationModel);
		overviewRuler.setModel(fAnnotationModel);

		// add what types are show on the different rulers
		annotationRuler.addAnnotationType(ERROR_TYPE);
		overviewRuler.addAnnotationType(ERROR_TYPE);
		overviewRuler.addHeaderAnnotationType(ERROR_TYPE);
		// set what layer this type is on
		overviewRuler.setAnnotationTypeLayer(ERROR_TYPE, 3);
		// set what color is used on the overview ruler for the type
		overviewRuler.setAnnotationTypeColor(ERROR_TYPE, new Color(Display.getDefault(), ERROR_RGB));

		lineNumbers = new LineNumberRulerColumn();
		compositeRuler.addDecorator(0, lineNumbers);
		compositeRuler.addDecorator(0, annotationRuler);

		viewer = new SourceViewer(parent, compositeRuler, SWT.FULL_SELECTION | SWT.HORIZONTAL | SWT.VERTICAL);
		viewer.setDocument(document, fAnnotationModel);
		
		// hover manager that shows text when we hover
		AnnotationHover ah = new AnnotationHover();
		AnnotationConfiguration ac = new AnnotationConfiguration();
		AnnotationBarHoverManager fAnnotationHoverManager = new AnnotationBarHoverManager(compositeRuler, viewer, ah, ac);
		fAnnotationHoverManager.install(annotationRuler.getControl());

		// to paint the annotations
		AnnotationPainter ap = new AnnotationPainter(viewer, fAnnotationAccess);
		ap.addAnnotationType(ERROR_TYPE);
		ap.setAnnotationTypeColor(ERROR_TYPE, new Color(Display.getDefault(), ERROR_RGB));

		// this will draw the squigglies under the text
		viewer.addPainter(ap);

		viewer.configure(new CodeViewerConfiguration(cc));

		// some misspelled text
		document.set("Here's some texst so that we have somewhere to show an error");

		// add an annotation
		ErrorAnnotation errorAnnotation = new ErrorAnnotation(1, "Learn how to spell \"text!\"");

	// lets underline the word "texst"
		fAnnotationModel.addAnnotation(errorAnnotation, new Position(12, 5));
	}

	public boolean isSingleLine() {
		return singleLine;
	}
	
	public void showInvisibles(boolean should) {
		if (should) {
			showingInvisibles = true;
			viewer.addPainter(whitespaceCharacterPainter);
		} else {
			showingInvisibles = false;
			viewer.removePainter(whitespaceCharacterPainter);
		}
	}
	
	public boolean isShowingInvisibles() {
		return showingInvisibles;
	}

	public void attachUpdater() {

	}
	
	public boolean getWordWrap() {
		return getTextWidget().getWordWrap();
	}
	
	public void setWordWrap(boolean val) {
		getTextWidget().setWordWrap(val);
	}

	public String grammarName() {
		return parser.grammar.name;
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
	
	public String scopeAt(int line, int line_offset) {
		return parser.root.scopeAt(line, line_offset).hierarchyNames(true);
	}
	
	// Sets the grammar explicitly by name.
	// TODO: restore the uncolouring stuff
	public boolean setGrammarByName(String name) {
		// System.out.printf("setGrammarByName(%s)\n", name);
		if (this.parser != null && this.parser.grammar.name.equals(name))
			return true;

		for (Bundle bundle : Bundle.getBundles()) {
			for (Grammar grammar : bundle.getGrammars()) {
				if (grammar.name.equals(name)) {
					if (this.parser != null) {
						this.parser.close();
					}
					this.parser = new Parser(grammar, this);
					if (colourer != null) {
						colourer.setGlobalColours();
					}
					getMateDocument().reparseAll();
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
	
	public void removeGrammarListener(IGrammarListener listener) {
		grammarListeners.remove(listener);
	}
	
	public void redraw() {
		// SwtMateTextLocation startLocation = new SwtMateTextLocation(0, getMateDocument());
		// SwtMateTextLocation endLocation = new SwtMateTextLocation(0 + getTextWidget().getCharCount(), getMateDocument());
		getTextWidget().redraw();
	}
	
	class AnnotationConfiguration implements IInformationControlCreator {
		public IInformationControl createInformationControl(Shell shell) {
			return new DefaultInformationControl(shell);
		}
	}

	class ColorCache implements ISharedTextColors {
		public Color getColor(RGB rgb) {
			return new Color(Display.getDefault(), rgb);
		}

		public void dispose() {
		}
	}

	// santa's little helper
	class AnnotationMarkerAccess implements IAnnotationAccess, IAnnotationAccessExtension {
		public Object getType(Annotation annotation) {
			return annotation.getType();
		}

		public boolean isMultiLine(Annotation annotation) {
			return true;
		}

		public boolean isTemporary(Annotation annotation) {
			return !annotation.isPersistent();
		}

		public String getTypeLabel(Annotation annotation) {
			if (annotation instanceof ErrorAnnotation)
				return "Errors";

			return null;
		}

		public int getLayer(Annotation annotation) {
			if (annotation instanceof ErrorAnnotation)
				return ((ErrorAnnotation)annotation).getLayer();

			return 0;
        }

		public void paint(Annotation annotation, GC gc, Canvas canvas, Rectangle bounds) {
			ImageUtilities.drawImage(((ErrorAnnotation)annotation).getImage(), gc, canvas, bounds, SWT.CENTER, SWT.TOP);
		}

		public boolean isPaintable(Annotation annotation) {
			if (annotation instanceof ErrorAnnotation)
				return ((ErrorAnnotation)annotation).getImage() != null;

			return false;
		}

		public boolean isSubtype(Object annotationType, Object potentialSupertype) {
			if (annotationType.equals(potentialSupertype))
				return true;

			return false;

		}

		public Object[] getSupertypes(Object annotationType) {
			return new Object[0];
		}
	}

	// source viewer configuration
	class CodeViewerConfiguration extends SourceViewerConfiguration {
		private ColorCache manager;
		
		public CodeViewerConfiguration(ColorCache manager) {
			this.manager = manager;
		}
		
		public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
			PresentationReconciler reconciler = new PresentationReconciler();
			return reconciler;
		}
		
		public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
			return new AnnotationHover();
		}
	}

    // annotation hover manager
	class AnnotationHover implements IAnnotationHover, ITextHover {
		public String getHoverInfo(ISourceViewer sourceViewer, int lineNumber) {
			Iterator ite = fAnnotationModel.getAnnotationIterator();
			
			ArrayList all = new ArrayList();
			
			while (ite.hasNext()) {
				Annotation a = (Annotation) ite.next();
				if (a instanceof ErrorAnnotation) {
					all.add(((ErrorAnnotation)a).getText());
				}
			}
			
			StringBuffer total = new StringBuffer();
			for (int x = 0; x < all.size(); x++) {
				String str = (String) all.get(x);
				total.append(" " + str + (x == (all.size()-1) ? "" : "\n"));
			}
			
			return total.toString();
		}
		
		public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
			return null;
		}
		
		public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
			return null;
		}
	}

	// one error annotation
	class ErrorAnnotation extends Annotation {
		private IMarker marker;
		private String text;
		private int line;
		private Position position;
		
		public ErrorAnnotation(IMarker marker) {
			this.marker = marker;
		}
		
		public ErrorAnnotation(int line, String text) {
			super(ERROR_TYPE, true, null);
			this.marker = null;
			this.line = line;
			this.text = text;
		}
		
		public IMarker getMarker() {
			return marker;
		}
		
		public int getLine() {
			return line;
		}
		
		public String getText() {
			return text;
		}
		
		public Image getImage() {
			return ERROR_IMAGE;
		}
		
		public int getLayer() {
			return 3;
		}
		
		public String getType() {
			return ERROR_TYPE;
		}
		
		public Position getPosition() {
			return position;
		}
		
		public void setPosition(Position position) {
			this.position = position;
		}
	}
}
