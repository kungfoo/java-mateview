package com.redcareditor.mate;

import java.util.ArrayList;
import java.util.*;

import com.redcareditor.onig.Rx;
import com.redcareditor.plist.Dict;
import com.redcareditor.plist.PlistNode;
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
		replaceIncludePatterns();
	}

	private void loadPatterns() {
		allPatterns = new ArrayList<Pattern>();
		Dict[] patterns = plist.getDictionaries("patterns");
		for (Dict p : patterns) {
			Pattern pattern = Pattern.createPattern(p);
			if (pattern != null) {
				pattern.grammar = this;
				allPatterns.add(pattern);
			}
		}
	}

	private void loadRepository() {
		repository = new HashMap<String, List<Pattern>>();
		Dict plistRepo = plist.getDictionary("repository");
		Dict plistRepoEntry;
		for (String key : plistRepo.keys()) {
			List<Pattern> repoArray = new ArrayList<Pattern>();
			plistRepoEntry = plistRepo.getDictionary(key);
			if (plistRepoEntry.containsElement("begin") || plistRepo.containsElement("match")) {
				Pattern pattern = Pattern.createPattern(plistRepoEntry);
				if (pattern != null) {
					pattern.grammar = this;
					repoArray.add(pattern);
				}
			}
			if (plistRepo.containsElement("patterns")) {
				for (PlistNode<?> plistPattern : plistRepoEntry.getArray("patterns")) {
					Pattern pattern = Pattern.createPattern((Dict) plistPattern);
					if (pattern != null) {
						pattern.grammar = this;
						repoArray.add(pattern);
					}
				}
			}
			repository.put(key, repoArray);
		}
	}

	private void replaceIncludePatterns() {
		for (Pattern p : allPatterns) {
			if (p instanceof DoublePattern) {
				Pattern.replaceIncludePatterns((ArrayList<Pattern>) ((DoublePattern) p).patterns, this);
			}
		}
		Pattern.replaceIncludePatterns((ArrayList<Pattern>) this.allPatterns, this);
	}
	
	public static Grammar findByScopeName(String scope) {
//		for (var bundle in Buffer.bundles) {
//			foreach (var gr in bundle.grammars) {
//				if (gr.scope_name == scope) {
//					return gr;
//				}
//			}
//		}				
		return null;
	}

	private boolean loaded() {
		return allPatterns != null && repository != null;
	}
}
