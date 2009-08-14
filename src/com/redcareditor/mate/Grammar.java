package com.redcareditor.mate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.redcareditor.onig.Rx;
import com.redcareditor.plist.Dict;
import com.redcareditor.plist.PlistPropertyLoader;

public class Grammar {
	public String name;
	public Dict plist;
	private PlistPropertyLoader propertyLoader;
	public String[] fileTypes;
	public String keyEquivalent;
	public String scopeName;
	public String comment;

	public List<Pattern> allPatterns;
	public List<Pattern> singlePatterns;
	public Map<String, List<Pattern>> repository;
	public Rx firstLineMatch;
	public Rx foldingStartMarker;
	public Rx foldingStopMarker;

	public Grammar(Dict plist) {
		propertyLoader = new PlistPropertyLoader(plist, this);
		this.plist = plist;
	}

	public void initForReference() {
		String[] properties = new String[] { "name", "keyEquivalent", "scopeName", "comment" };
		for (String property : properties) {
			propertyLoader.loadStringProperty(property);
		}
		propertyLoader.loadRegexProperty("firstLineMatch");
		fileTypes = plist.getStrings("fileTypes");
	}

	public void initForUse() {
		if (loaded()) {
			return;
		}
		
		initForReference();
		propertyLoader.loadRegexProperty("foldingStartMarker");
		propertyLoader.loadRegexProperty("foldingStopMarker");

		loadPatterns();
		loadRepository();
	}

	private void loadPatterns() {
		allPatterns = new ArrayList<Pattern>();
		Dict[] patterns = plist.getDictionaries("patterns");
		for(Dict p : patterns){
			Pattern pattern = Pattern.createPattern(p);
			if(pattern != null){
				pattern.grammar = this;
				allPatterns.add(pattern);
			}
		}
	}

	private void loadRepository() {

	}

	private boolean loaded() {
		return allPatterns != null && repository != null;
	}
}
