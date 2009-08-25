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
		
		Dict captureDict = dict.getDictionary("beginCaptures");
		if (captureDict != null)
			beginCaptures = Pattern.makeCapturesFromPlist(captureDict);
		captureDict = dict.getDictionary("captures");
		if (captureDict != null)
			bothCaptures = Pattern.makeCapturesFromPlist(captureDict);
		captureDict = dict.getDictionary("endCaptures");
		if (captureDict != null)
			endCaptures = Pattern.makeCapturesFromPlist(captureDict);
		
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
		setDisabled(dict);
		grammarPatterns.add(this);
	}
}
