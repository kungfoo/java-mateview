package com.redcareditor.mate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

public class GrammarTest {
	private static Grammar g;

	@BeforeClass
	public static void setUp() {
		String filename = "input/Bundles/Apache.tmbundle/Syntaxes/Apache.plist";
		g = new Grammar("Apache", filename);
	}

	@Test
	public void shouldLoadGrammarInformation() {
		assertEquals("Apache", g.name);
		assertEquals("source.apache-config", g.scopeName);
	}
	
	public ArrayList<String> patternNames() {
		ArrayList<String> result = new ArrayList<String>();
		for (Pattern p : g.allPatterns) {
			result.add(p.name);
//			System.out.printf("test name: %s\n", p.name);
		}
		return result;
	}
	
	@Test
	public void shouldLoadPatternsIntoMemory() {
		assertTrue("allPatterns is not empty", g.allPatterns.size() > 0);
		assertTrue(patternNames().contains("comment.line.number-sign.apache-config"));
		assertTrue(patternNames().contains("source.include.apache-config"));
		assertTrue(patternNames().contains("support.constant.apache-config"));
	}
	
	@Test
	public void shouldLoadCaptures() {
		for (Pattern p : g.allPatterns) {
			if (p.name == "comment.line.number-sign.apache-config") {
				assertEquals("punctuation.definition.comment.apache-config", ((SinglePattern) p).captures.get(1));
			}
		}
	}
}
