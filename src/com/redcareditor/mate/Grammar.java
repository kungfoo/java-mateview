package com.redcareditor.mate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.redcareditor.onig.Rx;
import com.redcareditor.plist.Dict;
import com.redcareditor.plist.PlistNode;
import com.redcareditor.plist.PlistPropertyLoader;

public class Grammar {
	public String name;
	public String fileName;
	public Dict plist;
	private PlistPropertyLoader propertyLoader;
	public String[] fileTypes;
	public String keyEquivalent;
	public String scopeName;
	public String comment;

	public List<Pattern> allPatterns;
	public List<Pattern> patterns;
	public List<Pattern> singlePatterns;
	public Map<String, List<Pattern>> repository;
	public Rx firstLineMatch;
	public Rx foldingStartMarker;
	public Rx foldingStopMarker;

	/* these are here for lookup speed purposes */
	private static Map<String, Grammar> grammarsByScopeNames = new HashMap<String, Grammar>();
	
	public Grammar(String plistFile){
		this.plist = Dict.parseFile(plistFile);
		propertyLoader = new PlistPropertyLoader(plist, this);
		initForReference();
	}

	private void initForReference() {
		String[] properties = new String[] { "name", "keyEquivalent", "scopeName", "comment" };
		for (String property : properties) {
			propertyLoader.loadStringProperty(property);
		}
		grammarsByScopeNames.put(scopeName, this);
		propertyLoader.loadRegexProperty("firstLineMatch");
		if (plist.containsElement("fileTypes"))
			fileTypes = plist.getStrings("fileTypes");
	}

	public void initForUse() {
		if (loaded())
			return;
		System.out.printf("initForUse: %s\n", this.name);
		
		initForReference();
		propertyLoader.loadRegexProperty("foldingStartMarker");
		propertyLoader.loadRegexProperty("foldingStopMarker");
		
		allPatterns = new ArrayList<Pattern>();
		loadPatterns();
		loadRepository();
		replaceIncludePatterns();
	}

	private void loadPatterns() {
		patterns = new ArrayList<Pattern>();
		Dict[] patternsDict = plist.getDictionaries("patterns");
		for (Dict patternDict : patternsDict) {
			createAndAddPattern(patterns, patternDict);
		}
	}

	private void loadRepository() {
		repository = new HashMap<String, List<Pattern>>();
		Dict plistRepo = plist.getDictionary("repository");
		for (String key : plistRepo.keys()) {
			List<Pattern> repoArray = new ArrayList<Pattern>();
			Dict plistRepoEntry = plistRepo.getDictionary(key);
			if (plistRepoEntry.containsElement("begin") || plistRepoEntry.containsElement("match")) {
				createAndAddPattern(repoArray, plistRepoEntry);
			}
			else if (plistRepoEntry.containsElement("patterns")) {
				for (PlistNode<?> plistPattern : plistRepoEntry.getArray("patterns")) {
					createAndAddPattern(repoArray, (Dict) plistPattern);
				}
			}
			repository.put(key, repoArray);
		}
	}

	public Pattern createAndAddPattern(List<Pattern> repoArray, Dict plistRepoEntry) {
		Pattern pattern = Pattern.createPattern(this, plistRepoEntry);
		allPatterns.add(pattern);
		if (pattern != null) {
			repoArray.add(pattern);
		}
		return pattern;
	}

	private void replaceIncludePatterns() {
		for (Pattern p : allPatterns) {
			if (p instanceof DoublePattern) {
				Pattern.replaceIncludePatterns(((DoublePattern) p).patterns, this);
			}
		}
		Pattern.replaceIncludePatterns(patterns, this);
	}

	public static Grammar findByScopeName(String scope) {
		return grammarsByScopeNames.get(scope);
	}

	private boolean loaded() {
		return allPatterns != null && repository != null;
	}
}
