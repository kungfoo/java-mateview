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
	
	// stuff only needed in this class
	private boolean initialized = false;
	private PlistPropertyLoader propertyLoader;
	
	public Theme(Dict dict){
		propertyLoader = new PlistPropertyLoader(dict, this);
		
		propertyLoader.loadStringProperty("name");
		propertyLoader.loadStringProperty("author");
		
		List<PlistNode<?>> dictSettings = dict.getArray("settings");
		for(PlistNode<?> node : dictSettings){
			Dict nodeDict = (Dict) node;
			if(nodeDict.getString("scope") == null){
				Dict settingsDict = nodeDict.getDictionary("settings");
				for(String key : settingsDict.value.keySet()){
					globalSettings.put(key, settingsDict.getString(key));
				}
			} else{
				settings.add(new ThemeSetting(nodeDict));
			}
		}
	}
	
	
}
