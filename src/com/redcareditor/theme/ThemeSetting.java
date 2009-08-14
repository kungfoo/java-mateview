package com.redcareditor.theme;

import java.util.List;
import java.util.Map;

import org.joni.Matcher;

import com.redcareditor.plist.Dict;

public class ThemeSetting {
	public String name;
	public String selector;
	public Map<String, String> settings;
	public List<Matcher	> matchers;
	
	
	public ThemeSetting(Dict dict){
		
	}
}
