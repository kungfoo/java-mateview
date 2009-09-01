package com.redcareditor.onig;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.joni.Regex;
import org.joni.Region;

public class Match implements Iterable<Range> {
	private Regex regex;
	private Region region;
	private String text;

	public Match() {}
	
	public Match(Regex regex, Region region, String text) {
		super();
		this.regex = regex;
		this.region = region;
		this.text = text;
	}

	public int numCaptures() {
		return region.numRegs;
	}

	public Range getCapture(int capture){
		checkBounds(capture);
		return new Range(
				region.beg[capture],
				region.end[capture]
			);
	}

	private void checkBounds(int capture) {
//		System.out.printf("checkBounds(%d) (out of %d)\n", capture, numCaptures()-1);
		if (capture > numCaptures()-1 || capture < 0) {
			throw new IllegalArgumentException("Capture Index out of bounds!");
		}
	}

	public List<Range> ranges() {
		List<Range> result = new ArrayList<Range>();
		for (Range r : this) {
			result.add(r);
		}
		return result;
	}
	
	@Override
	public String toString() {
		StringBuilder bui = new StringBuilder();
		bui.append(text);
		bui.append(region);
		return bui.toString();
	}

	public Iterator<Range> iterator() {
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
