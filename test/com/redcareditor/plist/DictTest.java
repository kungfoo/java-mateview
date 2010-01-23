package com.redcareditor.plist;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class DictTest {
	private Dict dict;

	@Test
	public void testParseSubDicts() {
		dict = Dict.parseFile("input/ruby-subdict.plist");
		assertNotNull(dict);
		// TODO: check the contents!
	}

	@Test
	public void testSimpleStringItem() {
		dict = Dict.parseFile("input/Bundles/Ruby.tmbundle/Syntaxes/Ruby.plist");
		String firstLine = dict.getString("firstLineMatch");
		assertEquals("^#!/.*\\bruby\\b", firstLine);
	}

	@Test
	public void testFileTypes() {
		dict = Dict.parseFile("input/Bundles/Ruby.tmbundle/Syntaxes/Ruby.plist");
		String[] check = { "rb", "rbx", "rjs", "Rakefile", "rake", "cgi", "fcgi", "gemspec", "irbrc", "capfile" };
		String[] types = dict.getStrings("fileTypes");

		assertArrayEquals(check, types);
	}
}
