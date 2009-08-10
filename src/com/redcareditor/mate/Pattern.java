package com.redcareditor.mate;

import java.util.HashMap;
import java.util.Map;

import com.redcareditor.onig.Rx;
import com.redcareditor.plist.Dict;

public class Pattern {
	public Grammar grammar;
	public String name;
	public Map<Integer, String> captures;

	protected Pattern(){
	}
	
	public static Pattern createPattern(Dict dict) {
		if (dict.containsElement("match")) {
			return new Pattern().new SinglePattern(dict);
		}

		if (dict.containsElement("include")) {

		}

		if (dict.containsElement("begin")) {

		}
		return null;
	}
	
	private void loadCaptures(Dict dict){
		if (dict == null) {
			captures = new HashMap<Integer, String>();
			return;
		} else {
			
		}
	}

	public class SinglePattern extends Pattern{
		public Rx regex;
		public SinglePattern(Dict dict) {
			String name = dict.getString("name");
			// can't be null, otherwise we would never end up here.
			regex = new Rx(dict.getString("match"));
			loadCaptures(dict);
		}
	}

	public class DoublePattern {
	}

	public class IncludePattern {
	}
}
