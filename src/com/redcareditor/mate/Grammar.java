package com.redcareditor.mate;

import com.redcareditor.onig.Rx;
import com.redcareditor.plist.Dict;

public class Grammar {
	private String name;
	private Dict plist;
	private String[] fileTypes;
	// TODO: add regexen here
	Rx firstLineMatch;
	private String keyEquivalent;
	private String scopeName;
	// patterns
	private String filename;
	private String comment;

	public Grammar(Dict plist) {
		this.plist = plist;
	}

	public void initForReference() {
		name = plist.getString("name");
		firstLineMatch = new Rx(plist.getString("firstLineMatch"));
		keyEquivalent = plist.getString("keyEquivalent");
		scopeName = plist.getString("scopeName");
		comment = plist.getString("comment");
		
	}

	public String getName() {
		return name;
	}

	public Dict getPlist() {
		return plist;
	}

	public String[] getFileTypes() {
		return fileTypes;
	}

	public Rx getFirstLineMatch() {
		return firstLineMatch;
	}

	public String getKeyEquivalent() {
		return keyEquivalent;
	}

	public String getScopeName() {
		return scopeName;
	}

	public String getFilename() {
		return filename;
	}

	public String getComment() {
		return comment;
	}
}
