package cz.deznekcz.util.xml;

import java.util.HashMap;

import org.junit.Test;

import cz.deznekcz.util.xml.XMLPairTag;

public class XMLTest {
	@Test
	public void testMake() throws Exception {
		String id = "value";
		HashMap<String, String> searched = new HashMap<>();
		searched.put("a", "a");
		searched.put("b", "a");
		searched.put("c", "a");
		searched.put("d", "a");
		
		XMLPairTag<XMLroot> values = XML.init("storage")
			.root()
				.newPairTag("id", false)
					.setTextCDATA(id)
				.close()
				.newPairTag("values");
		
		for (String value : searched.keySet()) {
			values = values.newPairTag("value", false)
						.attribute("id", value)
						.setTextCDATA(searched.get(value))
					.close();
		}
		
		XML xml = values.close().close();
		System.out.println(xml.write());
	}
}
