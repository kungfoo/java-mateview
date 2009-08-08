package com.redcareditor.mate;

import static org.junit.Assert.*;

import org.junit.Test;

import com.redcareditor.plist.Dict;


public class GrammarTest {
	@Test
	public void testInitForReference(){
		Dict ruby = Dict.parseFile("input/Ruby.plist");
		Grammar g = new Grammar(ruby);
		g.initForReference();
		
		assertEquals("Ruby", g.getName());
	}
}
