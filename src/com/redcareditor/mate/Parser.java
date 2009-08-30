package com.redcareditor.mate;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;

public class Parser {
	public Grammar grammar;
	public Colourer colourer;
	public MateText mateText;
	
	public int parsed_upto;	
	public int lookAhead;
	public int lastVisibleLine;
	public int deactivationLevel;
	public int parsedUpto;
	public boolean alwaysParseAll;
	
	public RangeSet changes;
	public Scope root;

	// temporary stores for the modifications to the mateText
	private int modifyStart, modifyEnd;
	private String modifyText;
	
	public Parser(Grammar g, MateText m) {
		g.initForUse();
		grammar = g;
		mateText = m;
		lookAhead = 100;
		lastVisibleLine = 0;
//		tags = new Sequence<TextTag>(null);
		changes = new RangeSet();
		colourer = new Colourer(m);
		deactivationLevel = 0;
		makeRoot();
		attachListeners();
		parsedUpto = 0;
		alwaysParseAll = false;
	}
	
	public void makeRoot() {
		this.root = new Scope(mateText, this.grammar.scopeName);
		this.root.isOpen = true;
//		this.root.start_mark_set(0, 0, true);
//		this.root.end_mark_set(buffer.end_iter().get_line(), buffer.end_iter().get_line_index(), false);
		DoublePattern dp = new DoublePattern();
		dp.name = this.grammar.name;
		dp.patterns = this.grammar.allPatterns;
		dp.grammar = this.grammar;
		this.root.pattern = dp;
	}
	
	public void attachListeners() {
		mateText.addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent e) {
				verifyEventCallback(e.start, e.end, e.text);
			}
		});
		
		mateText.addModifyListener(new ModifyListener() {
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
		System.out.printf("modifying %d - %d, %d, %s\n", modifyStart, modifyEnd, mateText.getLineAtOffset(modifyStart), modifyText);
	}
	
}
