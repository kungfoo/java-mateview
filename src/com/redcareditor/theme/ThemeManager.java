package com.redcareditor.theme;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReentrantLock;

import com.redcareditor.plist.Dict;

/**
 * Thread-safe class that will load the themes if necessary.
 */
public class ThemeManager {
//	private static Lock mutex;
	public static List<Theme> themes;
//
//	static {
//		mutex = new ReentrantLock();
//	}
//
//	/**
//	 * loads (if necessary) and return the themes currently deployed for this.
//	 */
//	public static List<Theme> getThemes() {
//		try {
//			mutex.lock();
//			themes = new ArrayList<Theme>();
//			if (!initialized()) {
//				loadThemes();
//			}
//			mutex.unlock();
//		} finally {
//			mutex.unlock();
//		}
//		return themes;
//	}
//
//	public static void addTheme(Theme theme) {
//		try {
//			mutex.lock();
//			themes.add(theme);
//			mutex.unlock();
//		} finally {
//			mutex.unlock();
//		}
//	}

	public static List<String> themeNames(String textmateDir) {
		List<String> names = new ArrayList<String>();
		File dir = new File(textmateDir + "/Themes");
		if (dir.exists()) {
			for (String name : dir.list()) {
				if (name.endsWith(".tmTheme")) {
					names.add(name);
				}
			}
		}
		return names;
	}
	
	public static void loadThemes(String textmateDir) {
		if (themes != null) {
			return;
		}
		themes = new ArrayList<Theme>();
		for (String themeName : themeNames(textmateDir)) {
			Dict dict = Dict.parseFile(textmateDir + "/Themes/" + themeName);
			if (dict != null) {
				Theme theme = new Theme(dict);
				themes.add(theme);
			}
		}
	}

	private static boolean initialized() {
		return themes != null;
	}
}
