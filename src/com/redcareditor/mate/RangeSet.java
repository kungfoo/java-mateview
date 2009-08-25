package com.redcareditor.mate;

import java.util.ArrayList;

import com.redcareditor.onig.Range;

public class RangeSet {
	private ArrayList<Range> ranges;
	
	public RangeSet(){
		ranges = new ArrayList<Range>();
	}
	
	public boolean isEmpty() {
		return ranges.isEmpty();
	}
	
	public void add(int a, int b) {
		int insertAt = 0;
		Range range = new Range(a,b);

		while(insertAt < ranges.size() && ranges.get(insertAt).start < range.start){
			insertAt++;
		}
		
		merge(insertAt, range);
	}
	
	private void merge(int mergeAt, Range range) {
		ranges.add(mergeAt, range);
		
		if (mergeAt > 0) {
			Range beforeMerge = ranges.get(mergeAt-1);
			if (range.touch(beforeMerge)) {
				ranges.remove(mergeAt);
				beforeMerge.end = Math.max(beforeMerge.end, range.end);
				mergeAt--;
				range = ranges.get(mergeAt);
			}
		}
		
		if (mergeAt+1 < ranges.size()) {
			Range afterMerge = ranges.get(mergeAt+1);
			while (mergeAt < ranges.size()-1 && range.touch(afterMerge)) {
				range.start = Math.min(range.start, afterMerge.start);
				range.end = Math.max(range.end, afterMerge.end);
				ranges.remove(mergeAt+1);
				
				if (mergeAt+1 < ranges.size())
					afterMerge = ranges.get(mergeAt+1);
			}
		}
	}
	
	public int length() {
		return ranges.size();
	}
	
	public int size() {
		int sizec = 0;
		for (Range p : this.ranges) {
			sizec += p.end - p.start + 1;
		}
		return sizec;
	}
	
	public String present() {
		StringBuilder sb = new StringBuilder("");
		for (Range p : ranges) {
			if (p.end - p.start == 0) {
				sb.append(p.start);
				sb.append(", ");
			}
			else {
				sb.append(p.start);
				sb.append("..");
				sb.append(p.end);
				sb.append(", ");
			}
		}
		return sb.toString();
	}
}
