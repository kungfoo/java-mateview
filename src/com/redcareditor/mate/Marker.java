package com.redcareditor.mate;

import com.redcareditor.onig.Match;

public class Marker {
	public boolean isCloseScope;
	public Pattern pattern;
	public Match match;
	public int from;  // the line offset where it begins
	public int hint;  // ??
}
