package com.redcareditor.mate;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class BundleTest {

	@Before
	public void setUp() {
		Bundle.loadBundles("input/");
	}
	
	@Test
	public void shouldHaveCreatedCorrectBundles() {
		assertTrue(Bundle.getBundleByName("Apache") != null);
		assertTrue(Bundle.getBundleByName("Ruby") != null);
		assertTrue(Bundle.getBundleByName("HTML") != null);
		assertTrue(Bundle.getBundleByName("CSS") != null);
		assertTrue(Bundle.getBundleByName("Perl") != null);
		assertEquals(9, Bundle.bundles.size());
	}
	
	@Test
	public void shouldHaveCreatedCorrectGrammars() {
		assertEquals(1, Bundle.getBundleByName("Apache").grammars.size());
		assertEquals(1, Bundle.getBundleByName("Ruby").grammars.size());
	}
}
