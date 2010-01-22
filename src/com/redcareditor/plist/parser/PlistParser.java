package com.redcareditor.plist.parser;

import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.redcareditor.plist.Dict;

/**
 * encapsulates streaming parsing of Apple Plist Property files into the
 * {@link Dict} data structure.
 * 
 * @author kungfoo
 */
public class PlistParser {

	private static class PlistHandler extends DefaultHandler {
		private StringBuilder buffer = new StringBuilder();
		private Stack<PlistNode<?>> stack = new Stack<PlistNode<?>>();

		public PlistHandler(Dict root) {
			stack.push(root);
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if (qName.equals("array")) {
				stack.push(new ArrayNode());
			} else if (qName.equals("dict") && stack.peek() instanceof ArrayNode) {
				/* currently parsing an array with subdicts */
				Dict dict = new Dict();
				ArrayNode node = (ArrayNode) stack.peek();
				node.add(dict);
				stack.push(dict);
			}
		}

		@Override
		public void endElement(String uri, String localName, String name) throws SAXException {
			if (name.equals("key")) {
				String key = buffer.toString();
				PlistNode<?> node = new PlistNode<Object>();
				((Dict) stack.peek()).addNode(key, node);
				stack.push(node);
			} else if (name.equals("dict")) {
				/* end of the dict we're currently adding key-value pairs */
				stack.pop();
			} else {
				// TODO: handle all types...
				if (name.equals("string")) {
					PlistNode<String> node = (PlistNode<String>) stack.peek();
					node.value = buffer.toString();
				} else if (name.equals("array")) {
					ArrayNode node = (ArrayNode) stack.pop();
				}

				if (!(stack.peek() instanceof ArrayNode)) {
					stack.pop();
				}
			}
			buffer.setLength(0);
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			buffer.append(ch, start, length);
		}
	}

	public static Dict parse(FileInputStream stream) {
		EntityResolver resolver = PlistParser.createEntityResolver();
		Dict root = new Dict();
		PlistParser.PlistHandler handler = new PlistParser.PlistHandler(root);
		try {
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			parserFactory.setValidating(false);
			SAXParser parser = parserFactory.newSAXParser();
			XMLReader reader = parser.getXMLReader();
			reader.setEntityResolver(resolver);
			reader.setContentHandler(handler);
			reader.parse(new InputSource(stream));
			return root;

		} catch (Exception e) {
			throw new PlistParsingException("There has been a plist parser error", e);
		}
	}

	private static EntityResolver createEntityResolver() {
		return new EntityResolver() {
			public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
				return new InputSource(getClass().getClassLoader().getResourceAsStream("PropertyList-1.0.dtd"));
			}
		};
	}

}
