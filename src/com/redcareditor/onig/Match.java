package com.redcareditor.onig;

import java.util.Iterator;

import org.joni.Regex;
import org.joni.Region;

public class Match {
	Regex regex;
	Region region;
	String text;

	public Match(Regex regex, Region region, String text) {
		super();
		this.regex = regex;
		this.region = region;
		this.text = text;
	}

	public int numCaptures() {
		return region.numRegs;
	}

	public int begin(int capture) {
		checkBounds(capture);
		return region.beg[capture];
	}

	public int end(int capture) {
		checkBounds(capture);
		return region.end[capture];
	}

	private void checkBounds(int capture) {
		if (capture >= regex.numberOfCaptures() || capture < 0) {
			throw new IllegalArgumentException("Capture Index out of bounds!");
		}
	}

	public Iterator<Range> ranges() {
		return new Iterator<Range>() {
			int i = 0;

			public boolean hasNext() {
				return i < numCaptures();
			}

			public Range next() {
				Range r = new Range(region.beg[i], region.end[i]);
				i++;
				return r;
			}

			public void remove() {
				throw new UnsupportedOperationException("no removing!");
			}
		};
	}
}
