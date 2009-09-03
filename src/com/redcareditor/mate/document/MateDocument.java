package com.redcareditor.mate.document;

import com.redcareditor.mate.TextLocation;

public interface MateDocument {
	
	public int getLineCount();
	
	public int getLineLength(int line);
	
	public boolean addTextLocation(TextLocation location);
}
