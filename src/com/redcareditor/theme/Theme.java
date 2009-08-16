package com.redcareditor.theme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	public Theme(Dict dict) {
		propertyLoader = new PlistPropertyLoader(dict, this);
		propertyLoader.loadStringProperty("name");
		propertyLoader.loadStringProperty("author");
		loadSettings(dict);
	}

	public ThemeSetting settingForScope(String scope) {
		if (isSettingAlreadyCached(scope)) {
			return cachedSettingsForScopes.get(scope);
		} else {
			ThemeSetting setting = findSetting(scope);
			cachedSettingsForScopes.put(scope, setting);
			return setting;
		}
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

	private boolean isSettingAlreadyCached(String scope) {
		return cachedSettingsForScopes.containsKey(scope);
	}

	private ThemeSetting findSetting(String scope) {
		// TODO: implement setting lookup similar to Daniels version.
		return null;
	}
}
