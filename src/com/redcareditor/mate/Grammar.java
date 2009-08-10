package com.redcareditor.mate;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.redcareditor.onig.Rx;
import com.redcareditor.plist.Dict;

public class Grammar {
	public String name;
	public Dict plist;
	public String[] fileTypes;
	public String keyEquivalent;
	public String scopeName;
	public String comment;

	public List<Pattern> allPatterns;
	public Map<String, List<Pattern>> repository;
	public Rx firstLineMatch;
	public Rx foldingStartMarker;
	public Rx foldingStopMarker;

	public Grammar(Dict plist) {
		this.plist = plist;
	}

	public void initForReference() {
		String[] properties = new String[] { "name", "keyEquivalent", "scopeName", "comment" };
		for (String property : properties) {
			loadStringProperty(property);
		}
		loadRegexProperty("firstLineMatch");
		fileTypes = plist.getStrings("fileTypes");
	}

	public void initForUse() {
		if (loaded()) {
			return;
		}

		loadRegexProperty("foldingStartMarker");
		loadRegexProperty("foldingStopMarker");

		loadPatterns();
		loadRepository();
	}

	private void loadPatterns() {
		allPatterns = new ArrayList<Pattern>();
		Dict[] patterns = plist.getDictionaries("patterns");
		for(Dict p : patterns){
			Pattern pattern = Pattern.createPattern(p);
			pattern.grammar = this;
			allPatterns.add(pattern);
		}
	}

	private void loadRepository() {

	}

	private void loadStringProperty(String propertyName) {
		String value = plist.getString(propertyName);
		try {
			Field prop = this.getClass().getDeclaredField(propertyName);
			if (value != null) {
				prop.set(this, value);
			}
		} catch (Exception e) {
			System.out.println(String.format("Can't set %s = %s", propertyName, value));
			e.printStackTrace();
		}
	}

	private void loadRegexProperty(String propertyName) {
		String value = plist.getString(propertyName);
		try {
			Field prop = this.getClass().getDeclaredField(propertyName);
			if (value != null) {
				Rx regex = new Rx(value);
				prop.set(this, regex);
			}
		} catch (Exception e) {
			System.out.println(String.format("Can't set %s = %s", propertyName, value));
			e.printStackTrace();
		}
	}

	private boolean loaded() {
		return allPatterns != null && repository != null;
	}
}
