package com.redcareditor.mate;

import java.util.*;

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
                // System.out.println(captureInt + "->" + captureName);
			}
		}
	}
	
	public static void replaceIncludePatterns(ArrayList<Pattern> patterns, Grammar grammar) {
		replaceRepositoryIncludes(patterns, grammar);
		replaceBaseAndSelfIncludes(patterns, grammar);
	}
	
	public static void replaceRepositoryIncludes(ArrayList<Pattern> patterns, Grammar grammar) {
		ArrayList<Pattern> includePatterns = new ArrayList<Pattern>();
		ArrayList<Pattern> patternsToInclude = new ArrayList<Pattern>();
		boolean anyIncluded = true;
		while (anyIncluded) {
			anyIncluded = false;
			for (Pattern p : patterns) {
				if (p instanceof IncludePattern && p.name.startsWith("#")) {
					includePatterns.add(p);
					String reponame = p.name.substring(1, p.name.length() - 1);
					ArrayList<Pattern> repositoryEntryPatterns = (ArrayList<Pattern>) grammar.repository.get(reponame);
					if (repositoryEntryPatterns != null) {
						for (Pattern p2 : repositoryEntryPatterns) {
							patternsToInclude.add(p2);
						}
					}
					else {
						System.out.printf("warning: couldn't find repository key '%s' in grammar '%s'\n", reponame, grammar.name);
					}
				}
			}
			removePatterns(patterns, includePatterns);
			addPatterns(patterns, patternsToInclude);
			includePatterns.clear();
			patternsToInclude.clear();
		}
	}
	
	public static void replaceBaseAndSelfIncludes(ArrayList<Pattern> patterns, Grammar grammar) {
		ArrayList<Pattern> includePatterns = new ArrayList<Pattern>();
		ArrayList<Pattern> patternsToInclude = new ArrayList<Pattern>();
		boolean alreadySelf = false; // some patterns have $self twice
		Grammar ng;
		for (Pattern p : patterns) {
			if (p instanceof IncludePattern) {
				if (p.name.startsWith("$")) {
					includePatterns.add(p);
					if ((p.name == "$self" || p.name == "$base") && !alreadySelf) {
						alreadySelf = true;
						for (Pattern pat : grammar.allPatterns) {
							patternsToInclude.add(pat);
						}
					}
				}
				else if ((ng = Grammar.findByScopeName(p.name)) != null) {
					ng.initForUse();
					includePatterns.add(p);
					for (Pattern pat : ng.allPatterns) {
						patternsToInclude.add(pat);
					}
				}
				else {
					System.out.printf("unknown include pattern: %s\n", p.name);
				}
			}
		}
		removePatterns(patterns, includePatterns);
		addPatterns(patterns, patternsToInclude);
	}
	
	private static void removePatterns(ArrayList<Pattern> patlist, ArrayList<Pattern> ps) {
		for (Pattern p : ps) {
			patlist.remove(p);
		}
	}

	private static void addPatterns(ArrayList<Pattern> patlist, ArrayList<Pattern> ps) {
		for (Pattern p : ps) {
			patlist.add(p);
		}
	}
}
