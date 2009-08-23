package com.redcareditor.mate;

import java.util.ArrayList;

public class RangeSet {
	public class Range {
		public int a;
		public int b;
	}
	
	private ArrayList<Range> ranges = new ArrayList<Range>();
	
	public boolean isEmpty() {
		return true;
	}
	
	public void add(int a, int b) {
		int insert_ix = 0;
		Range n = new Range();
		n.a = a;
		n.b = b;
		for (Range p : ranges) {
			if (p.a < n.a)
				insert_ix++;
		}
		ranges.add(insert_ix, n);
		merge(insert_ix);
	}
	
	public void merge(int ix) {
		
	}
	
	public int length() {
		return ranges.size();
	}
	
	public int size() {
		int sizec = 0;
		for (Range p : this.ranges) {
			sizec += p.b - p.a + 1;
		}
		return sizec;
	}
}
