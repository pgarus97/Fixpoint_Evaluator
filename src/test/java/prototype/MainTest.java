package prototype;


import org.junit.jupiter.api.Test;

class MainTest {

	@Test
	void testAssignments() {
		
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
		assert Main.calculation(Main.wp("x=5;{x=5}[4/5]{x=10}","x^2")).equals("40.0");
		assert Main.calculation(Main.wp("{skip}[1/2]{x=x+2}","x")).equals("1/2 * x + 0.5 * (x+2)");
		assert Main.calculation(Main.wp("x=5;{skip}[1/2]{x=x+2}","x")).equals("6.0");

	}
	
	@Test
	void testMixed() {
		
		assert Main.calculation(Main.wp("x=5 ; if (x<5) {x=x+1} else {x=x-1}", "x^2")).equals("16.0");
		// assert Main.calculation(Main.wp("if (x<5) {x=x+1} else {x=x-1}", "x^2")).equals("?"); What would this compute to?
	}
}
