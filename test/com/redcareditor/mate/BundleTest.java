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
		String[] bundleNames = new String[] { "Apache", "Ruby", "HTML", "CSS", "Perl" };
		for (String bundleName : bundleNames) {
			containsBundleNamed(bundleName);
		}
		assertEquals(9, Bundle.bundles.size());
	}

	private void containsBundleNamed(String bundleName) {
		assertNotNull(Bundle.getBundleByName(bundleName));
	}

	@Test
	public void shouldHaveCreatedCorrectGrammars() {
		assertEquals(1, Bundle.getBundleByName("Apache").grammars.size());
		assertEquals(1, Bundle.getBundleByName("Ruby").grammars.size());
	}
}
