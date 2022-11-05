package view;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import javax.swing.JToggleButton;

import org.junit.jupiter.api.Test;

class WPCalculatorViewTest {

	WPCalculatorView mainView = new WPCalculatorView();

	@Test
	void testPrepareEvaluationView() {
		ArrayList<String> input = new ArrayList<String>();
		input.add("Test");
		mainView.prepareEvaluationView(input);
		assertEquals("Test",mainView.getWhileLoops().get(0).getText());
	}

	@Test
	void testPrepareCalculationView() {
		mainView.getWhileLoops().add(new JToggleButton("Test"));
		mainView.prepareCalculationView();
		assertTrue(mainView.getWhileLoops().isEmpty());
	}

	@Test
	void testGetLogToFile() {
		mainView.getFileLog().setSelected(true);
		assertTrue(mainView.getLogToFile());
		mainView.getFileLog().setSelected(false);
	}

	@Test
	void testGetLogLevel() {
		mainView.getMinimalLog().setSelected(true);
		assertEquals(1, mainView.getLogLevel());
		mainView.getMinimalLog().setSelected(false);
		
		mainView.getDetailedLog().setSelected(true);
		assertEquals(2, mainView.getLogLevel());
		mainView.getDetailedLog().setSelected(false);
	}

	@Test
	void testGetIterationSelection() {
		mainView.getDefaultIteration().setSelected(true);
		assertEquals(0, mainView.getIterationSelection());
		mainView.getDefaultIteration().setSelected(false);
		
		mainView.getAllSigmaIteration().setSelected(true);
		assertEquals(1, mainView.getIterationSelection());
		mainView.getAllSigmaIteration().setSelected(false);

		mainView.getDirectIteration().setSelected(true);
		assertEquals(2, mainView.getIterationSelection());
		mainView.getDirectIteration().setSelected(false);
		
		mainView.getUpsideDown().setSelected(true);
		assertEquals(3, mainView.getIterationSelection());
		mainView.getUpsideDown().setSelected(false);
	}

	@Test
	void testClearResult() {
		mainView.getResult().setText("test");
		mainView.clearResult();
		assertEquals("",mainView.getResult().getText());
	}

}
