package com.redcareditor.mate;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Composite;

import com.redcareditor.onig.NullRx;
import com.redcareditor.onig.Rx;
import com.redcareditor.theme.Theme;

public class MateText extends StyledText {
	private int modifyStart, modifyEnd;
	private String modifyText;
	public Parser parser;
	
	public MateText(Composite composite, int style) {
		super(composite, style);
		addListeners();
	}
	
	public void addListeners() {
		addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent e) {
				verifyEventCallback(e.start, e.end, e.text);
			}
		});
		
		addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				modifyEventCallback();
			}
		});
	}
	
	public void verifyEventCallback(int start, int end, String text) {
		modifyStart = start;
		modifyEnd   = end;
		modifyText  = text;
	}
	
	public void modifyEventCallback() {
		System.out.printf("modifying %d - %d, %d, %s\n", modifyStart, modifyEnd, getLineAtOffset(modifyStart), modifyText);
	}
	
	// Sets the grammar explicitly by name.
	// TODO: restore the uncolouring stuff
	public boolean setGrammarByName(String name) {
		if (this.parser != null && this.parser.grammar.name.equals(name))
			return true;
	  
		for (Bundle bundle : Bundle.bundles) {
			for (Grammar grammar : bundle.grammars) {
				if (grammar.name.equals(name)) {
//					int parsed_upto = 150;
					Theme theme;
//					if (this.parser != null) {
//						theme = this.parser.colourer.theme;
//						this.parser.colourer.uncolour_scope(this.parser.root, true);
//						parsed_upto = this.parser.parsed_upto;
//						this.parser.close();
//					}
					this.parser = new Parser(grammar, this);
//					this.parser.last_visible_line_changed(parsed_upto);
//					GLib.Signal.emit_by_name(this, "grammar_changed", gr.name);
//					if (theme != null)
//					  this.parser.change_theme(theme);
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
		for (Bundle bundle : Bundle.bundles) {
			for (Grammar grammar : bundle.grammars) {
				for (String ext : grammar.fileTypes) {
					if (fileName.endsWith(ext) && (bestName == null || ext.length() > bestLength)) {
						bestName = grammar.name;
						bestLength = ext.length();
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
		for (Bundle bundle : Bundle.bundles) {
			for (Grammar grammar : bundle.grammars) {
				re = grammar.firstLineMatch;
				if (re instanceof NullRx) {
				}
				else {
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
}



