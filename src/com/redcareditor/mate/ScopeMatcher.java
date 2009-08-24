package com.redcareditor.mate;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.joni.Regex;

import com.redcareditor.onig.Match;
import com.redcareditor.onig.Rx;

public class ScopeMatcher {
	public Rx pos_rx;
	public ArrayList<Rx> neg_rxs;

	public static boolean testMatch(String selectorString, String scopeString) {
		Match m = getMatch(selectorString, scopeString);
		return (m != null);
	}
	
	public static Match getMatch(String selectorString, String scopeString) {
		Match m = match(selectorString, scopeString);
		if (m != null) {
			System.out.printf("%d\n", m.numCaptures());
			System.out.printf("test_match('%s', '%s') == %d\n", selectorString, scopeString, m.begin(0));
		}
		else {
			System.out.printf("test_match('%s', '%s') == null\n", selectorString, scopeString);
		}
		return m;
	}
	
	public static Match match(String selectorString, String scopeString) {
		ArrayList<ScopeMatcher> matchers = ScopeMatcher.compile(selectorString);
		for (ScopeMatcher matcher : matchers) {
			Match m;
			if ((m = testMatchRe(matcher.pos_rx, matcher.neg_rxs, scopeString)) != null)
				return m;
		}
		return null;
	}

	public static ArrayList<ScopeMatcher> compile(String selectorString) {
		ArrayList<ScopeMatcher> ms = new ArrayList<ScopeMatcher>();
		// FIXME should validate and throw UTF8 error if bad.
		String[] scopeOrs1 = selectorString.split(",");
		System.out.printf("match: selector: '%s'\n", selectorString);
		for (String selectorString1 : scopeOrs1) {
			ScopeMatcher m = new ScopeMatcher();
			m.neg_rxs = new ArrayList<Rx>();
			String[] positivesAndNegatives = selectorString1.split(" -");
			for (String subSelectorString : positivesAndNegatives) {
				if (m.pos_rx == null) {
					String s1 = subSelectorString.trim().replaceAll("\\.", "\\\\.");
					String s2 = s1.replaceAll(" ", ").* .*(");
					System.out.printf("positive '%s'\n", "("+s2+")");
					m.pos_rx = Rx.createRx("("+s2+")");
				}
				else {
					String s1 = subSelectorString.trim().replaceAll("\\.", "\\\\.");
					String s2 = s1.trim().replaceAll(" ", ".* .*");
					System.out.printf("negative '%s'\n", s2);
					m.neg_rxs.add(Rx.createRx(s2));
				}
			}
			ms.add(m);
		}
		return ms;
	}

	public static Match testMatchRe(Rx positiveSelectorRegex, 
									  ArrayList<Rx> negativeSelectorRegexes,
									  String scopeString) {
		Match m = positiveSelectorRegex.search(scopeString, 0, scopeString.length());
		if (m != null) {
			for (Rx negRx : negativeSelectorRegexes) {
				Match m1 = negRx.search(scopeString, 0, scopeString.length());
				if (m1 != null) {
					return null;
				}
			}
			return m;
		}
		else {
			return null;
		}
	}
	
}