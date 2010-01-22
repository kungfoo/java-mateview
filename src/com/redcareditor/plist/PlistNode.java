package com.redcareditor.plist;


/**
 * generic class that holds the various elements of the property tree in a plist
 * file.
 * 
 * @author kungfoo
 * 
 * @param <T>
 */
public class PlistNode<T> {
	public T value;

	public PlistNode(T value) {
		this.value = value;
	}

	protected PlistNode() {

	}
}
