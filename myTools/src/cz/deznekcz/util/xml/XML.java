package cz.deznekcz.util.xml;

import cz.deznekcz.reference.OutString;

public class XML {

	public static String singleTag(String name, String...attributes) {
		OutString out = OutString.init("<%s");
		out.format(name);
		for (String string : attributes) {
			out.append(" ").append(string);
		}
		return out.append("/>").get();
	}

	public static String startTag(String name, String...attributes) {
		OutString out = OutString.init("<%s");
		out.format(name);
		for (String string : attributes) {
			out.append(" ").append(string);
		}
		return out.append(">").get();
	}

	public static String endTag(String name) {
		String ret = String.format("</%s>", name);
		return ret;
	}

	public static String textTag(String name, String value) {
		return String.format("<%s>%s</%s>", name, value, name);
	}

	public static String attribute(String name, String value) {
		return String.format("%s=\"%s\"", name, value);
	}

	public static String CDATA(String text) {
		return String.format("<![CDATA[%s]]>", text);
	}

	public static String headUTF8() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
	}

}
