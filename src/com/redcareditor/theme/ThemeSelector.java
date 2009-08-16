package com.redcareditor.theme;

import java.util.ArrayList;
import java.util.List;

import com.redcareditor.onig.Rx;

public class ThemeSelector {
	public Rx positiveRegex;
	public List<Rx> negativeRegexes;

	public static List<ThemeSelector> compile(String scopeSelector) {
		List<ThemeSelector> result = new ArrayList<ThemeSelector>();
		for (String selector : scopeSelector.split(",")) {
			result.add(new ThemeSelector(selector));
		}
		return result;
	}

	private ThemeSelector(String selector) {
		negativeRegexes = new ArrayList<Rx>();
		String[] positivesAndNegatives = selector.split(" -");
		for (String subSelector : positivesAndNegatives) {
			if (positiveRegex == null) {
				String s1 = backSlashDots(subSelector);
				String s2 = s1.replace(" ", ").* .*(");
				positiveRegex = new Rx("(" + s2 + ")");
			} else {
				String s1 = backSlashDots(subSelector);
				String s2 = s1.replace(" ", ".* .*");
				negativeRegexes.add(new Rx(s2));
			}
		}
	}

	private String backSlashDots(String subSelector) {
		return subSelector.trim().replace(".", "\\.");
	}
}
