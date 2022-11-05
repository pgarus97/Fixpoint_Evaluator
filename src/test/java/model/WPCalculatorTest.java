package model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import controller.MainController;
import view.WPCalculatorView;

class WPCalculatorTest {

	WPCalculator mainCalculator = new WPCalculator();
	WPCalculatorView mainView = new WPCalculatorView();
	MainController mainController= new MainController();
	
	WPCalculatorTest(){
		mainController.link(mainView, mainCalculator);
		mainCalculator.setRestriction(10); //default test case
		mainCalculator.setIterationSelection(1); //default case = all-sigma
		mainCalculator.setIterationDelta(0.001); //default case
		mainCalculator.setIterationCount(5); //default case
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
	void testSigmaForwarding() {
		assertEquals("x=1.0;x=2.0;{x=x+1}[1/2]{c=0};if{x=2}{x=0}else{c=0};",mainCalculator.sigmaForwarding("x=1;x=x+1;{x=x+1}[1/2]{c=0};if{x=2}{x=0}else{c=0}",new State()));
		assertEquals("x=1.0;x=2.0;x=0;",mainCalculator.sigmaForwarding("x=1;x=x+1;if{x=2}{x=0}else{c=0}",new State()));
		assertEquals("x=1.0;c=0.0;c=0;",mainCalculator.sigmaForwarding("x=1;c=0;if{c=1}{x=0}else{c=0}",new State()));
		assertEquals("x=1.0;c=0.0;",mainCalculator.sigmaForwarding("x=1;c=0;while(c=1){{x=x+1}[1/2]{c=0}}",new State()));
		assertEquals("x=0.0;c=0.0;{y=x+1}[1/2]{c=x+2};",mainCalculator.sigmaForwarding("x=0;c=0;{y=x+1}[1/2]{c=x+2};while(x=1){{x=x+1}[1/2]{c=0}}",new State()));
		assertEquals("x=0.0;c=0.0;min{y=x+1}{c=c+1};",mainCalculator.sigmaForwarding("x=0;c=0;min{y=x+1}{c=c+1};while(x=1){{x=x+1}[1/2]{c=0}};skip",new State()));
	}
	@Test
	void testAssignments() {
		
		assertEquals("25.0",mainCalculator.calculation(mainCalculator.wp("x=5", "x^2",0)));
		assertEquals("100.0",mainCalculator.calculation(mainCalculator.wp("x=5;x=10", "x^2",0)));	
		assertEquals("100.0",mainCalculator.calculation(mainCalculator.wp("x=5;x=10;y=2", "x^2",0)));	
		assertEquals("25.0",mainCalculator.calculation(mainCalculator.wp("x=5;y=10", "x^2",0)));
		assertEquals("100.0", mainCalculator.calculation(mainCalculator.wp("x=5;y=10", "y^2",0)));
		assertEquals("15.0", mainCalculator.calculation(mainCalculator.wp("x=5;y=10", "x+y",0)));
	}
	
	@Test
	void testProbability() {
		
		assert mainCalculator.calculation(mainCalculator.wp("{x=5}[4/5]{x=10}","x^2",0)).equals("40.0");
		assert mainCalculator.calculation(mainCalculator.wp("{x=5}[1/2]{x=10};{x=3}[1/2]{x=4}","x",0)).equals("3.5");
		assert mainCalculator.calculation(mainCalculator.wp("x=0;y=0;{skip}[1/2]{x=x+2}","x",0)).equals("1.0");
		assert mainCalculator.calculation(mainCalculator.wp("x=5;{skip}[1/2]{x=x+2}","x",0)).equals("6.0");
		assert mainCalculator.calculation(mainCalculator.wp("{x=5}[1/2]{x=10};{x=3}[1/2]{x=4};x=6","x",0)).equals("6.0");
	}
	
	@Test
	void testConditional() {

		assert mainCalculator.calculation(mainCalculator.wp("x=5;if{x<5}{x=x+1}else{x=x-1}", "x^2",0)).equals("16.0");
		assert mainCalculator.calculation(mainCalculator.wp("x=5;if{x<5}{x=x+1}else{x=x-1};x=8", "x^2",0)).equals("64.0");
		assert mainCalculator.calculation(mainCalculator.wp("x=5;{x=3}[1/2]{x=10};if{x<5}{x=x+1}else{x=x-1}", "x",0)).equals("6.5");
		assert mainCalculator.calculation(mainCalculator.wp("x=5;if{x<5}{x=x+1}else{if{x=5}{x=3}else{x=8}}", "x",0)).equals("3.0");
		assert mainCalculator.calculation(mainCalculator.wp("x=5;if{x<5}{x=x+1}else{min{x=x+1}{x=3}", "x",0)).equals("3.0");
	}
	
	@Test
	void testDemonicChoice() {

		assert mainCalculator.calculation(mainCalculator.wp("x=1;min{x=x+1}{x=3}","x",0)).equals("2.0");
		assert mainCalculator.calculation(mainCalculator.wp("x=3;min{x=x+1}{x=3}","x",0)).equals("3.0");
	}
	
	@Test
	void testDirectWhile() {	
		mainCalculator.setIterationCount(10);
		mainCalculator.setIterationSelection(2);


		//assignment
		assertEquals("1.0",mainCalculator.calculation(mainCalculator.wp("c=0;x=1;while(c=1){x=x+1}","x",0)));
		//probabilistic
		assertEquals("1.0", mainCalculator.calculation(mainCalculator.wp("c=0;x=1;while(c=1){{x=x+1}[1/2]{c=0}}", "x",0)));
		mainCalculator.setRestriction(2);
		//if-clause
		assertEquals("2.0",mainCalculator.calculation(mainCalculator.wp("x=0;c=0;while(c<2){if{x<2}{x=x+1}{c=3}}","x",0)));
		//demonic
		assertEquals("1.0",mainCalculator.calculation(mainCalculator.wp("x=2;c=1;while(c=1){min{x=x+1;c=0}{x=x-1;c=0}}","x",0)));
		//while in while
		mainCalculator.setRestriction(3);
		mainCalculator.setIterationCount(3);
		assertEquals("2.0",mainCalculator.calculation(mainCalculator.wp("x=0;c=0;while(x<2){while(c<2){c=c+1;x=x+1}}","x",0)));

		
	}
	
	@Test
	void testAllSigmaWhile() {	
		
		mainCalculator.setIterationSelection(1);
		mainCalculator.setRestriction(2);
		mainCalculator.fillAllSigma("xc");

		//assignment
		assertEquals("1.0",mainCalculator.calculation(mainCalculator.wp("c=0;x=1;while(c=1){x=x+1}","x",0)));
		//probabilistic
		assertEquals("1.0", mainCalculator.calculation(mainCalculator.wp("c=0;x=1;while(c=1){{x=x+1}[1/2]{c=0}}", "x",0)));
		//if-clause
		assertEquals("2.0",mainCalculator.calculation(mainCalculator.wp("x=0;c=0;while(c<2){if{x<2}{x=x+1}{c=3}}","x",0)));
		//demonic
		assertEquals("1.0",mainCalculator.calculation(mainCalculator.wp("x=2;c=1;while(c=1){min{x=x+1;c=0}{x=x-1;c=0}}","x",0)));
		//while in while
		mainCalculator.setRestriction(3);
		mainCalculator.fillAllSigma("xc");
		mainCalculator.setIterationCount(3);
		assertEquals("2.0",mainCalculator.calculation(mainCalculator.wp("x=0;c=0;while(x<2){while(c<2){c=c+1;x=x+1}}","x",0)));
	}
	
	@Disabled
	void testUpsideDownEvaluation() {
		mainCalculator.setIterationSelection(3);
		mainCalculator.fillAllSigma("xc");
		assertEquals("1.0",mainCalculator.calculation(mainCalculator.wp("c=0;x=1;while(c=1){x=x+1}","x",0)));
	}
	
	@Test
	void testDefaultWhile() {	
		
		mainCalculator.setIterationSelection(0);
		mainCalculator.setRestriction(2);
		mainCalculator.fillAllSigma("xc");

		//assignment
		assertEquals("1.0",mainCalculator.calculation(mainCalculator.wp("c=0;x=1;while(c=1){x=x+1}","x",0)));
		//probabilistic
		assertEquals("1.0", mainCalculator.calculation(mainCalculator.wp("c=0;x=1;while(c=1){{x=x+1}[1/2]{c=0}}", "x",0)));
		//if-clause
		assertEquals("2.0",mainCalculator.calculation(mainCalculator.wp("x=0;c=0;while(c<2){if{x<2}{x=x+1}{c=3}}","x",0)));
		//demonic
		assertEquals("1.0",mainCalculator.calculation(mainCalculator.wp("x=2;c=1;while(c=1){min{x=x+1;c=0}{x=x-1;c=0}}","x",0)));
		//while in while
		mainCalculator.setRestriction(3);
		mainCalculator.fillAllSigma("xc");
		assertEquals("2.0",mainCalculator.calculation(mainCalculator.wp("x=0;c=0;while(x<2){while(c<2){c=c+1;x=x+1}}","x",0)));
	}
	
	@Test
	void testfillAllSigma() {
		mainCalculator.setRestriction(1); //var from {0,1}
		
		ArrayList<State> allSigma = mainCalculator.fillAllSigma("xy");
		
		assertEquals("0",allSigma.get(0).get("x"));
		assertEquals("0",allSigma.get(0).get("y"));
		
		assertEquals("0",allSigma.get(1).get("x"));
		assertEquals("1",allSigma.get(1).get("y"));
		
		assertEquals("1",allSigma.get(2).get("x"));
		assertEquals("0",allSigma.get(2).get("y"));

		assertEquals("1",allSigma.get(3).get("x"));
		assertEquals("1",allSigma.get(3).get("y"));
	}
	
	@Test
	void testCalculateConcreteSigma() {
		
		mainCalculator.setRestriction(1); //var from {0,1}
		ArrayList<State> allSigma = mainCalculator.fillAllSigma("xy");

		assertEquals(1.0,mainCalculator.calculateSigma("if(x=0,1,y)", allSigma.get(0)));
		assertEquals(1.0,mainCalculator.calculateSigma("if(x=0,1,y)", allSigma.get(1)));
		assertEquals(0.0,mainCalculator.calculateSigma("if(x=0,1,y)", allSigma.get(2)));
		assertEquals(1.0,mainCalculator.calculateSigma("if(x=0,1,y)", allSigma.get(3)));
		assertEquals(null,mainCalculator.calculateSigma("if(x=0,1,z)", allSigma.get(3)));
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
		Fixpoint fixpointMap = new Fixpoint("iff((x=0)&(c=0),0.0;(x=0)&(c=1),1.0;(x=1)&(c=0),1.0;(x=1)&(c=1),2.0)",2);
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
	}
	
	@Test
	void testTruncate() {
		assertEquals("1.0",mainCalculator.truncate("#{1}"));
		assertEquals("0",mainCalculator.truncate("#{-1}"));
		assertEquals("10.0",mainCalculator.truncate("#{11}"));
		assertEquals("#{x+1}",mainCalculator.truncate("#{x+1}"));
		assertEquals("2.0",mainCalculator.truncate("#{1+1}"));
		assertEquals("#{x+4.0}",mainCalculator.truncate("#{x+#{1+3}}"));
		assertEquals("6.0",mainCalculator.truncate("#{2+#{1+3}}"));
		assertEquals("#{x+#{y+3}}",mainCalculator.truncate("#{x+#{y+3}}"));
	}
}
