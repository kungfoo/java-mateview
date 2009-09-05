package com.redcareditor.mate.document;

public interface MateTextLocation extends Comparable<MateTextLocation> {
	public int getLine();

	public int getLineOffset();
}
