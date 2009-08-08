package ch.mollusca.plist;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class Dict extends PlistNode {
	private Map<String, PlistNode<?>> map = new HashMap<String, PlistNode<?>>();

	public static Dict parseFile(String filename) {
		SAXBuilder builder;
		Document document;
		builder = new SAXBuilder();
		try {
			document = builder.build(new File(filename));
			return new Dict(document.getRootElement().getChild("dict"));

		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("The file " + filename + " was not found!");
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected Dict(Element element) {
		List<Element> children = element.getChildren();
		String key = null;
		for (Element c : children) {
			if (c.getName().equals("key")) {
				key = c.getValue();
			} else {
				map.put(key, PlistNode.parseElement(c));
			}
		}
	}

	public PlistNode get(String key) {
		return map.get(key);
	}

	public Set<String> keys() {
		return map.keySet();
	}
}
