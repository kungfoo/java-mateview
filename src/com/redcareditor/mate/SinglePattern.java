package com.redcareditor.mate;

import java.util.List;
import java.util.Map;

import com.redcareditor.onig.Rx;
import com.redcareditor.plist.Dict;

public class SinglePattern extends Pattern {
	public Rx regex;
	public Map<Integer, String> captures;
	
	public SinglePattern(List<Pattern> grammarPatterns, Dict dict) {
		name = dict.getString("name");
		regex = Rx.createRx(dict.getString("match"));
		captures = makeCapturesFromPlist(dict.getDictionary("captures"));
		setDisabled(dict);
		grammarPatterns.add(this);
	}
}