package com.redcareditor.mate;

import org.junit.Test;
import static org.junit.Assert.*;

public class TextLocationTest {
	@Test
	public void testToString(){
		TextLocation loc = new TextLocation(10, 15);
		assertEquals("{10,15}", loc.toString());
	}
	
	@Test
	public void testEquals(){
		TextLocation l1 = new TextLocation(10, 10);
		TextLocation l2 = new TextLocation(10, 10);
		assertTrue(l1.equals(l2));
		assertTrue(l2.equals(l1));
		
		TextLocation l3 = new TextLocation(10, 11);
		assertFalse(l3.equals(l1));
		assertFalse(l2.equals(l3));
		
		Integer i = new Integer(12);
		assertFalse(l1.equals(i));
	}
	
	@Test
	public void testLessThan(){
		TextLocation l1 = new TextLocation(9, 10);
		TextLocation l2 = new TextLocation(10, 10);
		
		assertEquals(-1, l1.compareTo(l2));
		
		TextLocation l3 = new TextLocation(9, 11);
		assertEquals(-1, l1.compareTo(l3));
	}
	
	@Test
	public void testBigerThan(){
		TextLocation l1 = new TextLocation(10,10);
		TextLocation l2 = new TextLocation(9, 10);
		
		assertEquals(1, l1.compareTo(l2));
		
		TextLocation l3 = new TextLocation(10, 3);
		assertEquals(1, l1.compareTo(l3));
	}
}
