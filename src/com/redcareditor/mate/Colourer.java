package com.redcareditor.mate;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.LineBackgroundEvent;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import com.redcareditor.theme.Theme;
import com.redcareditor.theme.ThemeSetting;

public class Colourer {
	private Theme theme;
	private MateText mateText;
	
	private int highlightedLine = 0;
	
	public Colourer(MateText mt) {
		mateText = mt;

		this.mateText.getControl().addLineStyleListener(new LineStyleListener() {
			public void lineGetStyle(LineStyleEvent event) {
				colourLine(event);
			}
		});

		mateText.getControl().addCaretListener(new CaretListener() {
			public void caretMoved(CaretEvent e) {
				updateHighlightedLine(mateText.getControl().getLineAtOffset(e.caretOffset));
			}
		});
	}
	
	private void updateHighlightedLine(int line){
		if (caretLineHasChanged(line)){
			mateText.getControl().setLineBackground(line, 1, getColour(globalLineBackgroundColour()));
			mateText.getControl().setLineBackground(highlightedLine, 1, getColour(globalBackgroundColour()));
			highlightedLine = line;
		}
	}

	private boolean caretLineHasChanged(int line) {
		return line != highlightedLine;
	}

	public void setTheme(Theme theme) {
		this.theme = theme;
		theme.initForUse();
		System.out.printf("setTheme(%s) globalBackgroundColour() = %s\n", theme.name, globalBackgroundColour());
		mateText.getControl().setBackground(getColour(globalBackgroundColour()));
	}
	
	public Theme getTheme() {
		return theme;
	}
	
	private void colourLine(LineStyleEvent event) {
		if (theme == null)
			return;
		int eventLine = mateText.getControl().getLineAtOffset(event.lineOffset);
		ArrayList<Scope> scopes = mateText.parser.root.scopesOnLine(eventLine);
		System.out.printf("got to colour %d scopes\n", scopes.size());
		ArrayList<StyleRange> styleRanges = new ArrayList<StyleRange>();
		for (Scope scope : scopes) {
//			System.out.printf("  %s\n", scope.name);
			if (scope.parent == null) {
				continue;
			}
			if (scope.name == null && scope.pattern != null &&
				(scope.pattern instanceof SinglePattern || ((DoublePattern) scope.pattern).contentName == null)) {
				continue;
			}
			styleRanges.add(getStyleRangeForScope(scope, false));
			if (scope.pattern instanceof DoublePattern && ((DoublePattern) scope.pattern).contentName != null && scope.isCapture == false)
				styleRanges.add(getStyleRangeForScope(scope, true));
		}
//		StyleRange[] styleRangeArray = new StyleRange[styleRanges.size()];
		event.styles = (StyleRange[]) styleRanges.toArray(new StyleRange[0]);
	}
	
	private StyleRange getStyleRangeForScope(Scope scope, boolean inner) {
		StyleRange styleRange = new StyleRange();
		// TODO: allow for multiple settings that set different 
		// parts of the style.
		ThemeSetting setting = null;
		ThemeSetting excludeSetting = null;
		if (scope.parent != null) 
			excludeSetting = scope.parent.themeSetting;
		setting = theme.settingsForScope(scope, inner, null);//exclude_setting);

		if (inner) {
			styleRange.start = scope.getInnerStart().getOffset();
			styleRange.length = scope.getInnerEnd().getOffset() - styleRange.start;
		}
		else {
			styleRange.start = scope.getStart().getOffset();
			styleRange.length = scope.getEnd().getOffset() - styleRange.start;
			System.out.printf("colour %s (%d, %d)\n", scope.name, styleRange.start, styleRange.length);
		}
		if (setting != null)
			setStyleRangeProperties(scope, setting, styleRange);
		
		return styleRange;
	}
	
	private void setStyleRangeProperties(Scope scope, ThemeSetting setting, StyleRange styleRange) {
		String fontStyle = setting.settings.get("fontStyle");
		if (fontStyle == "italic") {
			styleRange.fontStyle = SWT.ITALIC;
		}
		else
			styleRange.fontStyle = SWT.NORMAL;

		if (fontStyle == "underline")
			styleRange.fontStyle = SWT.UNDERLINE_SINGLE;
		else
			styleRange.fontStyle = SWT.NORMAL; 
		
		String background = setting.settings.get("background");
//		stdout.printf("        scope background:        %s\n", background);
		String mergedBgColour;
		String parentBg = theme.globalSettings.get("background");
//		stdout.printf("        global background: %s\n", parent_bg);
		// TODO: wasn't this a better way of creating the background colours?
//		var parent_bg = scope.nearest_background_colour();
//		if (parent_bg == null) {
//		}
//		else {
//			stdout.printf("        parent background: %s\n", parent_bg);
//		}
		if (background != null && background != "") {
//			if (parent_bg != null) {
//				merged_bg_colour = parent_bg;
//			}
//			else {
			mergedBgColour = Colourer.mergeColour(parentBg, background);
//			}
			if (mergedBgColour != null) {
				scope.bgColour = mergedBgColour;
				styleRange.background = getColour(mergedBgColour);
//				stdout.printf("       tag.background = %s\n", merged_bg_colour);
			}
		}
		else {
			mergedBgColour = parentBg;
		}
//		stdout.printf("        merged_bg_colour:  %s\n", merged_bg_colour);
		String foreground = setting.settings.get("foreground");
//		stdout.printf("        scope foreground:        %s\n", foreground);
		String parentFg = scope.nearestForegroundColour();
		if (parentFg == null) {
			parentFg = theme.globalSettings.get("foreground");
//			stdout.printf("        global foreground:        %s\n", parent_fg);
		}
		if (foreground != null && foreground != "") {
			String mergedFgColour;
			if (parentFg != null && !scope.isCapture)
				mergedFgColour = Colourer.mergeColour(parentFg, foreground);
			else
				mergedFgColour = foreground;
			if (mergedFgColour != null) {
				scope.fgColour = mergedFgColour;
				styleRange.foreground = getColour(mergedFgColour);
			}
//			stdout.printf("       merged_fg_colour: %s\n", merged_fg_colour);
		}
//		stdout.printf("\n");
	}
	
	private void colourLineBackground(LineBackgroundEvent event) {
		if (theme == null)
			return;
		StyledText styledText = mateText.getControl();
		int eventLine = styledText.getLineAtOffset(event.lineOffset);
		int caretLine = styledText.getLineAtOffset(styledText.getCaretOffset());
//		System.out.printf("lineBack event.line= %d, caretLine = %d\n", 
//				eventLine, 
//				caretLine);
		if (eventLine == caretLine)
			event.lineBackground = getColour(globalLineBackgroundColour());
		else
			event.lineBackground = getColour(globalBackgroundColour());
	}
	
	private Color getColour(String colour) {
		return new Color(Display.getCurrent(), 
					Integer.parseInt(colour.substring(1, 3), 16),
					Integer.parseInt(colour.substring(3, 5), 16),
					Integer.parseInt(colour.substring(5, 7), 16));
	}
	
	private String globalBackgroundColour() {
		String bgColour = theme.globalSettings.get("background");
		if (bgColour != null && bgColour != "") {
			bgColour = Colourer.mergeColour("#FFFFFF", bgColour);
			return bgColour;
		}
		return null;
	}

	private String globalLineBackgroundColour() {
		String colour = theme.globalSettings.get("lineHighlight");
		if (colour != null && colour != "") {
			colour = Colourer.mergeColour("#FFFFFF", colour);
			return colour;
		}
		return null;
	}

	public static int char_to_hex(Character ch) {
		if (Character.isDigit(ch)) {
			return Character.digit(ch, 10);
		}
		return 0;
	}
	
	public static int hex_to_int(char ch1, char ch2) {
		return char_to_hex(ch1)*16 + char_to_hex(ch2);
	}
	
	// Here parent_colour is like '#FFFFFF' and
	// colour is like '#000000DD'.
	static public String mergeColour(String parentColour, String colour) {
		int pre_r, pre_g, pre_b;
		int post_r, post_g, post_b;
		int opacity;
		int new_r, new_g, new_b;
		String new_colour = null;
		if (parentColour == null)
			return null;
		if (colour.length() == 7)
			return colour;
		if (colour.length() == 9) {
			pre_r = hex_to_int(parentColour.charAt(1), parentColour.charAt(2));
			pre_g = hex_to_int(parentColour.charAt(3), parentColour.charAt(4));
			pre_b = hex_to_int(parentColour.charAt(5), parentColour.charAt(6));

			post_r = hex_to_int(colour.charAt(1), colour.charAt(2));
			post_g = hex_to_int(colour.charAt(3), colour.charAt(4));
			post_b = hex_to_int(colour.charAt(5), colour.charAt(6));
			opacity = hex_to_int(colour.charAt(7), colour.charAt(8));

			new_r = (pre_r*(255-opacity) + post_r*opacity)/255;
			new_g = (pre_g*(255-opacity) + post_g*opacity)/255;
			new_b = (pre_b*(255-opacity) + post_b*opacity)/255;
			new_colour = String.format("#%.2x%.2x%.2x", new_r, new_g, new_b);
			// stdout.printf("%s/%s/%s - %d,%d,%d\n", parent_colour, colour, new_colour, new_r, new_g, new_b);
			return new_colour;
		}
		return "#000000";
	}

	public void uncolourScopes(List<Scope> scopes) {
		
	}

	public void uncolourScope(Scope scope, boolean something) {
		
	}
	
	public void colourLineWithScopes(List<Scope> scopes) {
		
	}
}
