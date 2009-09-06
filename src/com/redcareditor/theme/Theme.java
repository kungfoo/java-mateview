package com.redcareditor.theme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.redcareditor.mate.Scope;
import com.redcareditor.mate.ScopeMatcher;
import com.redcareditor.onig.Match;
import com.redcareditor.plist.Dict;
import com.redcareditor.plist.PlistNode;
import com.redcareditor.plist.PlistPropertyLoader;

public class Theme {
	public String author;
	public String name;
	public Map<String, String> globalSettings = new HashMap<String, String>();
	public List<ThemeSetting> settings = new ArrayList<ThemeSetting>();
	public Map<String, ThemeSetting> cachedSettingsForScopes = new HashMap<String, ThemeSetting>();

	private PlistPropertyLoader propertyLoader;
	private boolean isInitialized = false;

	public Theme(Dict dict) {
		propertyLoader = new PlistPropertyLoader(dict, this);
		propertyLoader.loadStringProperty("name");
		propertyLoader.loadStringProperty("author");
		loadSettings(dict);
	}

	private void loadSettings(Dict dict) {
		List<PlistNode<?>> dictSettings = dict.getArray("settings");
		for (PlistNode<?> node : dictSettings) {
			Dict nodeDict = (Dict) node;
			if (!nodeDict.containsElement("scope")) {
				loadGlobalSetting(nodeDict);
			} else {
				settings.add(new ThemeSetting(nodeDict));
			}
		}
	}

	private void loadGlobalSetting(Dict nodeDict) {
		Dict settingsDict = nodeDict.getDictionary("settings");
		for (String key : settingsDict.value.keySet()) {
			globalSettings.put(key, settingsDict.getString(key));
		}
	}

	public void initForUse() {
		if (isInitialized)
			return;
		isInitialized = true;
		System.out.printf("initializing theme for use: %s\n", name);
		this.cachedSettingsForScopes = new HashMap<String, ThemeSetting>();
		for (ThemeSetting setting : settings) {
			setting.compileScopeMatchers();
		}
	}

	public ThemeSetting settingsForScope(Scope scope, boolean inner, ThemeSetting excludeSetting) {
		if (isSettingAlreadyCached(scope.name)) {
			return cachedSettingsForScopes.get(scope.name);
		} else {
			ThemeSetting setting = findSetting(scope, inner, excludeSetting);
			cachedSettingsForScopes.put(scope.name, setting);
			return setting;
		}
	}

	private boolean isSettingAlreadyCached(String scope) {
		return cachedSettingsForScopes.containsKey(scope);
	}

	// TODO make this return multiple themes if they are identical
	// (see 13.5 of Textmate manual)
	public ThemeSetting findSetting(Scope scope, boolean inner, ThemeSetting excludeSetting) {
		String scopeName = scope.hierarchyNames(inner);
		//stdout.printf("  finding settings for '%s'\n", scope_name);
		Match current_m = null, m;
		ThemeSetting current = null;
		for (ThemeSetting setting : settings) {
			if (setting == excludeSetting && excludeSetting != null) {
//				stdout.printf("    setting '%s' excluded due to parent\n", exclude_setting.name);
			}
			else {
				if ((m = setting.match(scopeName)) != null) {
//					stdout.printf("    setting '%s' matches selector '%s'\n", setting.name, setting.selector); 
					if (current == null) {
						current = setting;
						current_m = m;
					}
					else if (ScopeMatcher.compareMatch(scopeName, current_m, m) < 0) {
						current = setting;
						current_m = m;
					}
				}
			}
		}
//		if (current == null) {
//			stdout.printf("none match\n");
//		}
//		else {
//			stdout.printf("    best: '%s'\n", current.name);
//		}
		return current;
	}
}
