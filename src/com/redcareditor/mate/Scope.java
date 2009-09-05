
package com.redcareditor.mate;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import com.redcareditor.mate.document.MateDocument;
import com.redcareditor.mate.document.MateTextLocation;
import com.redcareditor.mate.document.MateTextRange;
import com.redcareditor.onig.Match;
import com.redcareditor.onig.Rx;

public class Scope implements Comparable<Scope>{
	private MateText mateText;
	
	private MateDocument document;
	
	private MateTextRange range;
	private MateTextRange innerRange;
	
	public String name;
	public Pattern pattern;
	
	public boolean isOpen;
	public boolean isCapture;
	
	public Match openMatch;
	public Match closeMatch;
	
	public Rx closingRegex;
	public String beginMatchString;
	public String endMatchString;
	
	public Scope parent;
	public SortedSet<Scope> children;
	
	StringBuilder prettyString;
	int indent;
	
	public Scope(MateText mt, String name) {
		this.mateText = mt;
		this.name = name;
		this.children = new TreeSet<Scope>();
		this.document = mt.getMateDocument();
		
		this.range = document.getTextRange();
		this.innerRange = document.getTextRange();
	}
	
	public void clearAfter(int lineIx, int something) {
		// TODO: port this method
	}
	
	public Scope scopeAt(int line, int lineOffset) {
		MateTextLocation location = document.getTextLocation(line, lineOffset);
		
		if (getStart().compareTo(location) <= 0 || parent == null) {
			if (isOpen || getEnd().compareTo(location) >= 0) {
				for (Scope child : children) {
					if (child.contains(location)) {
						return child.scopeAt(line, lineOffset);
					}
				}
				return this;
			}
			
		}

		return null;
	}
	
	public int compareTo(Scope o) {
		if(getStart().compareTo(o.getStart()) == 0){
			return getEnd().compareTo(o.getEnd());
		}
		return getStart().compareTo(o.getStart());
	}

	public Scope containingDoubleScope(int lineIx) {
		// TODO: port this method
		return this;
	}
	
	public boolean surfaceIdenticalTo(Scope other) {
		// TODO: port me
		return false;
	}

	public boolean surfaceIdenticalToModuloEnding(Scope other) {
		// TODO: port me
		return false;
	}

	public boolean overlapsWith(Scope other) {
		// TODO: port me
		return false;
	}

	public void addChild(Scope newChild) {
		children.add(newChild);
	}
	
	public void removeChild(Scope child) {
		children.remove(child);
	}

	public Scope firstChildAfter(MateTextLocation location) {
		for (Scope child : children) {
			if (child.getStart().compareTo(location) >= 0) {
				return child;
			}
		}
		return null;
	}
	
	public ArrayList<Scope> deleteAnyOnLineNotIn(int lineIx, ArrayList<Scope> scopes) {
//		var start_scope = scope_at(line_ix, -1);
		ArrayList<Scope> removedScopes = new ArrayList<Scope>();
//		var iter = children.get_begin_iter();
		for (Scope child : children) {
			int childStartLine = child.getStart().getLine();
			if (childStartLine == lineIx && !scopes.contains(child)) {
				removedScopes.add(child);
			}
		}
		children.removeAll(removedScopes);
		return removedScopes;
	}
	
	public void setStartPos(int line, int lineOffset, boolean hasLeftGravity) {
		MateTextLocation start = document.getTextLocation(line, lineOffset);
		this.range.setStart(start);
		document.addTextLocation(start);
	}

	public void setInnerStartPos(int line, int lineOffset, boolean hasLeftGravity) {
		MateTextLocation innerStart = document.getTextLocation(line, lineOffset);
		this.innerRange.setStart(innerStart);
		document.addTextLocation(innerStart);
	}

	public void setInnerEndPos(int line, int lineOffset, boolean c) {
		MateTextLocation innerEnd = document.getTextLocation(line, lineOffset);
		this.innerRange.setEnd(innerEnd);
		document.addTextLocation(innerEnd);
	}

	public void setEndPos(int line, int lineOffset, boolean c) {
		MateTextLocation end = document.getTextLocation(line, lineOffset);
		this.range.setEnd(end);
		document.addTextLocation(end);
	}
	
	public int getLength(){
		return range.getLength();
	}
	
	public MateTextLocation getStart() {
		return range.getStart();
	}
	
	public MateTextLocation getEnd(){
		return range.getEnd();
	}
	
	public MateTextLocation getInnerEnd(){
		return innerRange.getEnd();
	}
		
	public boolean contains(MateTextLocation location){
		return range.conatains(location);
	}
	
	public String pretty(int indent) {
		prettyString = new StringBuilder("");
		this.indent = indent;
		for (int i = 0; i < indent; i++)
			prettyString.append("  ");
		if (this.isCapture)
			prettyString.append("c");
		else
			prettyString.append("+");
		
		if (this.name != null)
			prettyString.append(" " + this.name);
		else
			prettyString.append(" " + "[noname]");
		
		if (this.pattern instanceof DoublePattern && 
				this.isCapture == false && 
				((DoublePattern) this.pattern).contentName != null) 
			prettyString.append(" " + ((DoublePattern) this.pattern).contentName);
		prettyString.append(" (");
		prettyString.append(String.format(
				"%d,%d",
				getStart().getLine(), 
				getStart().getLineOffset()));
//		prettyString.append(getStart().getOffset());
		prettyString.append(")-(");
		prettyString.append(String.format(
				"%d,%d",
				getEnd().getLine(), 
				getEnd().getLineOffset()));
//		prettyString.append(getEnd().getOffset());
		prettyString.append(")");
		prettyString.append((isOpen ? " open" : " closed"));
		prettyString.append("\n");

		this.indent += 1;
		for (Scope child : this.children) {
			prettyString.append(child.pretty(this.indent));
		}
		
		return prettyString.toString();
	}
}
