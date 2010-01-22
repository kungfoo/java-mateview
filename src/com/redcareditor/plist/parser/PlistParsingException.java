package com.redcareditor.plist.parser;

public class PlistParsingException extends RuntimeException {
	private static final long serialVersionUID = -5123993301131646747L;

	public PlistParsingException(String message) {
		super(message);
	}

	public PlistParsingException(String message, Throwable cause) {
		super(message, cause);
	}
}
