package com.redcareditor.onig;

import org.junit.Test;

public class RxTest {
	@Test
	public void testSingleFoo() {
		String pattern = "(foo) ";
		String target = "___foo   foo fooo fooo foo            afsdfsd ";
		Rx regex = new Rx(pattern);
		Match m = regex.search(target, 0, target.length());
		
		for(Range r : m){
			System.out.println(r);
		}
	}
}
