package controller;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import org.apache.commons.lang3.math.NumberUtils;

import model.WPCalculator;
import view.WPCalculatorView;

//main controller

public class MainController implements ControllerHandler {
	
	private WPCalculatorView mainView;	//main view
	private WPCalculator mainCalculator;	//main model
	
	
	/*
	 * connects the model and view 
	 */
	public void link(WPCalculatorView view , WPCalculator wpCalculator) {
        this.mainView = view;
        this.mainCalculator = wpCalculator;
        this.mainView.setHandler(this);
        this.mainCalculator.setHandler(this);
    }

	/*
	 * Prepares and executes the wp transformer function of the model
	 */
	@Override
	public void wp(String C, String f, boolean sigmaForwarding) {
		output( "\n\n" + "Calculating: wp["+C+"]("+f+")");
		String calcResult = "";
		double start = System.currentTimeMillis();
		if(sigmaForwarding) {
			output( "\n\n" + "Sigma-Forwarding activated.");
			String sigmaForwardResult = mainCalculator.sigmaForwarding(C.replace(" ", ""), new LinkedHashMap<String,String>());
    		sigmaForwardResult = sigmaForwardResult.substring(0,sigmaForwardResult.length()-1);
			output( "\n" + "Sigma-Forwarding Result: wp["+ sigmaForwardResult+"]("+f+")");
			calcResult = mainCalculator.calculation(mainCalculator.wp(sigmaForwardResult,f)); 
		}else {
    		calcResult = mainCalculator.calculation(mainCalculator.wp(C.replace(" ", ""),f)); 
		}
		double end = System.currentTimeMillis();
		
		output("\n\n" + "Calculation Time: " + (end - start)/1000 + "s");
	    output("\n" + "Result: " + calcResult);
	    if(C.contains("while(")) {
	    	mainView.prepareEvaluationView(mainCalculator.getWhileLoops());
	    }   		
	}
	
	/*
	 * Prepares the model for calculating and fills it with all necessary inputs from the view
	 */
	@Override
	public boolean prepareCalculationModel(String restriction,String iterationCount, boolean allSigma, String usedVars, String deltaInput) {
		mainView.clearResult();
		mainCalculator.flushWhileLoops();
		mainCalculator.setAllSigmaSelection(allSigma);
		output("Information:");
		
		if(!restriction.isEmpty()) {
			output("\n" + "Variable restriction set to {0,...," + restriction + "}.");
    		mainCalculator.setRestriction(Double.parseDouble(restriction));
		}else {
			//default case
			output("\n" + "No restriction inputted. Set to default {0,...,1}.");
			mainCalculator.setRestriction(1);
		}
		if(!iterationCount.isEmpty()) {
			output("\n" + "Iteration count set to " + iterationCount + ".");
			mainCalculator.setIterationCount(Double.parseDouble(iterationCount));
	    }else {
	    	if (allSigma) {
    			output("\n" + "No iteration count inputted. Taking all sigma default: infinite iteration.");
    			mainCalculator.setIterationCount(Double.POSITIVE_INFINITY);
	    	}else {
	    		//default case 
    			output("\n" + "No iteration count inputted. Taking default count = 10.");
    			mainCalculator.setIterationCount(10);
	    	}
	    }
		if (allSigma) {
			if(usedVars.isEmpty()) {
    			output("\n\n" + "No used variables inputted! You need to input all variables in C.");
    			return false;
			}else {
				mainCalculator.fillAllSigma(usedVars);
			}
			if(deltaInput.isEmpty()) {
    			output("\n" + "No delta for the iteration inputted. Taking default delta = 0.001.");
    			mainCalculator.setIterationDelta(0.001);
			}else {
				mainCalculator.setIterationDelta(Double.parseDouble(deltaInput));
			}
		}
		return true;
	}
	
	/*
	 * Helper function for outputting a string to the view
	 */
	public void output(String text) {
		mainView.getResult().append(text);
		mainView.getResult().validate();
		mainView.getResult().repaint();
		mainView.updateFrame();
	}
	
	/*
	 * Functions to delegate fixpoint cache tasks from view to model
	 */
	@Override
	public void saveFixpointCache() {
		mainCalculator.saveFixpointCache();
	}

	@Override
	public void loadFixpointCache() {
		mainCalculator.loadFixpointCache();		
	}
	
	@Override
	public void clearFixpointCache() {
		mainCalculator.clearFixpointCache();				
	}
	
	/*
	 * Returns the least fixpoint of a given while term if it exists in the models cache
	 */
	@Override
	public String getLFP(String currentWhileTerm) {
		return mainCalculator.getFixpointCache().get(currentWhileTerm);
	}

	/*
	 * Prepares model with all needed inputs from the view in order to perform a fixpoint evaluation on a given witness
	 */
	@Override
	public void evaluateFixpoint(String currentWhileTerm, String witness, String fixpointDelta) {
		output("\n\n" + "*************************");
		output("\n\n" + "Starting fixpoint evaluation. Information: ");
		output("\n" + "Selected While-Term: " + currentWhileTerm);
	    output("\n" + "LFP: " + getLFP(currentWhileTerm));
	    if(fixpointDelta != "" && NumberUtils.isCreatable(fixpointDelta)) {
    	    output("\n" + "Delta: " + fixpointDelta );
	    }else {
    	    output("\n" + "The inputted delta: '" + fixpointDelta + "' is not a number!");
    	    return;
	    }
	    output("\n" + "Witness: " + witness );

	    mainCalculator.evaluateFixpoint(currentWhileTerm, witness, fixpointDelta, 1, new LinkedHashSet<String>());
	    //TODO do reduction of state here with returning sigmaSet?
	}
	
}
