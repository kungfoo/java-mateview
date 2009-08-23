package com.redcareditor.mate;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


public class FilledRangeSetTest {
	private RangeSet rs;

	@Before
	public void setUp() throws Exception {
		rs = new RangeSet();
		rs.add(1, 3);
		rs.add(5, 5);
		rs.add(10, 15);
	}

	@Test
	public void testShouldReportLength() {
		assertEquals(3, rs.length());
	}

	@Test
	public void testShouldReportSize() {
		assertEquals(10, rs.size());
	}

	@Test
	public void testShouldMergeRanges() {
		rs.add(14, 16);
		assertEquals("1..3, 5, 10..16, ", rs.present());
	}

	@Test
	public void testShouldMergeRanges2() {
		rs.add(7, 11);
		assertEquals("1..3, 5, 7..15, ", rs.present());
	}

	@Test
	public void testShouldMergeTwoRanges() {
		rs.add(4, 11);
		assertEquals("1..15, ", rs.present());
	}

	@Test
	public void testShouldMergeAllRanges() {
		rs.add(1, 20);
		assertEquals("1..20, ", rs.present());
	}

	@Test
	public void testShouldMergeAdjacentRanges() {
		rs.add(16, 18);
		assertEquals("1..3, 5, 10..18, ", rs.present());
	}

	@Test
	public void testShouldMergeTwoAdjacentRanges() {
		rs.add(4, 4);
		assertEquals("1..5, 10..15, ", rs.present());
	}
}


