
package com.redcareditor.mate;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Position;
import org.eclipse.swt.custom.StyledText;

import com.redcareditor.onig.Match;
import com.redcareditor.onig.Rx;

public class Scope implements Comparable<Scope>{
	private MateText mateText;
	private StyledText styledText;
	
	public String name;
	public Pattern pattern;
	
	public boolean isOpen;
	public boolean isCapture;
	
	public Match openMatch;
	public Match closeMatch;
	
	public Position startPos;
	public Position innerStartPos;
	public Position innerEndPos;
	public Position endPos;
	
	public Rx closingRegex;
	public String beginMatchString;
	public String endMatchString;
	
	public Scope parent;
	public SortedSet<Scope> children;
	
	StringBuilder prettyString;
	int indent;
	
	public Scope(MateText mt, String name) {
		this.mateText = mt;
		this.styledText = mt.getTextWidget();
		this.name = name;
		this.children = new TreeSet<Scope>();
	}
	
	public void clearAfter(int lineIx, int something) {
		// TODO: port this method
	}

	public Scope scopeAt(int line, int lineOffset) {
		TextLocation loc = new TextLocation(line, lineOffset);
		
		if (startLoc().compareTo(loc) <= 0 || parent == null) {
			if (isOpen || endLoc().compareTo(loc) >= 0) {
				for (Scope child : children) {
					if (child.containsLoc(loc)) {
						return child.scopeAt(line, lineOffset);
					}
				}
				return this;
			}
			
		}

		return null;
	}
	
	public int compareTo(Scope o) {
		if(startLoc().compareTo(o.startLoc()) == 0){
			return endLoc().compareTo(o.endLoc());
		}
		return startLoc().compareTo(o.startLoc());
	}
	
	private boolean containsLoc(TextLocation loc) {
		return startLoc().compareTo(loc) <= 0 && endLoc().compareTo(loc) > 0;
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

	private int comparePositions(Position a, Position b) {
		return 0;
	}

	public Scope firstChildAfter(TextLocation loc) {
//		stdout.printf("\"%s\".first_child_after(%d, %d)\n", name, loc.line, loc.line_offset);
		
		for (Scope child : children) {
			if (child.startLoc().compareTo(loc) >= 0) {
				return child;
			}
		}
		return null;
	}

	private Position makePosition(int line, int lineOffset) {
		Position pos = new Position(styledText.getOffsetAtLine(line) + lineOffset, 0);
		try {
			mateText.getDocument().addPosition(pos);
			return pos;
		}
		catch(BadLocationException e) {
			System.out.printf("BadLocationException in Scope (%d, %d)\n", line, lineOffset);
			e.printStackTrace();
		}
		return null;
	}
	
	public void setStartPos(int line, int lineOffset, boolean hasLeftGravity) {
		this.startPos = makePosition(line, lineOffset);
	}

	public void setInnerStartPos(int line, int lineOffset, boolean hasLeftGravity) {
		this.innerStartPos = makePosition(line, lineOffset);
	}

	public void setInnerEndPos(int line, int lineOffset, boolean c) {
		this.innerEndPos = makePosition(line, lineOffset);
	}

	public void setEndPos(int line, int lineOffset, boolean c) {
		this.endPos = makePosition(line, lineOffset);
	}

	public TextLocation startLoc() {
		return new TextLocation(startLine(), startLineOffset());
	}

	public TextLocation innerEndLoc() {
		return new TextLocation(endLine(), innerEndLineOffset());
	}
	
	public TextLocation endLoc() {
		return new TextLocation(endLine(), endLineOffset());
	}
	
	public int startLine() {
		if (startPos == null)
			return 0;
		else
			return styledText.getLineAtOffset(startPos.offset);
	}

	public int endLine() {
		if (endPos == null)
			return styledText.getLineCount() - 1;
		else
			return styledText.getLineAtOffset(endPos.offset);
	}

	public int startLineOffset() {
		if (startPos == null)
			return 0;
		else
			return startPos.offset - styledText.getOffsetAtLine(startLine());
	}

	public int innerEndLineOffset() {
		if (innerEndPos == null)
			return styledText.getCharCount();
		else
			return innerEndPos.offset - styledText.getOffsetAtLine(endLine());
	}
	
	public int endLineOffset() {
		if (endPos == null)
			return styledText.getCharCount() - styledText.getOffsetAtLine(endLine());
		else
			return endPos.offset - styledText.getOffsetAtLine(endLine());
	}
	
	public int startOffset() {
		return this.startPos.offset;
	}
	
	public int endOffset() {
		return this.endPos.offset;
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
		// TODO: port these sections once Positions are figured out
//		if (startPos == null) {
//			prettyString.append("inf");
//		}
//		else {
			prettyString.append(String.format(
					"%d,%d",
					startLine(), 
					startLineOffset()));
//		}
		prettyString.append(")-(");
//		if (endPos == null) {
//			prettyString.append("inf");
//		}
//		else {
			prettyString.append(String.format(
					"%d,%d",
					endLine(), 
					endLineOffset()));
//		}
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
