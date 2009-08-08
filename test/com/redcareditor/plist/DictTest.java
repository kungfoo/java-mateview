package com.redcareditor.plist;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.redcareditor.plist.Dict;
import com.redcareditor.plist.PlistNode;

public class DictTest {
	private Dict dict;

	@Before
	public void setup() {
		dict = Dict.parseFile("input/Ruby.plist");
		assertNotNull(dict);
	}

	@Test
	public void testSimpleStringItem() {
		String firstLine = dict.getString("firstLineMatch");
		assertEquals("^#!/.*\\bruby\\b", firstLine);
	}

	@Test
	public void testFileTypes() {
		String[] check = { "rb", "rbx", "rjs", "Rakefile", "rake", "cgi",
				"fcgi", "gemspec", "irbrc", "capfile" };
		String[] types = dict.getStrings("fileTypes");

		assertArrayEquals(check, types);
	}
}
