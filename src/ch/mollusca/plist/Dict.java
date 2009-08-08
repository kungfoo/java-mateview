package ch.mollusca.plist;

import java.io.File;
import java.io.IOException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;


public class Dict extends PlistNode {
	private SAXBuilder builder;
	private Document document;
	private Element plist;
	
	public Dict(String filename){
		builder = new SAXBuilder();
		try {
			document = builder.build(new File(filename));
			parseDocument();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("The file " + filename + " was not found!");
			e.printStackTrace();
		}
	}

	private void parseDocument() {
		plist = document.getRootElement();
	}
}
