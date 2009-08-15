package com.redcareditor.theme;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread-safe class that will load the themes if necessary.
 */
public class ThemeManager {
	private static Lock mutex;
	private static List<Theme> themes;

	static {
		mutex = new ReentrantLock();
		loadThemes();
	}

	/**
	 * loads (if necessary) and return the themes currently deployed for this.
	 */
	public static List<Theme> getThemes() {
		try {
			mutex.lock();
			themes = new ArrayList<Theme>();
			if (!initialized()) {
				loadThemes();
			}
			mutex.unlock();
		} finally {
			mutex.unlock();
		}
		return themes;
	}

	public static void addTheme(Theme theme) {
		try {
			mutex.lock();
			themes.add(theme);
			mutex.unlock();
		} finally {
			mutex.unlock();
		}
	}

	private static void loadThemes() {

	}

	private static boolean initialized() {
		return themes != null;
	}
}
