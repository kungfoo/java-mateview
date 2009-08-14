package com.redcareditor.plist;

import java.lang.reflect.Field;

import com.redcareditor.onig.Rx;

public class PlistPropertyLoader {
	private Dict dict;
	private Object object;

	public PlistPropertyLoader(Dict dict, Object obj){
		this.dict = dict;
		this.object = obj;
	}
	
	public void loadStringProperty(String propertyName) {
		String value = dict.getString(propertyName);
		try {
			Field prop = object.getClass().getDeclaredField(propertyName);
			if (value != null) {
				prop.set(object, value);
			}
		} catch (Exception e) {
			System.out.println(String.format("Can't set %s = %s", propertyName, value));
			e.printStackTrace();
		}
	}
	
	public void loadRegexProperty(String propertyName) {
		String value = dict.getString(propertyName);
		try {
			Field prop = object.getClass().getDeclaredField(propertyName);
			if (value != null) {
				Rx regex = new Rx(value);
				prop.set(object, regex);
			}
		} catch (Exception e) {
			System.out.println(String.format("Can't set %s = %s", propertyName, value));
			e.printStackTrace();
		}
	}
}
