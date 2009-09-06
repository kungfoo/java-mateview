package com.redcareditor.theme;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.redcareditor.mate.ScopeMatcher;
import com.redcareditor.onig.Match;
import com.redcareditor.plist.Dict;
import com.redcareditor.plist.PlistPropertyLoader;

public class ThemeSetting {
	public String name;
	public String scopeSelector;
	public Map<String, String> settings;
	public List<ScopeMatcher> matchers;
	
	private PlistPropertyLoader propertyLoader;
	
	public ThemeSetting(Dict dict){
		propertyLoader = new PlistPropertyLoader(dict, this);
		propertyLoader.loadStringProperty("name");
		this.scopeSelector = dict.getString("scope");
		
		loadSettings(dict);
		compileScopeMatchers();
	}

	private void loadSettings(Dict dict) {
		settings = new HashMap<String, String>();
		Dict settingsDict = dict.getDictionary("settings");
		for(String key : settingsDict.value.keySet()){
			settings.put(key, (String) settingsDict.value.get(key).value);
		}
	}

	public void compileScopeMatchers() {
		//stdout.printf("  compiling '%s'\n", selector);
		this.matchers = ScopeMatcher.compile(scopeSelector);
	}

	public Match match(String scope) {
		Match m;
		if (this.matchers == null)
			compileScopeMatchers();
		
		for (ScopeMatcher matcher : this.matchers) {
			if ((m = ScopeMatcher.testMatchRe(matcher.pos_rx, matcher.neg_rxs, scope)) != null)
				return m;
		}
		return null;
	}
}
