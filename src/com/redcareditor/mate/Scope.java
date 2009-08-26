
package com.redcareditor.mate;

public class Scope {
	public MateText mateText;
	public String name;
	public Pattern pattern;
	
	public boolean isOpen;
	
	public Scope(MateText mt, String n) {
		mateText = mt;
		name = n;
	}
}
