package com.redcareditor.mate;

import java.io.FileNotFoundException;

import com.redcareditor.util.FileUtility;

import ch.mollusca.benchmarking.Before;
import ch.mollusca.benchmarking.Benchmark;

public class GrammarBenchmark {

	private String autocompleter;

	@Before
	public void setup(){
		try {
			autocompleter = new String(FileUtility.readFully("input/autocompleter.rb"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Benchmark
	public void benchmarkAllPatternsOnSingleLine(){
		
	}
	
	@Benchmark
	public void benchmarkAllPatternsOnFile(){
		
	}
}
