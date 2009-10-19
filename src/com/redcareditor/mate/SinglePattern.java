package com.redcareditor.mate;

import java.util.Map;

import com.redcareditor.onig.Rx;
import com.redcareditor.plist.Dict;

public class SinglePattern extends Pattern {
	public Rx match;
	public Map<Integer, String> captures;
	
	public SinglePattern(Grammar grammar, Dict dict) {
		super(grammar);
		name = dict.getString("name");
		match = Rx.createRx(dict.getString("match"));
		captures = makeCapturesFromPlist(dict.getDictionary("captures"));
		setDisabled(dict);
	}
	
	@Override
	public void replaceRepositoryIncludes() {
		// nothing to do
	}
}