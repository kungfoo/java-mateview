
package com.redcareditor.mate;

import java.util.*;

public class Bundle {
	public String name;
	public ArrayList<Grammar> grammars;
	
	public Bundle(String name) {
		this.name = name;
		this.grammars = new ArrayList<Grammar>();
	}
}