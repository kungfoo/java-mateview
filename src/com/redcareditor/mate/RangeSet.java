package com.redcareditor.mate;

import java.util.ArrayList;
import java.util.List;

public class RangeSet {
	public class Range {
		public int a;
		public int b;
	}
	
	private List<Range> ranges = new ArrayList<Range>();
	
	public boolean isEmpty() {
		return (ranges.size() == 0);
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
		Range n = ranges.get(ix);
		if (ix > 0) {
			Range x = ranges.get(ix-1);
			if (n.a <= x.b+1) {
				ranges.remove(ix);
				x.b = Math.max(x.b, n.b);
				ranges.set(ix-1, x);
				ix--;
				n = ranges.get(ix);
			}
		}
		if (ix < ranges.size()-1) {
			Range y = ranges.get(ix+1);
			while (ix < ranges.size()-1 && n.b >= y.a-1) {
				y.a = Math.min(n.a, y.a);
				if (n.b > y.b)
					y.b = n.b;
				ranges.set(ix+1, y);
				ranges.remove(ix);
				if (ix < ranges.size()-1)
					y = ranges.get(ix+1);
			}
		}
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
	
	public String present() {
		StringBuilder sb = new StringBuilder("");
		for (Range p : ranges) {
			if (p.b - p.a == 0) {
				sb.append(p.a);
				sb.append(", ");
			}
			else {
				sb.append(p.a);
				sb.append("..");
				sb.append(p.b);
				sb.append(", ");
			}
		}
		return sb.toString();
	}
}
