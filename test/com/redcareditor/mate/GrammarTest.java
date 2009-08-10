package com.redcareditor.mate;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.redcareditor.plist.Dict;

public class GrammarTest {
	private Dict ruby;
	private Grammar g;

	@Before
	public void setUp() {
		ruby = Dict.parseFile("input/Ruby.plist");
		g = new Grammar(ruby);
	}

	@Test
	public void testInitForReference() {
		g.initForReference();
		assertEquals("Ruby", g.name);
	}

	@Test
	public void testInitForUse() {
		g.initForUse();
	}
}
