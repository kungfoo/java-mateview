package com.redcareditor.mate;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Composite;

public class MateText extends StyledText {
	private int modifyStart, modifyEnd;
	private String modifyText;
	
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
}



