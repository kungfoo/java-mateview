package ch.mollusca.plist;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

public class PlistNode {

	public PlistNode() {
	}

	public static PlistNode parseElement(Element element) {
		if (elementNameIs(element, "string")) {
			return new PlistNode().new StringNode(element.getValue());
		}
		if (elementNameIs(element, "integer")) {
			return new PlistNode().new IntegerNode(Integer.parseInt(element
					.getValue()));
		}
		if (elementNameIs(element, "array")) {
			return new PlistNode().new ArrayNode(element);
		}
		if (elementNameIs(element, "dict")) {
			return new Dict(element);
		}

		return null;
	}

	public class IntegerNode extends PlistNode {
		public int value;

		public IntegerNode(int value) {
			this.value = value;
		}
	}

	public class StringNode extends PlistNode {
		public String value;

		public StringNode(String value) {
			this.value = value;
		}
	}

	public class ArrayNode extends PlistNode {
		public List<PlistNode> array = new ArrayList<PlistNode>();

		@SuppressWarnings("unchecked")
		public ArrayNode(Element element) {
			List<Element> children = element.getChildren();
			for (Element e : children) {
				array.add(PlistNode.parseElement(e));
			}
		}
	}

	private static boolean elementNameIs(Element element, String name) {
		return element.getName().equals(name);
	}
}
