package com.redcareditor.mate;

import java.util.ArrayList;
import java.util.HashMap;

import com.redcareditor.onig.Rx;
import com.redcareditor.plist.Dict;
import com.redcareditor.plist.PlistNode;

public class DoublePattern extends Pattern {
	public String contentName;
	public Rx begin;
	public Rx end;
	public String endString;
	public String beginString;
	public HashMap<Integer, String> beginCaptures;
	public HashMap<Integer, String> endCaptures;
	public HashMap<Integer, String> bothCaptures;
	public ArrayList<Pattern> patterns;
	
	public DoublePattern(ArrayList<Pattern> grammarPatterns, Dict dict) {
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
		ArrayList<PlistNode<?>> plistPatterns = (ArrayList<PlistNode<?>>) dict.getArray("patterns");
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
