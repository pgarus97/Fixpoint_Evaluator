package controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import model.WPCalculator;
import view.WPCalculatorView;

class MainControllerTest {

	WPCalculator mainCalculator = new WPCalculator();
	WPCalculatorView mainView = new WPCalculatorView();
	MainController mainController= new MainController();
	
	MainControllerTest(){
		mainCalculator.setRestriction(1); //default test case
		mainCalculator.setIterationSelection(1); //default case = all-sigma
		mainCalculator.setIterationDelta(0.001); //default case
		mainCalculator.setIterationCount(5); //default case
		mainController.link(mainView, mainCalculator);
	}
	
	@Test
	void testCreateAllSigmaFixpoint() {
		mainController.wp("while(c=1){{x=x+1}[1/2]{c=0}}","x",false);
		assertEquals("iff((c=0)&(x=0),0.0;(c=0)&(x=1),1.0;(c=1)&(x=0),0.5;(c=1)&(x=1),1.0)",mainController.createAllSigmaFixpoint("while(c=1){{x=x+1}[1/2]{c=0}} (x)"));
	}
	
	@Test
	void testLink() {
		mainController.link(mainView, mainCalculator);
		assertEquals(mainView.getHandler(),mainController);
		assertEquals(mainCalculator.getHandler(),mainController);
	}

	@Test
	void testWp() {
		assertEquals("1.0", mainController.wp("c=0;x=1;while(c=1){{x=x+1}[1/2]{c=0}}", "x",false));
		assertEquals("1.0", mainController.wp("c=0;x=1;while(c=1){{x=x+1}[1/2]{c=0}}", "x",true));

	}

	@Test
	void testIsConverted() {
		mainController.wp("while(c=1){{x=x+1}[1/2]{c=0}}","x",false);
		assertTrue(mainController.isConverted("while(c=1){{x=x+1}[1/2]{c=0}} (x)"));
		assertFalse(mainController.isConverted("while(c=0){{x=x+1}[1/2]{c=0}} (x)"));
	}

	@Test
	void testEvaluateFixpoint() {
		assertEquals("[]", mainController.evaluateFixpoint("while(c=1){{x=x+1}[1/2]{c=0}} (x)", "iff((x=0)&(c=0),0.0;(x=0)&(c=1),0.5;(x=1)&(c=0),1.0;(x=1)&(c=1),1.0)", "0.1").toString());
		assertEquals(null, mainController.evaluateFixpoint("while(c=1){{x=x+1}[1/2]{c=0}} (x)", "iff((x=0)&(c=0),0.0;(x=0)&(c=1),0.5;(x=1)&(c=0),1.0;(x=1)&(c=1),1.0)", null));

	}

	@Test
	void testPrepareCalculationModel() {
		mainController.prepareCalculationModel("1","1",1,"1");
		assertEquals(1.0, mainCalculator.getIterationCount());
		assertEquals(1.0, mainCalculator.getRestriction());
		assertEquals(1, mainCalculator.getIterationSelection());
		assertEquals(1.0, mainCalculator.getIterationDelta());

		}

	@Test
	void testOutputStringIntInt() {
		mainController.output("test", 1, 2);
		assertEquals("\n" + "        " + "        " + "test", mainView.getResult().getText());
	}

	@Test
	void testOutputStringInt() {
		mainController.output("test", 1);
		assertEquals("\n" +"test", mainView.getResult().getText());	
	}

	@Test
	void testPrepareProgram() {
		assertEquals("x=0;x=0", mainController.prepareProgram("x:=0; x=0"));
	}

	@Test
	void testGetUsedVars() {
		assertEquals("xc", mainController.getUsedVars("x=1;c=0;while(c=1){{x=x+1}[1/2]{c=0}}"));
	}

	@Test
	void testCacheMethods() {
		mainCalculator.setRestriction(1);
		mainCalculator.fillAllSigma("xc");
		mainController.clearFixpointCache();
		
		assertEquals(null,mainController.getLFP("while(c=1){{x=x+1}[1/2]{c=0}} (x)"));
		mainController.wp("while(c=1){{x=x+1}[1/2]{c=0}}", "x",false);
		assertEquals("iff((c=0)&(x=0),0.0;(c=0)&(x=1),1.0;(c=1)&(x=0),0.5;(c=1)&(x=1),1.0)",mainController.getLFP("while(c=1){{x=x+1}[1/2]{c=0}} (x)"));
		mainController.saveFixpointCache();
		mainController.clearFixpointCache();
		assertEquals(null,mainCalculator.getWPCache().get("while(c=1){{x=x+1}[1/2]{c=0}} (x)"));
		mainController.loadFixpointCache();
		assertEquals("iff((c=0)&(x=0),0.0;(c=0)&(x=1),1.0;(c=1)&(x=0),0.5;(c=1)&(x=1),1.0)",mainController.getLFP("while(c=1){{x=x+1}[1/2]{c=0}} (x)"));
	}
	
}
