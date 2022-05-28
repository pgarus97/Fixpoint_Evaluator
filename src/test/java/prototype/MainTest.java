package prototype;


import java.util.HashMap;

import org.junit.jupiter.api.Test;

class MainTest {
	
    HashMap<String, String> variables = new HashMap<String,String>();
	WPCalculator mainCalculator = new WPCalculator();

	@Test
	void testAssignments() {
		variables.put("x", "0");
		variables.put("y", "0");
		mainCalculator.setVariables(variables);
		
		
		//assignments
		assert mainCalculator.calculation(mainCalculator.wp("x=5", "x^2")).equals("25.0");
		assert mainCalculator.calculation(mainCalculator.wp("x=5 ; x=10", "x^2")).equals("100.0");	
		assert mainCalculator.calculation(mainCalculator.wp("x=5 ; x=10 ; y=2", "x^2")).equals("100.0");	
		assert mainCalculator.calculation(mainCalculator.wp("x=5 ; y=10", "x^2")).equals("25.0");
		assert mainCalculator.calculation(mainCalculator.wp("x=5 ; y=10", "y^2")).equals("100.0");
		assert mainCalculator.calculation(mainCalculator.wp("x=5 ; y=10", "x+y")).equals("15.0");



	}
	
	@Test
	void testProbability() {
		variables.put("x", "0");
		variables.put("y", "0");
		mainCalculator.setVariables(variables);
		
		//probability with initial assignment x=5
		assert mainCalculator.calculation(mainCalculator.wp("{x=5}[4/5]{x=10}","x^2")).equals("40.0");
		//TODO why does this work? => f is already 36 on entry of first variables
		assert mainCalculator.calculation(mainCalculator.wp("{x=5}[1/2]{x=10};{x=3}[1/2]{x=4}","x")).equals("3.5");
		assert mainCalculator.calculation(mainCalculator.wp("x=0;y=0;{skip}[1/2]{x=x+2}","x")).equals("1.0");
		assert mainCalculator.calculation(mainCalculator.wp("x=5;{skip}[1/2]{x=x+2}","x")).equals("6.0");
		assert mainCalculator.calculation(mainCalculator.wp("{x=5}[1/2]{x=10};{x=3}[1/2]{x=4};x=6","x")).equals("6.0");
		

		
	}
	
	@Test
	void testConditional() {
		variables.put("x", "0");
		variables.put("y", "0");
		mainCalculator.setVariables(variables);
		//TODO here is the problem with order of variables
		//TODO check examples here in the presentation etc. => where does sigma get updated?
		//if worked because we pushed x as a variable forward and then later calculated it
		assert mainCalculator.calculation(mainCalculator.wp("x=5;if (x<5) {x=x+1} else {x=x-1}", "x^2")).equals("16.0");
		assert mainCalculator.calculation(mainCalculator.wp("x=5;{x=3}[1/2]{x=10};if (x<5) {x=x+1} else {x=x-1}", "x")).equals("6.5");

	}
	
	@Test
	void testWhile() {	
		variables.put("x", "1");
		variables.put("c", "0");
		mainCalculator.setVariables(variables);
		
		assert mainCalculator.calculation(mainCalculator.wp("c=0;x=1; while(c=1){{x=x+1}[1/2]{c=0}}", "x")).equals("1.0");
		assert mainCalculator.calculation(mainCalculator.wp("c=1;x=1; while(c=1){{x=x+1}[1/2]{c=0}}", "x")).equals("1.978515625");
	}
}
