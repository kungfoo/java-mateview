package com.redcareditor.plist.parser;

import java.util.ArrayList;
import java.util.List;


/**
 * this class is used to build up plist array entries by consecutively adding
 * values to them as they occur in the xml stream.
 * 
 * @author kungfoo
 * 
 */
public class ArrayNode extends PlistNode<List<Object>> {
	public ArrayNode() {
		super.value = new ArrayList<Object>();
	}

	public void add(Object o) {
		super.value.add(o);
	}
}
