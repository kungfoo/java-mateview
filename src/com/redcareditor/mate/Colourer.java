package com.redcareditor.mate;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.custom.LineBackgroundEvent;
import org.eclipse.swt.custom.LineBackgroundListener;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import com.redcareditor.theme.Theme;

public class Colourer {
	private Theme theme;
	private MateText mateText;
	
	public Colourer(MateText mt) {
		this.mateText = mt;
		this.mateText.getControl().addLineStyleListener(new LineStyleListener() {
			public void lineGetStyle(LineStyleEvent event) {
				colourLine(event);
			}
		});
		this.mateText.getControl().addLineBackgroundListener(new LineBackgroundListener() {
			public void lineGetBackground(LineBackgroundEvent event) {
				colourLineBackground(event);
			}
		});
	}

	public void setTheme(Theme theme) {
		this.theme = theme;
		theme.initForUse();
		System.out.printf("setTheme(%s) globalBackgroundColour() = %s\n", theme.name, globalBackgroundColour());
		mateText.getControl().setBackground(getColour(globalBackgroundColour()));
	}
	
	public Theme getTheme() {
		return this.theme;
	}
	
	private void colourLine(LineStyleEvent event) {
		if (this.theme == null)
			return;
		int eventLine = mateText.getControl().getLineAtOffset(event.lineOffset);
		ArrayList<Scope> scopes = mateText.parser.root.scopesOnLine(eventLine);
		System.out.printf("got to colour %d scopes\n", scopes.size());
		for (Scope scope : scopes) {
			System.out.printf("  %s\n", scope.name);
		}
	}
	
	private void colourLineBackground(LineBackgroundEvent event) {
		if (this.theme == null)
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
