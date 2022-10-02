package controller;

import java.util.LinkedHashSet;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.State;
import model.WPCalculator;
import view.WPCalculatorView;

//main controller

public class MainController implements ControllerHandler {
	
	private WPCalculatorView mainView;	//main view
	private WPCalculator mainCalculator;	//main model
	private static Logger logger = LoggerFactory.getLogger(MainController.class);

	
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
		double start = System.currentTimeMillis();
		output("\n\n" + "Calculating: wp["+C+"]("+f+")",1);
		String calcResult = "";
		if(sigmaForwarding) {
			output( "\n\n" + "Sigma-Forwarding activated.",1);
			String sigmaForwardResult = mainCalculator.sigmaForwarding(C.replace(" ", ""), new State());
    		sigmaForwardResult = sigmaForwardResult.substring(0,sigmaForwardResult.length()-1);
			output( "\n" + "Sigma-Forwarding Result: wp["+ sigmaForwardResult+"]("+f+")",1);
			calcResult = mainCalculator.calculation(mainCalculator.wp(sigmaForwardResult,f)); 
		}else {
    		calcResult = mainCalculator.calculation(mainCalculator.wp(C.replace(" ", ""),f)); 
		}
		double end = System.currentTimeMillis();
		
		output("\n\n" + "Calculation Time: " + (end - start)/1000 + "s",1);
	    output("\n" + "Result: " + calcResult,1);
	    if(C.contains("while(")) {
	    	mainView.prepareEvaluationView(mainCalculator.getWhileLoops());
	    }   		
	}
	
	/*
	 * Prepares and executes the createAllSigmaFixpoint function of the model
	 */
	@Override
	public String createAllSigmaFixpoint(String currentWhileTerm, String usedVars) {
	    output("\n\n" + "Converting to allSigma fixpoint notation...",1);
		String currentLFP = mainCalculator.getFixpointCache().get(currentWhileTerm);
		double start = System.currentTimeMillis();
		String result = mainCalculator.createAllSigmaFixpoint(currentLFP, usedVars);
		mainCalculator.getFixpointCache().replace(currentWhileTerm, result);
		double end = System.currentTimeMillis();
		
		output("\n" + "Success! Calculation Time: " + (end - start)/1000 + "s",1);
		output("\n" + "Converted fixpoint:",1);
	    output("\n" + result,1);

		return result;
	}

	/*
	 * Checks if a given while term (currentWhileTerm) has already been converted to a iff-clause
	 */
	@Override
	public boolean isConverted(String currentWhileTerm) {
		String currentLFP = mainCalculator.getFixpointCache().get(currentWhileTerm);
		if(currentLFP.startsWith("iff")) {
			return true;
		}else {
			return false;
		}
	}
	
	/*
	 * Prepares model with all needed inputs from the view in order to perform a fixpoint evaluation on a given witness
	 */
	@Override
	public void evaluateFixpoint(String currentWhileTerm, String witness, String fixpointDelta) {
	    double start = System.currentTimeMillis();
		output("\n\n" + "*************************",1);
		output("\n\n" + "Starting fixpoint evaluation. Information: ",1);
		output("\n" + "Selected While-Term: " + currentWhileTerm,1);
	    output("\n" + "LFP: " + getLFP(currentWhileTerm),1);
	    if(fixpointDelta != "" && NumberUtils.isCreatable(fixpointDelta)) {
    	    output("\n" + "Delta: " + fixpointDelta ,1);
	    }else {
    	    output("\n" + "The inputted delta: '" + fixpointDelta + "' is not a number!",1);
    	    return;
	    }
	    output("\n" + "Witness: " + witness ,1);

	    mainCalculator.evaluateFixpoint(currentWhileTerm, witness, fixpointDelta, 1, new LinkedHashSet<String>());
	    double end = System.currentTimeMillis();
		
		output("\n\n" + "Calculation Time: " + (end - start)/1000 + "s",1);
	}
	
	/*
	 * Prepares the model for calculating and fills it with all necessary inputs from the view
	 */
	@Override
	public boolean prepareCalculationModel(String restriction,String iterationCount, boolean allSigma, String usedVars, String deltaInput) {
		mainView.clearResult();
		mainCalculator.flushWhileLoops();
		mainCalculator.setAllSigmaSelection(allSigma);
		output("Information:",1);
		
		output("\n" + "Output detail level: " + mainView.getLogLevel(),1);
		if(mainView.getLogToFile()) {
			output("\n" + "Writing result to file in logs/app.log",1);
		}
		if(allSigma) {
			output("\n" + "Chosen iteration method: all-sigma fixpoint iteration",1);
		}else {
			output("\n" + "Chosen iteration method: direct fixpoint iteration",1);
		}
		if(!restriction.isEmpty()) {
			output("\n" + "Variable restriction set to {0,...," + restriction + "}.",1);
    		mainCalculator.setRestriction(Double.parseDouble(restriction));
		}else {
			//default case
			output("\n" + "No restriction inputted. Set to default {0,...,1}",1);
			mainCalculator.setRestriction(1);
		}
		if(!iterationCount.isEmpty()) {
			output("\n" + "Iteration count set to " + iterationCount,1);
			mainCalculator.setIterationCount(Double.parseDouble(iterationCount));
	    }else {
	    	if (allSigma) {
    			output("\n" + "No iteration count inputted. Taking all-sigma default: infinite iteration",1);
    			mainCalculator.setIterationCount(Double.POSITIVE_INFINITY);
	    	}else {
	    		//default case 
    			output("\n" + "No iteration count inputted. Taking default count = 10",1);
    			mainCalculator.setIterationCount(10);
	    	}
	    }
		if (allSigma) {
			if(usedVars.isEmpty()) {
    			output("\n\n" + "No used variables inputted! You need to input all variables in C!",1);
    			return false;
			}else {
				mainCalculator.fillAllSigma(usedVars); //TODO this also needs to hold for conversion? direct iteration
			}
			if(deltaInput.isEmpty()) {
    			output("\n" + "No delta for the iteration inputted. Taking default delta = 0.001",1);
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
	public void output(String text, int logLevel) {
		if(mainView.getLogLevel() >= logLevel) {
			if(mainView.getLogToFile()) {
		        logger.warn(text);
			}
			logger.info(text);
			mainView.getResult().append(text);
			mainView.getResult().validate();
			mainView.getResult().repaint();
			mainView.updateFrame();
		}
		
	    
	}
	
	/*
	 * Returns the least fixpoint of a given while term if it exists in the models cache
	 */
	@Override
	public String getLFP(String currentWhileTerm) {
		return mainCalculator.getFixpointCache().get(currentWhileTerm);
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
	

}
