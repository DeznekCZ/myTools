package cz.deznekcz.util.xml;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Before;
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
		
		XMLPairTag<XMLRoot> values = XML.init("storage")
			.root()
				.newPairTag("id", false)
					.setTextCDATA(id)
				.close()
				.newPairTag("values");
		
		for (String value : searched.keySet()) {
			values = values.newPairTag("value", false)
						.addAttribute("id", value)
						.setTextCDATA(searched.get(value))
					.close();
		}
		
		XML xml = values.close().close();
		xml.root();
		
		System.out.println(xml.write());
	}
	
	@Test
	public void testGetter() throws Exception {
		XML xml = XML.init("root");
		xml.root()
			.newPairTag("textA", false)
				.setText("value")
				.setComment("random comment")
			.close()
			.newPairTag("textB", false)
				.setTextCDATA("value");
		
		System.out.println(xml.write());
		
		assertEquals("value of textA via getText()", "value", 
				xml.root().getPairTag("textA").get(0).getText());
		assertEquals("value of textB via getText()", "<![CDATA[value]]>", 
				xml.root().getPairTag("textB").get(0).getText());
		assertEquals("value of textB via getTextCDATA()", "value", 
				xml.root().getPairTag("textB").get(0).getTextCDATA());
		assertEquals("comment of textB via getComment()", "random comment", 
				xml.root().getPairTag("textA").get(0).getComment());
	}
}
