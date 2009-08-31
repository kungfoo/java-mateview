package com.redcareditor.mate;

public class TextLocation implements Comparable<TextLocation> {
	public int line;
	public int lineOffset;

	public TextLocation(int line, int lineOffset) {
		this.line = line;
		this.lineOffset = lineOffset;
	}

	public int compareTo(TextLocation o) {
		if (line > o.line) {
			return 1;
		} else if (line < o.line) {
			return -1;
		} else {
			if (lineOffset > o.lineOffset) {
				return 1;
			} else if (lineOffset < o.lineOffset) {
				return -1;
			} else {
				return 0;
			}
		}
	}

	public static boolean gt(TextLocation t1, TextLocation t2) {
		return ((t1.line > t2.line) || (t1.line >= t2.line && t1.lineOffset > t2.lineOffset));
	}

	public static boolean lt(TextLocation t1, TextLocation t2) {
		return (!t1.equals(t2) && !TextLocation.gt(t1, t2));
	}
	
	public static boolean gte(TextLocation t1, TextLocation t2) {
		return (!TextLocation.lt(t1, t2));
	}
	
	public static boolean lte(TextLocation t1, TextLocation t2) {
		return (!TextLocation.gt(t1, t2));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TextLocation) {
			return ((TextLocation) obj).compareTo(this) == 0;
		}
		return false;
	}

	@Override
	public String toString() {
		return String.format("{%d,%d}", line, lineOffset);
	}
}
