package mainClass;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import controller.MainController;
import model.Fixpoint;
import model.WPCalculator;
import view.WPCalculatorView;

class MainTest {
	
	//TODO make systematic border tests etc.
	//TODO make one big view test
	
	WPCalculator mainCalculator = new WPCalculator();
	WPCalculatorView mainView = new WPCalculatorView();
	MainController mainController= new MainController();
	
	MainTest(){
		mainController.link(mainView, mainCalculator);
		mainCalculator.setRestriction(10); //default test case
		mainCalculator.setAllSigmaSelection(true); //default case
		mainCalculator.setIterationDelta(0.001); //default case
		mainCalculator.setIterationCount(Double.POSITIVE_INFINITY); //default case
	}
	
	@Test
	void testCalculation() {
		assertEquals("-1.0", mainCalculator.calculation("(5-6)"));
		assertEquals("2.0", mainCalculator.calculation("min(2,4)"));
		assertEquals("if(1=1,x+1,x)", mainCalculator.calculation("if(1=1,x+1,x)"));
		assertEquals("20.0", mainCalculator.calculation("if(r(11)=10,20,30)"));
		assertEquals("20.0", mainCalculator.calculation("if(r(-1)=0,20,30)"));
		assertEquals("30.0", mainCalculator.calculation("if(r(4)=10,20,30)"));
		
		assertEquals("x=3;x=4",mainCalculator.getInsideIf("x=3;x=4,x=2)"));
		assertEquals("x=3;iff(x=3,3;x=2,4)",mainCalculator.getInsideIf("x=3;iff(x=3,3;x=2,4),x=2)"));

	}

	
	@Test
	void testTruncate() {
		
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
		
		assertEquals("25.0",mainCalculator.calculation(mainCalculator.wp("x=5", "x^2")));
		assertEquals("100.0",mainCalculator.calculation(mainCalculator.wp("x=5;x=10", "x^2")));	
		assertEquals("100.0",mainCalculator.calculation(mainCalculator.wp("x=5;x=10;y=2", "x^2")));	
		assertEquals("25.0",mainCalculator.calculation(mainCalculator.wp("x=5;y=10", "x^2")));
		assertEquals("100.0", mainCalculator.calculation(mainCalculator.wp("x=5;y=10", "y^2")));
		assertEquals("15.0", mainCalculator.calculation(mainCalculator.wp("x=5;y=10", "x+y")));
	}
	
	@Test
	void testProbability() {
		
		assert mainCalculator.calculation(mainCalculator.wp("{x=5}[4/5]{x=10}","x^2")).equals("40.0");
		assert mainCalculator.calculation(mainCalculator.wp("{x=5}[1/2]{x=10};{x=3}[1/2]{x=4}","x")).equals("3.5");
		assert mainCalculator.calculation(mainCalculator.wp("x=0;y=0;{skip}[1/2]{x=x+2}","x")).equals("1.0");
		assert mainCalculator.calculation(mainCalculator.wp("x=5;{skip}[1/2]{x=x+2}","x")).equals("6.0");
		assert mainCalculator.calculation(mainCalculator.wp("{x=5}[1/2]{x=10};{x=3}[1/2]{x=4};x=6","x")).equals("6.0");
	}
	
	@Test
	void testConditional() {

		assert mainCalculator.calculation(mainCalculator.wp("x=5;if{x<5}{x=x+1}else{x=x-1}", "x^2")).equals("16.0");
		assert mainCalculator.calculation(mainCalculator.wp("x=5;if{x<5}{x=x+1}else{x=x-1};x=8", "x^2")).equals("64.0");
		assert mainCalculator.calculation(mainCalculator.wp("x=5;{x=3}[1/2]{x=10};if{x<5}{x=x+1}else{x=x-1}", "x")).equals("6.5");
		assert mainCalculator.calculation(mainCalculator.wp("x=5;if{x<5}{x=x+1}else{if{x=5}{x=3}else{x=8}}", "x")).equals("3.0");
		assert mainCalculator.calculation(mainCalculator.wp("x=5;if{x<5}{x=x+1}else{min{x=x+1}{x=3}", "x")).equals("3.0");
	}
	
	@Test
	void testDemonicChoice() {

		assert mainCalculator.calculation(mainCalculator.wp("x=1;min{x=x+1}{x=3}","x")).equals("2.0");
		assert mainCalculator.calculation(mainCalculator.wp("x=3;min{x=x+1}{x=3}","x")).equals("3.0");
	}
	
	@Test
	void testWhile() {	
		mainCalculator.setIterationCount(10);
		mainCalculator.setAllSigmaSelection(false);


		assertEquals("1.0", mainCalculator.calculation(mainCalculator.wp("c=0;x=1;while(c=1){{x=x+1}[1/2]{c=0}}", "x")));
		assertEquals("1.978515625", mainCalculator.calculation(mainCalculator.wp("c=1;x=1;while(c=1){{x=x+1}[1/2]{c=0}}", "x"))); 
		assertEquals("4.0",mainCalculator.calculation(mainCalculator.wp("x=1;c=0;while(c=1){{x=x+1}[1/2]{c=0}};while(c=0){x=4;c=1}","x")));
		
	}
	
	@Test
	void testAllSigmaWhile() {	
		
		mainCalculator.setRestriction(2);
		mainCalculator.fillAllSigma("xc");

		assertEquals("1.0", mainCalculator.calculation(mainCalculator.wp("c=0;x=1;while(c=1){{x=x+1}[1/2]{c=0}}", "x")));
		assertEquals("1.5", mainCalculator.calculation(mainCalculator.wp("c=1;x=1;while(c=1){{x=x+1}[1/2]{c=0}}", "x"))); 
	}
	
	@Test
	void testfillAllSigma() {
		mainCalculator.setRestriction(1); //var from {0,1}
		
		ArrayList<LinkedHashMap<String,String>> allSigma = mainCalculator.fillAllSigma("xy");
		
		assert allSigma.get(0).get("x").equals("0");
		assertEquals("0",allSigma.get(0).get("x")); //TODO change all tests to assertEquals notation
		assert allSigma.get(0).get("y").equals("0");
		
		assert allSigma.get(1).get("x").equals("0");
		assert allSigma.get(1).get("y").equals("1");
		
		assert allSigma.get(2).get("x").equals("1");
		assert allSigma.get(2).get("y").equals("0");
		
		assert allSigma.get(3).get("x").equals("1");
		assert allSigma.get(3).get("y").equals("1");

	}
	
	@Test
	void testCalculateConcreteSigma() {
		
		mainCalculator.setRestriction(1); //var from {0,1}
		ArrayList<LinkedHashMap<String,String>> allSigma = mainCalculator.fillAllSigma("xy");

		assertEquals(1.0,mainCalculator.calculateConcreteSigma("if(x=0,1,y)", allSigma.get(0)));
		assertEquals(1.0,mainCalculator.calculateConcreteSigma("if(x=0,1,y)", allSigma.get(1)));
		assertEquals(0.0,mainCalculator.calculateConcreteSigma("if(x=0,1,y)", allSigma.get(2)));
		assertEquals(1.0,mainCalculator.calculateConcreteSigma("if(x=0,1,y)", allSigma.get(3)));
		assertEquals(null,mainCalculator.calculateConcreteSigma("if(x=0,1,z)", allSigma.get(3)));
	}
	
	@Test
	void testFixpointIfConversion() {
		Fixpoint fixpointMap = new Fixpoint();
		fixpointMap.addContentFromMap("(x=0)&(c=0)", "0.0");
		fixpointMap.addContentFromMap("(x=0)&(c=1)", "1.0");
		fixpointMap.addContentFromMap("(x=1)&(c=0)", "1.0");
		fixpointMap.addContentFromMap("(x=1)&(c=1)", "2.0");

		assertEquals("iff((x=0)&(c=0),0.0;(x=0)&(c=1),1.0;(x=1)&(c=0),1.0;(x=1)&(c=1),2.0)", fixpointMap.setStringFromMap());
	}
	
	@Test
	void testFixpointToMap() {
		Fixpoint fixpointMap = new Fixpoint("iff((x=0)&(c=0),0.0;(x=0)&(c=1),1.0;(x=1)&(c=0),1.0;(x=1)&(c=1),2.0)");
		assertEquals("0.0",fixpointMap.getContentMap().get("(x=0)&(c=0)"));
		assertEquals("1.0",fixpointMap.getContentMap().get("(x=0)&(c=1)"));
		assertEquals("1.0",fixpointMap.getContentMap().get("(x=1)&(c=0)"));
		assertEquals("2.0",fixpointMap.getContentMap().get("(x=1)&(c=1)"));
	}
	
	@Test
	void testEvaluateFixpoint() {
		mainCalculator.setRestriction(1);
		//LFP
		assertEquals("[]",mainCalculator.evaluateFixpoint("while(c=1){{x=x+1}[1/2]{c=0}} (x)", "iff((x=0)&(c=0),0.0;(x=0)&(c=1),0.5;(x=1)&(c=0),1.0;(x=1)&(c=1),1.0)", "0.1", 1, new LinkedHashSet<String>()).toString());
		//witness
		assertEquals("[(x=0)&(c=1), (x=1)&(c=1)]",mainCalculator.evaluateFixpoint("while(c=1){x=x+1} (x)", "iff((x=0)&(c=0),0.0;(x=0)&(c=1),1.0;(x=1)&(c=0),1.0;(x=1)&(c=1),1.0)", "0.1", 1, new LinkedHashSet<String>()).toString());
		//no fixpoint
		assertEquals("[(x=0)&(c=0), (x=0)&(c=1), (x=1)&(c=0), (x=1)&(c=1)]",mainCalculator.evaluateFixpoint("while(c=1){x=x+1} (x)", "iff((x=0)&(c=0),1.0;(x=0)&(c=1),0.5;(x=1)&(c=0),1.0;(x=1)&(c=1),1.0)", "0.1", 1, new LinkedHashSet<String>()).toString());
	}
	
	@Test
	void testCacheMethods() {
		mainCalculator.setRestriction(1);
		mainCalculator.fillAllSigma("xc");
		mainController.clearFixpointCache();
		
		assertEquals(null,mainController.getLFP("while(c=1){{x=x+1}[1/2]{c=0}} (x)"));
		mainController.wp("while(c=1){{x=x+1}[1/2]{c=0}}", "x",false);
		assertEquals("iff((x=0)&(c=0),0.0;(x=0)&(c=1),0.5;(x=1)&(c=0),1.0;(x=1)&(c=1),1.0)",mainController.getLFP("while(c=1){{x=x+1}[1/2]{c=0}} (x)"));
		mainController.saveFixpointCache();
		mainController.clearFixpointCache();
		assertEquals(null,mainCalculator.getFixpointCache().get("while(c=1){{x=x+1}[1/2]{c=0}} (x)"));
		mainController.loadFixpointCache();
		assertEquals("iff((x=0)&(c=0),0.0;(x=0)&(c=1),0.5;(x=1)&(c=0),1.0;(x=1)&(c=1),1.0)",mainController.getLFP("while(c=1){{x=x+1}[1/2]{c=0}} (x)"));
	}
	
	@Test
	void testSigmaForwarding() {
		assertEquals("x=1.0;x=2.0;{x=x+1}[1/2]{c=0};if{x=2}{x=0}else{c=0};",mainCalculator.sigmaForwarding("x=1;x=x+1;{x=x+1}[1/2]{c=0};if{x=2}{x=0}else{c=0}",new LinkedHashMap<String,String>()));
		assertEquals("x=1.0;x=2.0;x=0;",mainCalculator.sigmaForwarding("x=1;x=x+1;if{x=2}{x=0}else{c=0}",new LinkedHashMap<String,String>()));
		assertEquals("x=1.0;c=0.0;c=0;",mainCalculator.sigmaForwarding("x=1;c=0;if{c=1}{x=0}else{c=0}",new LinkedHashMap<String,String>()));
		assertEquals("x=1.0;c=0.0;skip;",mainCalculator.sigmaForwarding("x=1;c=0;while(c=1){{x=x+1}[1/2]{c=0}}",new LinkedHashMap<String,String>()));
		assertEquals("x=0.0;c=0.0;{y=x+1}[1/2]{c=x+2};skip;",mainCalculator.sigmaForwarding("x=0;c=0;{y=x+1}[1/2]{c=x+2};while(x=1){{x=x+1}[1/2]{c=0}}",new LinkedHashMap<String,String>()));
		assertEquals("x=0.0;c=0.0;min{y=x+1}{c=c+1};skip;",mainCalculator.sigmaForwarding("x=0;c=0;min{y=x+1}{c=c+1};while(x=1){{x=x+1}[1/2]{c=0}}",new LinkedHashMap<String,String>()));

	}
}
