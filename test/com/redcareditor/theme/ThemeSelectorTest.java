package com.redcareditor.theme;

import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

public class ThemeSelectorTest {
	@Test
	public void testSinglePositiveSelector(){
		String selector = "entity.name.function";
		List<ThemeSelector> selectors = ThemeSelector.compile(selector);
		assertEquals(1, selectors.size());
		
		ThemeSelector s = selectors.get(0);
		assertEquals("(entity\\.name\\.function)", s.positiveRegex.pattern);
	}
	
	@Test
	public void testMultiplePositiveSelectors(){
		String selector = "meta.tag, declaration.tag, entity.name.tag, entity.other.attribute-name";
		List<ThemeSelector> selectors = ThemeSelector.compile(selector);
		assertEquals(4, selectors.size());
		
		ThemeSelector s1 = selectors.get(0);
		assertEquals("(meta\\.tag)", s1.positiveRegex.pattern);
		
		ThemeSelector s3 = selectors.get(2);
		assertEquals("(entity\\.name\\.tag)", s3.positiveRegex.pattern);
	}
	
	@Test
	public void testPositiveAndNegativeSelector(){
		String selector = "source.ruby string - string source";
		List<ThemeSelector> selectors = ThemeSelector.compile(selector);
		
	}
}
