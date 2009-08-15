package com.redcareditor.theme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.redcareditor.plist.Dict;
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
		
		
		
		
	}
}
