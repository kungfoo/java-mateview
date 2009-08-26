package com.redcareditor.mate;

public class Parser {
	public Grammar grammar;
	public Colourer colourer;
	public MateText mateText;
	
	public int parsed_upto;
	
	public Parser(Grammar g, MateText m) {
		grammar = g;
		mateText = m;
	}
}
