package com.redcareditor.mate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.redcareditor.plist.Dict;

public class Bundle {
	public String name;
	public List<Grammar> grammars;
	public static List<Bundle> bundles;

	public Bundle(String name) {
		this.name = name;
		this.grammars = new ArrayList<Grammar>();
	}

	public static Bundle getBundleByName(String findName) {
		for (Bundle b : bundles) {
			if (b.name.equals(findName))
				return b;
		}
		return null;
	}

	/**
	 * Return a list of bundle names like "Ruby.tmbundle"
	 */
	public static List<String> bundleDirs(String textmateDir) {
		List<String> names = new ArrayList<String>();
		File dir = new File(textmateDir + "/Bundles");
		if (dir.exists()) {
			for (String name : dir.list()) {
				if (name.endsWith(".tmbundle")) {
					names.add(name);
				}
			}
		}
		return names;
	}

	public static void loadBundles(String textmateDir) {
		if (bundles != null) {
			return;
		}
		bundles = new ArrayList<Bundle>();
		for (String bundleDir : bundleDirs(textmateDir)) {
			Bundle bundle = new Bundle(bundleDir.split("\\.")[0]);
			bundles.add(bundle);
			File syntaxDir = new File(textmateDir + "/Bundles/" + bundleDir + "/Syntaxes");
			if (syntaxDir.exists()) {
				loadSyntax(bundle, syntaxDir);
			}
		}

		for (Bundle b : bundles) {
			for (Grammar g : b.grammars) {
				g.initForReference();
			}
		}
	}

	private static void loadSyntax(Bundle bundle, File syntaxDir) {
		for (String syntaxFileName : syntaxDir.list()) {
			if (isTmBundlefile(syntaxFileName)) {
				Dict plist = Dict.parseFile(syntaxDir.getPath() + "/" + syntaxFileName);
				if (plist != null) {
					Grammar grammar = new Grammar(plist);
					grammar.fileName = syntaxFileName;
					bundle.grammars.add(grammar);
				}
			}
		}
	}

	private static boolean isTmBundlefile(String syntaxFileName) {
		return (syntaxFileName.endsWith(".tmLanguage") || syntaxFileName.endsWith(".plist"));
	}
}