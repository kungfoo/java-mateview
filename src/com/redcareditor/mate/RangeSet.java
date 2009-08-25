package com.redcareditor.mate;

import java.util.ArrayList;
import java.util.Set;

import com.redcareditor.onig.Range;

public class RangeSet extends ArrayList<Range> implements Set<Range>{
	
	public RangeSet(){
		super();
	}
	
	public void add(int a, int b) {
		int insertAt = 0;
		Range range = new Range(a,b);

		while(insertAt < length() && get(insertAt).start < range.start){
			insertAt++;
		}
		
		merge(insertAt, range);
	}
	
	private void merge(int mergeAt, Range range) {
		add(mergeAt, range);
		
		if (mergeAt > 0) {
			Range beforeMerge = get(mergeAt-1);
			if (range.touch(beforeMerge)) {
				remove(mergeAt);
				beforeMerge.end = Math.max(beforeMerge.end, range.end);
				mergeAt--;
				range = get(mergeAt);
			}
		}
		
		if (mergeAt+1 < length()) {
			Range afterMerge = get(mergeAt+1);
			while (mergeAt < length()-1 && range.touch(afterMerge)) {
				range.start = Math.min(range.start, afterMerge.start);
				range.end = Math.max(range.end, afterMerge.end);
				remove(mergeAt+1);
				
				if (mergeAt+1 < length())
					afterMerge = get(mergeAt+1);
			}
		}
	}
	
	public int length() {
		return super.size();
	}
	
	public int rangeSize() {
		int sizec = 0;
		for (int i=0;i<length();i++){
			sizec += get(i).end - get(i).start + 1;
		}
		return sizec;
	}
	
	public String present() {
		StringBuilder sb = new StringBuilder("");
		for (int i=0;i<length();i++){
			if (get(i).end - get(i).start == 0) {
				sb.append(get(i).start);
				sb.append(", ");
			}
			else {
				sb.append(get(i).start);
				sb.append("..");
				sb.append(get(i).end);
				sb.append(", ");
			}
		}
		return sb.toString();
	}
}
