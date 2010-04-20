package com.redcareditor.mate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.logging.Handler;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

import com.redcareditor.onig.Match;
import com.redcareditor.onig.Rx;

public class Scanner implements Iterable<Marker> {
	private Scope currentScope;
	public int position;
	public String line;
	public int lineLength;
	public int lineIx;
	public Logger logger;

	public void setCurrentScope(Scope scope) {
		this.currentScope = scope;
	}
	
	public Scope getCurrentScope() {
		return this.currentScope;
	}
	
	public Scanner(Scope startScope, String line, int lineIx) {
		this.currentScope = startScope;
		this.line = line;
		this.lineIx = lineIx;
		this.lineLength = line.getBytes().length;
		this.position = 0;
		logger = Logger.getLogger("JMV.Scanner");
		logger.setUseParentHandlers(false);
		for (Handler h : logger.getHandlers()) {
			logger.removeHandler(h);
		}
		logger.addHandler(MateText.consoleHandler());
	}


	public Match scanForMatch(int from, Pattern p) {
		// if (p.name != null && (p.name.startsWith("#") || p.name.startsWith("$")))
		//	System.out.printf("*** WARNING trying to scan for pattern called %s\n", p.name);
		
		Match match = null;
		if (p instanceof SinglePattern) {
			SinglePattern sp = (SinglePattern) p;
			if (sp.match.regex != null) {
				match = sp.match.search(line, from, this.lineLength);
			}
		}
		else if (p instanceof DoublePattern) {
			if (((DoublePattern) p).begin.regex != null) {
				match = ((DoublePattern) p).begin.search(this.line, from, this.lineLength);
			}
		}
		return match;
	}
	
	
	public Marker findNextMarker() {
		//logger.info(String.format("scanning: '%s' from %d to %d (current_scope is %s)", this.line.replaceAll("\n", ""), this.position, this.lineLength, currentScope.name));
		Marker bestMarker = null;
		int newLength;
		boolean isCloseMatch = false;
		Rx closingRegex = currentScope.closingRegex;
		if (closingRegex != null && closingRegex.usable()) {
			//logger.info(String.format("closing regex: '%s'", closingRegex.pattern));
			Match match = closingRegex.search(this.line, this.position, this.lineLength);
			if (match != null && 
			       !(match.getCapture(0).start == currentScope.getStart().getLineOffset() && 
			         currentScope.getStart().getLine() == this.lineIx)
			    ) {
				//logger.info(String.format("closing match: %s (%d-%d)", this.currentScope.name, match.getCapture(0).start, match.getCapture(0).end));
				Marker newMarker = new Marker();
				newMarker.pattern = this.currentScope.pattern;
				newMarker.match = match;
				newMarker.from = match.getCapture(0).start;
				newMarker.isCloseScope = true;
				bestMarker = newMarker;
				isCloseMatch = true;
			} else {
				// logger.info(String.format("no close match"));
			}
		}
		//logger.info(String.format("  scanning for %d patterns", ((DoublePattern) currentScope.pattern).patterns.size()));
		if (currentScope.pattern instanceof SinglePattern)
			return null;
		DoublePattern dp = (DoublePattern) (currentScope.pattern);
		dp.replaceGrammarIncludes();
		for (Pattern p : dp.patterns) {
			// System.out.printf("     scanning for %s (%s)\n", p.name, p.disabled);
			if (p.disabled)
				continue;
			int positionNow = position;
			int positionPrev = position-1;
			Match match;
			while ((match = scanForMatch(positionNow, p)) != null &&
				   positionNow != positionPrev // some regex's have zero width (meta.selector.css)
				) {
				positionPrev = positionNow;
				// logger.info(String.format("  matched: %s (%d-%d)", p.prettyName(), match.getCapture(0).start, match.getCapture(0).end));
				Marker newMarker = new Marker();
				newMarker.pattern = p;
				newMarker.match = match;
				try {
					newMarker.from = match.getCapture(0).start;
				}
				catch (ArrayIndexOutOfBoundsException e) {
					System.out.printf("*** Warning ArrayIndexOutOfBoundsException pattern: %s, line:'%s'\n", p.name, line);
					e.printStackTrace();
					continue;
				}
				newMarker.isCloseScope = false;
				bestMarker = newMarker.bestOf(bestMarker);
				positionNow = match.getByteCapture(0).end;
			}
		}
		return bestMarker;
	}
	
	public Iterator<Marker> iterator() {
		return new ScannerIterator(this);
	}
	
	// TODO: implement this class for real
	public class ScannerIterator implements Iterator<Marker> {
		private Scanner scanner;
		private Marker nextMarker;
		
		public ScannerIterator(Scanner scanner) {
			this.scanner = scanner;
		}
		
		public boolean hasNext() {
			nextMarker = scanner.findNextMarker();
			return (nextMarker != null);
		}
		
		public Marker next() {
			return nextMarker;
		}
		
		public void remove() {
			nextMarker = null;
		}
	}
}
