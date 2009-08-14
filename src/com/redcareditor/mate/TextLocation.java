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
