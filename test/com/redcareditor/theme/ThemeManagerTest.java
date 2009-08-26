package com.redcareditor.theme;

import org.junit.Test;

import static org.junit.Assert.*;
import com.redcareditor.theme.ThemeManager;

public class ThemeManagerTest {
	@Test
	public void shouldLoadThemes() {
		ThemeManager.loadThemes("input/");
		assertEquals(1, ThemeManager.themes.size());
		assertEquals("Railscasts", ThemeManager.themes.get(0).name);
	}
}
