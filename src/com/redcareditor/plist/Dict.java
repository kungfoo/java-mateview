package com.redcareditor.plist;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.redcareditor.onig.Rx;
import com.redcareditor.plist.parser.ArrayNode;
import com.redcareditor.plist.parser.PlistNode;
import com.redcareditor.plist.parser.PlistParser;
import com.redcareditor.plist.parser.PlistParsingException;

/**
 * class to load plist files.
 * 
 * @author kungfoo
 * 
 */
public class Dict extends PlistNode<Map<String, PlistNode<?>>> {
	/*
	 * a lot of Dicts seem to have very few entries, so we use a small default
	 * size
	 */
	private static final int DEFAULT_MAP_SIZE = 2;

	public static Dict parseFile(String filename) {
		FileInputStream stream;
		try {
			stream = new FileInputStream(filename);
		} catch (FileNotFoundException e) {
			throw new PlistParsingException("tried to parse plist file: " + filename + "\nIt was not found though.");
		}
		return PlistParser.parse(stream);
	}
	
	public Dict() {
		value = new HashMap<String, PlistNode<?>>(DEFAULT_MAP_SIZE);
	}

	public String getString(String key) {
		return tryGettingValue(this, key);
	}

	public Integer getInt(String key) {
		return tryGettingValue(this, key);
	}

	public String[] getStrings(String key) {
		List<Object> list = ((ArrayNode)value.get(key).value).value;
		return list.toArray(new String[0]);
	}

	public Dict[] getDictionaries(String key) {
		List<Object> list = ((ArrayNode)value.get(key).value).value;
		return list.toArray(new Dict[0]);
	}

	/* this can be replaced with getDictionaries.
	 * no casts are necessary then...
	public List<PlistNode<?>> getArray(String key) {
		return tryGettingValue(this, key);
	}
	*/

	public Dict getDictionary(String key) {
		if(value.containsKey(key)){
			return (Dict) value.get(key).value;
		} else {
			return null;
		}
	}

	public Rx getRegExp(String key) {
		return Rx.createRx(getString(key));
	}

	public boolean containsElement(String key) {
		return value.containsKey(key);
	}

	public Set<String> keys() {
		return value.keySet();
	}

	@SuppressWarnings("unchecked")
	private static <T> T tryGettingValue(Dict dict, String key) {
		if (dict.value.containsKey(key)) {
			return (T) dict.value.get(key).value;
		} else {
			return null;
		}
	}

	public void addNode(String key, PlistNode<?> node) {
		value.put(key, node);
		node.setParent(this);
	}
}
