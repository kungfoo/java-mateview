package com.redcareditor.mate;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.redcareditor.plist.Dict;

public class GrammarTest {
	private Grammar g;

	@Before
	public void setUp() {
		Dict ruby;
		ruby = Dict.parseFile("input/Bundles/Apache.tmbundle/Syntaxes/Apache.plist");
		g = new Grammar(ruby);
		g.initForUse();
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
  public void shouldLoadPatternsWithoutNames() {
    Pattern foundPattern = null;
    for (Pattern p : g.allPatterns) {
      if ((p instanceof DoublePattern) &&
          ((DoublePattern)p).bothCaptures != null &&
          ((DoublePattern)p).bothCaptures.values().contains("support.constant.rewritecond.apache-config")) {
        foundPattern = p;
        break;
      }
    }
    assertNotNull("Unable to find unnamed rewrite pattern", foundPattern);
  }

	@Test
	public void shouldLoadCaptures() {
		for (Pattern p : g.allPatterns) {
			if ("comment.line.number-sign.apache-config".equals(p.name)) {
				assertEquals("punctuation.definition.comment.apache-config", ((SinglePattern) p).captures.get(1));
			}
		}
	}
}
