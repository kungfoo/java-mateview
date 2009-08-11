package com.redcareditor.mate;

import java.util.HashMap;
import java.util.Map;

import com.redcareditor.plist.Dict;

public class Pattern {
	public Grammar grammar;
	public String name;
	public Map<Integer, String> captures;

	
	public static Pattern createPattern(Dict dict) {
		if (dict.containsElement("match")) {
			return new SinglePattern(dict);
		}

		if (dict.containsElement("include")) {

		}

		if (dict.containsElement("begin")) {

		}
		return null;
	}
	
	protected void loadCaptures(Dict dict){
		captures = new HashMap<Integer, String>();
		if (dict == null) {
			return;
		} else {
			for(String captureNumber : dict.value.keySet()){
				Dict captureDict = dict.getDictionary(captureNumber);
				int captureInt = Integer.parseInt(captureNumber);
				String captureName = captureDict.getString("name");
				captures.put(captureInt, captureName);
				System.out.println(captureInt + "->" + captureName);
			}
		}
	}
}
