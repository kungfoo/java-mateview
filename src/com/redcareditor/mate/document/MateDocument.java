package com.redcareditor.mate.document;


public interface MateDocument {
	
	public int getLineCount();
	
	public int getLineLength(int line);
	
	public boolean addTextLocation(MateTextLocation location);
}