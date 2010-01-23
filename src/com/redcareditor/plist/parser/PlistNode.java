package com.redcareditor.plist.parser;


/**
 * generic class that holds the various elements of the property tree in a plist
 * file.
 * 
 * @author kungfoo
 * 
 * @param <T>
 */
public class PlistNode<T> {
	private PlistNode<?> parent;
	public T value;

	public PlistNode(T value) {
		this.value = value;
	}

	protected PlistNode() {
	}

	public void setParent(PlistNode<?> node) {
		parent = node;
	}

	public PlistNode<?> getParent() {
		return parent;
	}

	@Override
	public String toString() {
		return value == null ? "null" : value.toString();
	}
}
