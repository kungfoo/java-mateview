package com.redcareditor.mate;

import com.redcareditor.plist.Dict;

public class IncludePattern extends Pattern {
	public IncludePattern(Grammar grammar, Dict dict) {
		super(grammar);
		name = dict.getString("include");
//		System.out.printf("ip: %s\n", name);
	}
	
	@Override
	public void replaceRepositoryIncludes() {
		
	}
}
