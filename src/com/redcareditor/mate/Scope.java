
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
	public ArrayList<Scope> children;
	
	StringBuilder prettyString;
	int indent;
	
	public Scope(MateText mt, String name) {
		this.mateText = mt;
		this.name = name;
		this.children = new ArrayList<Scope>();
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
		Scope scope = this;
		while ((scope.pattern instanceof SinglePattern || 
			      scope.isCapture || 
			      (scope.getStart().getLine() == lineIx && scope.getStart().getLineOffset() == 0)) && 
					 scope.parent != null) {
			scope = scope.parent;
		}
		return scope;
	}
	
	public boolean surfaceIdenticalTo(Scope other) {
		if (surfaceIdenticalToModuloEnding(other) &&
				getEnd().equals(other.getEnd()) &&
				getInnerEnd().equals(other.getInnerEnd()) &&
				beginMatchString == other.beginMatchString) {
			return true;
		}
		return false;
	}

	public boolean surfaceIdenticalToModuloEnding(Scope other) {
		// stdout.printf("%s == %s and %s == %s and %s == %s and %s == %s and %s == %s",
		// 			  name, other.name, pattern.name, other.pattern.name, start_loc().to_s(),
		// 			  other.start_loc().to_s(), inner_start_loc().to_s(), other.inner_start_loc().to_s(),
		// 			  begin_match_string, other.begin_match_string);
		if (name == other.name &&
				pattern == other.pattern &&
				getStart().equals(other.getStart()) &&
				getInnerStart().equals(other.getInnerStart()) &&
				beginMatchString == other.beginMatchString) {
			return true;
		}
		return false;
	}

	public boolean overlapsWith(Scope other) {
		// sd1    +---
		// sd2  +---
		if (getStart().compareTo(other.getStart()) >= 0) {
			if (getStart().compareTo(other.getEnd()) < 0) {
				return true;
			}
			return false;
		}

		// sd1 +---
		// sd2   +---
		if (getEnd().compareTo(other.getStart()) > 0) {
			return true;
		}
		return false;
	}

	public void addChild(Scope newChild) {
		if (children.size() == 0){
			children.add(newChild);
			return;
		}

		if (children.get(0).getStart().getOffset() > newChild.getStart().getOffset()){
			children.add(0, newChild);
			return;
		}

		int insertIx = 0;
		int ix = 1;
		for (Scope child : children) {
			if (child.getStart().getOffset() <= newChild.getStart().getOffset()) {
				insertIx = ix;
			}
			ix++;
		}
		children.add(insertIx, newChild);
//		children.add(newChild);
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
		ArrayList<Scope> removedScopes = new ArrayList<Scope>();
		for (Scope child : children) {
			int childStartLine = child.getStart().getLine();
			if (childStartLine == lineIx && !scopes.contains(child)) {
				System.out.printf("deleteAnyOnLineNotIn removing: %s\n", child.pattern.name);
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

	public MateTextLocation getInnerStart(){
		return innerRange.getStart();
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
