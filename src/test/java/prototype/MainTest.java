package prototype;


import java.util.HashMap;

import org.apache.commons.lang3.math.NumberUtils;
import org.junit.jupiter.api.Test;

class MainTest {
	
    HashMap<String, String> variables = new HashMap<String,String>();
	WPCalculator mainCalculator = new WPCalculator();

	
	@Test
	void calcTest() {
		System.out.println(NumberUtils.isCreatable("-1"));
		System.out.println(mainCalculator.calculation("(5-6)"));
		System.out.println(mainCalculator.calculation("min(2,4)"));
		System.out.println(mainCalculator.calculation("if(1=1,x+1,x)"));

	}

	
	@Test
	void truncateTest() {
		mainCalculator.setRestriction(10);
		
		assert mainCalculator.truncate("#{1}").equals("1.0");
		assert mainCalculator.truncate("#{-1}").equals("0");
		assert mainCalculator.truncate("#{11}").equals("10.0");
		
		assert mainCalculator.truncate("#{x+1}").equals("#{x+1}");
		assert mainCalculator.truncate("#{1+1}").equals("2.0");
		assert mainCalculator.truncate("#{x+#{1+3}}").equals("#{x+4.0}");
		assert mainCalculator.truncate("#{2+#{1+3}}").equals("6.0");
		assert mainCalculator.truncate("#{x+#{y+3}}").equals("#{x+#{y+3}}");


	}
	
	@Test
	void testAssignments() {
		variables.put("x", "0");
		variables.put("y", "0");
		mainCalculator.setVariables(variables);
		mainCalculator.setRestriction(10);

		
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
		mainCalculator.setRestriction(10);

		
		//probability with initial assignment x=5
		assert mainCalculator.calculation(mainCalculator.wp("{x=5}[4/5]{x=10}","x^2")).equals("40.0");
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
		mainCalculator.setRestriction(15);

		
		assert mainCalculator.calculation(mainCalculator.wp("x=5;if {x<5} {x=x+1} else {x=x-1}", "x^2")).equals("16.0");
		assert mainCalculator.calculation(mainCalculator.wp("x=5;if {x<5} {x=x+1} else {x=x-1};x=8", "x^2")).equals("64.0");
		assert mainCalculator.calculation(mainCalculator.wp("x=5;{x=3}[1/2]{x=10};if {x<5} {x=x+1} else {x=x-1}", "x")).equals("6.5");
		assert mainCalculator.calculation(mainCalculator.wp("x=5;if {x<5} {x=x+1} else {if{x=5}{x=3}else{x=8}}", "x")).equals("3.0");
		assert mainCalculator.calculation(mainCalculator.wp("x=5;if {x<5} {x=x+1} else {min{x=x+1}{x=3}", "x")).equals("3.0");



	}
	
	@Test
	void testDemonicChoice() {
		variables.put("x", "0");
		variables.put("y", "0");
		mainCalculator.setVariables(variables);
		mainCalculator.setRestriction(10);

		
		assert mainCalculator.calculation(mainCalculator.wp("x=1;min{x=x+1}{x=3}","x")).equals("2.0");
		assert mainCalculator.calculation(mainCalculator.wp("x=3;min{x=x+1}{x=3}","x")).equals("3.0");
	}
	
	@Test
	void testWhile() {	
		variables.put("x", "1");
		variables.put("c", "0");
		mainCalculator.setVariables(variables);
		mainCalculator.setRestriction(100);
		mainCalculator.setIterationCount(10);

		
		assert mainCalculator.calculation(mainCalculator.wp("c=0;x=1; while(c=1){{x=x+1}[1/2]{c=0}}", "x")).equals("1.0");
		assert mainCalculator.calculation(mainCalculator.wp("c=1;x=1; while(c=1){{x=x+1}[1/2]{c=0}}", "x")).equals("1.978515625");
	}
}
