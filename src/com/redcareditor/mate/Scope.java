
package com.redcareditor.mate;

public class Scope {
	public MateText mateText;
	public String name;
	public Pattern pattern;
	
	public boolean isOpen;
	public boolean isCapture;
	
	StringBuilder prettyString;
	int indent;
	
	
	public Scope(MateText mt, String n) {
		mateText = mt;
		name = n;
	}
	
	public void clearAfter(int lineIx, int something) {
		
	}
	
	public String pretty(int indent) {
		prettyString = new StringBuilder("");
		this.indent = indent;
		for (int i = 0; i < indent; i++)
			prettyString.append("  ");
		if (isCapture)
			prettyString.append("c");
		else
			prettyString.append("+");
		prettyString.append(" " + name);
		if (pattern instanceof DoublePattern && 
				isCapture == false && 
				((DoublePattern) pattern).contentName != null) 
			prettyString.append(" " + ((DoublePattern) pattern).contentName);
		prettyString.append(" (");
//		if (start_mark == null) {
//			prettyString.append("inf");
//		}
//		else {
//			prettyString.append("%d,%d".printf(buffer.iter_from_mark(start_mark).get_line(), 
//												 buffer.iter_from_mark(start_mark).get_line_offset()));
//		}
//		prettyString.append(")-(");
//		if (end_mark == null) {
//			prettyString.append("inf");
//		}
//		else {
//			prettyString.append("%d,%d".printf(buffer.iter_from_mark(end_mark).get_line(), 
//												 buffer.iter_from_mark(end_mark).get_line_offset()));
//		}
		prettyString.append(")");
		prettyString.append((isOpen ? " open" : " closed"));
		prettyString.append("\n");

		this.indent += 1;
//		GLib.SequenceIter iter = children.get_begin_iter();
//		while (!iter.is_end()) {
//			prettyString.append(children.get(iter).pretty(this.indent));
//			iter = iter.next();
//		}
//		this.indent -= 1;
		return prettyString.toString();
	}
}
