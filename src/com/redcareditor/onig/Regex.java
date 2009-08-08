package com.redcareditor.onig;

/**
 * wrapper class around the Joni Regex library which is a optimized port of
 * Onigurama
 * 
 * @author kungfoo
 * 
 */
public class Regex {
	String pattern;

	public Regex(String pattern) {
		// TODO: really make this a regex then...
		this.pattern = pattern;
	}
}
