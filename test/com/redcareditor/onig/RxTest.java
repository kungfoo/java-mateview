package com.redcareditor.onig;

import java.io.FileNotFoundException;

import org.junit.Test;

import com.redcareditor.util.FileUtility;

public class RxTest {
	@Test
	public void testSingleFoo() {
		String pattern = "((class|module|def|end) \\w+ < )+";
		String target = "";
		try {
			target = new String(FileUtility.readFully("input/autocompleter.rb"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Rx regex = new Rx(pattern);
		Match m = regex.search(target, 0, target.length());
		
		for(Range r : m){
			System.out.println(r);
		}
	}
}
