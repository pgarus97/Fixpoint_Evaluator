package model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class FixpointTest {

	@Test
	void testSetMapFromString() {
		Fixpoint input = new Fixpoint();
		input.setContentString("iff((c=0)&(x=0),0.0;(c=0)&(x=1),1.0;(c=1)&(x=0),0.5;(c=1)&(x=1),1.0)");
		input.setMapFromString();
		assertEquals("1.0",input.getContentMap().get("(c=0)&(x=1)"));
	}

	@Test
	void testSetStringFromMap() {
		Fixpoint input = new Fixpoint();
		input.addContentFromMap("(c=0)&(x=1)","1.0");
		input.setStringFromMap();
		assertEquals("iff((c=0)&(x=1),1.0)",input.getContentString());	
	}

	@Test
	void testAddContentFromMap() {
		Fixpoint input = new Fixpoint();
		input.addContentFromMap("(c=0)&(x=1)","1.0");
		assertEquals("1.0",input.getContentMap().get("(c=0)&(x=1)"));
	}

}
