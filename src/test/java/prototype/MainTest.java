package prototype;


import java.util.HashMap;

import org.junit.jupiter.api.Test;

class MainTest {
	
    HashMap<String, String> variables = new HashMap<String,String>();


	@Test
	void testAssignments() {
		
		variables.put("x", "5");
		
		//assignments
		assert Main.calculation(Main.wp("x=5", "x^2")).equals("25.0");
		assert Main.calculation(Main.wp("x=5 ; x=10", "x^2")).equals("100.0");	
		assert Main.calculation(Main.wp("x=5 ; x=10 ; y=2", "x^2")).equals("100.0");	
		assert Main.calculation(Main.wp("x=5 ; y=10", "x^2")).equals("25.0");

	}
	
	@Test
	void testProbability() {
		
		//probability with initial assignment x=5
		assert Main.calculation(Main.wp("{x=5}[4/5]{x=10}","x^2")).equals("40.0");
		assert Main.calculation(Main.wp("{x=5}[4/5]{x=10};x=6","x^2")).equals("36.0");
		assert Main.calculation(Main.wp("{skip}[1/2]{x=x+2}","x")).equals("1/2 * x + 0.5 * (x+2)");
		assert Main.calculation(Main.wp("x=5;{skip}[1/2]{x=x+2}","x")).equals("6.0");

	}
	
	@Test
	void testConditional() {	
		assert Main.calculation(Main.wp("x=5;if (x<5) {x=x+1} else {x=x-1}", "x^2")).equals("16.0");
	}
	
	@Test
	void testWhile() {	
		//assert Main.calculation(Main.wp("c=0;x=0; while(c=1){x=x+1}[1/2]{c=0}", "x")).equals("0.0");
		//assert Main.calculation(Main.wp("c=0;x=0; if (x<5) {x=x+1} else {x=x-1}", "x^2")).equals("16.0");
	}
}
