package com.redcareditor.mate;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.joni.Regex;

import com.redcareditor.onig.Match;
import com.redcareditor.onig.Rx;

public class ScopeMatcher {
	public Rx pos_rx;
	public ArrayList<Rx> neg_rxs;

	public static ArrayList<Integer> occurrences(String target, String find) {
		ArrayList<Integer> positions = new ArrayList<Integer>();
		int fromIndex = 0;
		int newIndex = -1;
		while ((newIndex = target.indexOf(find, fromIndex)) != -1) {
			positions.add(newIndex);
			fromIndex = newIndex + 1;
		}
		return positions;
	}
	 
	// returns 1 if m1 is better than m2, -1 if m1 is worse than m2, 0 if equally good
	public static int compareMatch(String scopeString, Match m1, Match m2) {
		ArrayList<Integer> spaceIxs = occurrences(scopeString, " ");
		int max_cap1 = m1.numCaptures();
		int max_cap2 = m2.numCaptures();
		int cap1_ix, cap1_el_ix, len1;
		int cap2_ix, cap2_el_ix, len2;
		for (int i = 0; i < Math.min(max_cap1, max_cap2); i++) {
			// first try element depth:
			cap1_ix = m1.begin(max_cap1-1-i);
			cap2_ix = m2.begin(max_cap2-1-i);
			cap1_el_ix = ScopeMatcher.sorted_ix(spaceIxs, cap1_ix);
			cap2_el_ix = ScopeMatcher.sorted_ix(spaceIxs, cap2_ix);
			if (cap1_el_ix > cap2_el_ix) {
				return 1;
			}
			else if (cap1_el_ix < cap2_el_ix) {
				return -1;
			}

			// next try length of match
			len1 = m1.end(max_cap1-1-i) - cap1_ix;
			len2 = m2.end(max_cap2-1-i) - cap2_ix;
			if (len1 > len2) {
				return 1;
			}
			else if (len1 < len2) {
				return -1;
			}
		}
		return 0;
	}
	
	private static int sorted_ix(ArrayList<Integer> ixs, int val) {
		if (ixs.size() == 0)
			return 0;
		if (val < ixs.get(0))
			return 0;
		if (ixs.size() == 1) {
			if (val > ixs.get(0))
				return 1;
			else
				return 0;
		}
		else {
			for (int i = 0; i < ixs.size()-1; i++) {
				if (val > ixs.get(i) && val < ixs.get(i+1))
					return i+1;
			}
			return ixs.size();
		}
	}

	// this method is mainly for testing in the Ruby specs
	public static String testRank(String selector_a, String selector_b, String scope_string) {
		Match m1 = match(selector_a, scope_string);
		Match m2 = match(selector_b, scope_string);
		int r = compareMatch(scope_string, m1, m2);
		if (r > 0) {
			return selector_a;
		}
		else if (r == 0 ){
			return selector_a + " == " + selector_b;
		}
		else {
			return selector_b;
		}
	}
	
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