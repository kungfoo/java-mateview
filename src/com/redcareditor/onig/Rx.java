package com.redcareditor.onig;

import java.io.UnsupportedEncodingException;

import org.jcodings.specific.ASCIIEncoding;
import org.joni.Option;
import org.joni.Regex;
import org.joni.Syntax;

/**
 * wrapper class around the Joni Regex library which is a optimized port of
 * Onigurama
 * 
 * @author kungfoo
 * 
 */
public class Rx {
	String pattern;
	Regex regex;

	public Rx(String pattern) {
		// TODO: really make this a regex then...
		this.pattern = pattern;
		regex = compileRegex(pattern);
	}

	public Regex compileRegex(String pattern) {
		byte[] bytes;
		try {
			bytes = pattern.getBytes("iso-8859-2");
			return new Regex(bytes, 0, bytes.length, Option.MULTILINE,
					ASCIIEncoding.INSTANCE, Syntax.DEFAULT);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
