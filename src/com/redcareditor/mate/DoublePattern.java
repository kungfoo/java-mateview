package com.redcareditor.mate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.redcareditor.onig.Rx;
import com.redcareditor.plist.Dict;
import com.redcareditor.plist.PlistNode;

public class DoublePattern extends Pattern {
	public String contentName;
	public Rx begin;
	public Rx end;
	public String endString;
	public String beginString;
	public Map<Integer, String> beginCaptures;
	public Map<Integer, String> endCaptures;
	public Map<Integer, String> bothCaptures;
	public List<Pattern> patterns;

	public DoublePattern(List<Pattern> grammarPatterns, Dict dict) {
		name = dict.getString("name");
		begin = Rx.createRx(dict.getString("begin"));
		endString = dict.getString("end");
		contentName = dict.getString("contentName");

		loadCaptures(dict);
		loadPatterns(grammarPatterns, dict);

		setDisabled(dict);
		grammarPatterns.add(this);
	}

	private void loadPatterns(List<Pattern> grammarPatterns, Dict dict) {
		patterns = new ArrayList<Pattern>();
		List<PlistNode<?>> plistPatterns = dict.getArray("patterns");
		Pattern subPattern;
		if (plistPatterns != null) {
			for (PlistNode<?> plistPattern : plistPatterns) {
				subPattern = Pattern.createPattern(grammarPatterns, (Dict) plistPattern);
				if (subPattern != null) {
					patterns.add(subPattern);
				}
			}
		}
	}

	private void loadCaptures(Dict dict) {
		if (dict.containsElement("beginCaptures")) {
			beginCaptures = Pattern.makeCapturesFromPlist(dict.getDictionary("beginCaptures"));
		}
		if (dict.containsElement("captures")) {
			bothCaptures = Pattern.makeCapturesFromPlist(dict.getDictionary("captures"));
		}
		if (dict.containsElement("endCaptures")) {
			endCaptures = Pattern.makeCapturesFromPlist(dict.getDictionary("endCaptures"));
		}
	}
}
