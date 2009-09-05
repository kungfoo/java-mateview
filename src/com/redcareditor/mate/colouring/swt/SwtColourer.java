package com.redcareditor.mate.colouring.swt;

import java.util.List;

import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;

import com.redcareditor.mate.MateText;
import com.redcareditor.mate.Scope;
import com.redcareditor.mate.colouring.Colourer;
import com.redcareditor.theme.Theme;
import com.redcareditor.util.swt.ColourUtil;

public class SwtColourer implements Colourer {
	private Theme theme;
	private MateText mateText;

	private int highlightedLine = 0;
	private StyledText control;
	
	/* cached swt colors */
	private Color globalLineBackground;
	private Color globalBackground;

	public SwtColourer(MateText mt) {
		mateText = mt;

		control = mateText.getControl();
		control.addCaretListener(new CaretListener() {
			public void caretMoved(CaretEvent e) {
				updateHighlightedLine(control.getLineAtOffset(e.caretOffset));
			}
		});
	}

	private void updateHighlightedLine(int line) {
		if (caretLineHasChanged(line)) {
			control.setLineBackground(line, 1, globalLineBackground);
			control.setLineBackground(highlightedLine, 1, globalBackground);
			highlightedLine = line;
		}
	}

	private boolean caretLineHasChanged(int line) {
		return line != highlightedLine;
	}

	/* (non-Javadoc)
	 * @see com.redcareditor.mate.Colourer#setTheme(com.redcareditor.theme.Theme)
	 */
	public void setTheme(Theme theme) {
		this.theme = theme;
		theme.initForUse();
		initCachedColours();
		control.setBackground(globalBackground);
		int currentLine = control.getLineAtOffset(control.getCaretOffset());
		control.setLineBackground(currentLine, 1, globalLineBackground);
	}

	private void initCachedColours() {
		globalLineBackground = ColourUtil.getColour(globalLineBackgroundColour());
		globalBackground = ColourUtil.getColour(globalBackgroundColour());
	}

	/* (non-Javadoc)
	 * @see com.redcareditor.mate.Colourer#getTheme()
	 */
	public Theme getTheme() {
		return theme;
	}

	private String globalBackgroundColour() {
		String bgColour = theme.globalSettings.get("background");
		if (bgColour != null && bgColour != "") {
			bgColour = ColourUtil.mergeColour("#FFFFFF", bgColour);
			return bgColour;
		}
		return null;
	}

	private String globalLineBackgroundColour() {
		String colour = theme.globalSettings.get("lineHighlight");
		if (colour != null && colour != "") {
			colour = ColourUtil.mergeColour("#FFFFFF", colour);
			return colour;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.redcareditor.mate.Colourer#uncolourScopes(java.util.List)
	 */
	public void uncolourScopes(List<Scope> scopes) {

	}

	/* (non-Javadoc)
	 * @see com.redcareditor.mate.Colourer#uncolourScope(com.redcareditor.mate.Scope, boolean)
	 */
	public void uncolourScope(Scope scope, boolean something) {

	}

	/* (non-Javadoc)
	 * @see com.redcareditor.mate.Colourer#colourLineWithScopes(java.util.List)
	 */
	public void colourLineWithScopes(List<Scope> scopes) {

	}
}
