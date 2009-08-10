package com.redcareditor.plist;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * class to load plist files.
 * 
 * @author kungfoo
 * 
 */
public class Dict extends PlistNode<Map<String, PlistNode<?>>> {

	public static Dict parseFile(String filename) {
		SAXBuilder builder;
		Document document;
		builder = new SAXBuilder();
		try {
			document = builder.build(new File(filename));
			return new Dict(document.getRootElement().getChild("dict"));

		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("The file " + filename + " was not found!");
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected Dict(Element element) {
		value = new HashMap<String, PlistNode<?>>();

		List<Element> children = element.getChildren();
		String key = null;
		for (Element c : children) {
			if (c.getName().equals("key")) {
				key = c.getValue();
			} else {
				value.put(key, PlistNode.parseElement(c));
			}
		}
	}

	public String getString(String key) {
		return (String) value.get(key).value;
	}

	public int getInt(String key) {
		return (Integer) value.get(key).value;
	}

	@SuppressWarnings("unchecked")
	public String[] getStrings(String key) {
		List<PlistNode<String>> strings = (List<PlistNode<String>>) value.get(key).value;
		String[] result = new String[strings.size()];
		int i = 0;
		for (PlistNode<String> str : strings) {
			result[i++] = str.value;
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public Dict[] getDictionaries(String key) {
		List<Dict> dictionaries = (List<Dict>) value.get(key).value;
		Dict[] result = new Dict[dictionaries.size()];
		int i = 0;
		for (Dict dict : dictionaries) {
			result[i++] = dict;
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<PlistNode<?>> getArray(String key) {
		return (List<PlistNode<?>>) value.get(key).value;
	}

	public boolean containsElement(String string) {
		return value.get(string) != null;
	}
}
