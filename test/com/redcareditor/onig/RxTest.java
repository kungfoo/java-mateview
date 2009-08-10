package com.redcareditor.onig;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

import org.junit.Test;

import com.redcareditor.util.FileUtility;

public class RxTest {
	@Test
	public void testSingleFoo() {
		String pattern = "^\\s*(class)\\s+(([.a-zA-Z0-9_:]+(\\s*(&lt;)\\s*[.a-zA-Z0-9_:]+)?)|((&lt;&lt;)\\s*[.a-zA-Z0-9_:]+))+";
		String fileContents = "";
		try {
			fileContents = new String(FileUtility.readFully("input/autocompleter.rb"));

			BufferedReader reader = new BufferedReader(new StringReader(fileContents));

			Rx regex = new Rx(pattern);

			String line;
			while ((line = reader.readLine()) != null) {
				Match m = regex.search(line, 0, line.length());
				
				if (m != null) {
					System.out.println(line);
					for (Range r : m) {
						System.out.println(r);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
