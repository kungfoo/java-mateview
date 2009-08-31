
package com.redcareditor.mate;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Position;

import com.redcareditor.onig.Match;
import com.redcareditor.onig.Rx;

public class Scope {
	public MateText mateText;
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
	public List<Scope> children;
	
	StringBuilder prettyString;
	int indent;
	
	public Scope(MateText mt, String n) {
		mateText = mt;
		name = n;
		children = new ArrayList<Scope>();
	}
	
	public void clearAfter(int lineIx, int something) {
		// TODO: port this method
	}
	
	public Scope scopeAt(int lineIx, int something) {
		// TODO: port this method
		return this;
	}
	
	public Scope containingDoubleScope(int lineIx) {
		// TODO: port this method
		return this;
	}
	
	public boolean surfaceIdenticalTo(Scope other) {
		// TODO: port me
		return false;
	}

	public boolean overlapsWith(Scope other) {
		// TODO: port me
		return false;
	}

	public void addChild(Scope child) {
		// TODO: should insert in the correct position
		children.add(child);
	}
	
	public void removeChild(Scope child) {
		children.remove(child);
	}

	private int comparePositions(Position a, Position b) {
		return 0;
	}
	
	public Scope firstChildAfter(TextLocation textLoc) {
		// TODO: implement me
		if (children.size() > 0) 
			return children.get(children.size()-1);
		else
			return null;
	}
	
	public TextLocation startLoc() {
		return new TextLocation(0, 0);
	}
	
	private Position makePosition(int line, int lineOffset) {
		Position pos = new Position(mateText.getTextWidget().getOffsetAtLine(line) + lineOffset, 0);
		try {
			mateText.getDocument().addPosition(pos);
		}
		catch(BadLocationException e) {
			System.out.printf("BadLocationException in Scope (%d, %d)\n", line, lineOffset);
			e.printStackTrace();
		}
		return pos;
	}
	
	public void setStartPos(int line, int lineOffset, boolean hasLeftGravity) {
		this.startPos = makePosition(line, lineOffset);
	}

	public void setEndPos(int line, int lineOffset, boolean c) {
		this.endPos = makePosition(line, lineOffset);
	}
	
	public int startLine() {
		return mateText.getTextWidget().getLineAtOffset(startPos.offset);
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
		// TODO: port these sections once Positions are figured out
		if (startPos == null) {
			prettyString.append("inf");
		}
		else {
			int lineIx = mateText.getTextWidget().getLineAtOffset(startPos.offset);
			prettyString.append(String.format(
					"%d,%d",
					lineIx, 
					startPos.offset - mateText.getTextWidget().getOffsetAtLine(lineIx)));
		}
		prettyString.append(")-(");
		if (endPos == null) {
			prettyString.append("inf");
		}
		else {
			int lineIx = mateText.getTextWidget().getLineAtOffset(endPos.offset);
			prettyString.append(String.format(
					"%d,%d",
					lineIx, 
					endPos.offset - mateText.getTextWidget().getOffsetAtLine(lineIx)));
		}
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
