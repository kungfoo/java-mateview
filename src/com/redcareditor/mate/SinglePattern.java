package com.redcareditor.mate;

import com.redcareditor.onig.Rx;
import com.redcareditor.plist.Dict;

public class SinglePattern extends Pattern {
	public Rx regex;
	
	public SinglePattern(Dict dict) {
		name = dict.getString("name");
		// can't be null, otherwise we would never end up here.
		regex = new Rx(dict.getString("match"));
		loadCaptures(dict.getDictionary("captures"));
	}
}