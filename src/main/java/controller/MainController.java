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
		if(mainView.getAllSigmaIteration().isSelected()) {
			mainCalculator.fillAllSigma(getUsedVars(C+f));
		}
		output("\n" + "Calculating: wp["+C+"]("+f+")",1);
		String calcResult = "";
		if(sigmaForwarding) {
			output("\n" + "Sigma-Forwarding activated.",1);
			String sigmaForwardResult = mainCalculator.sigmaForwarding(prepareProgram(C), new State());
    		sigmaForwardResult = sigmaForwardResult.substring(0,sigmaForwardResult.length()-1);
			output("Sigma-Forwarding Result: wp[" + C + "]("+f+") = "+ sigmaForwardResult,1);
			calcResult = mainCalculator.calculation(mainCalculator.wp(sigmaForwardResult,f,0)); 
		}else {
    		calcResult = mainCalculator.calculation(mainCalculator.wp(prepareProgram(C),f,0)); 
		}
		double end = System.currentTimeMillis();
		
		output("\n" + "Calculation Time: " + (end - start)/1000 + "s",1);
	    output("Result: " + calcResult,1);
	    if(C.contains("while(")) {
	    	mainView.prepareEvaluationView(mainCalculator.getWhileLoops());
	    }   		
	}
	
	/*
	 * Prepares and executes the createAllSigmaFixpoint function of the model
	 */
	@Override
	public String createAllSigmaFixpoint(String currentWhileTerm) {
		output("\n" + "*************************",1);
	    output("\n" + "Converting to allSigma fixpoint notation...",1);
		String currentLFP = mainCalculator.getFixpointCache().get(currentWhileTerm);
		double start = System.currentTimeMillis();
		String newLFP = mainCalculator.convertFixpoint(currentLFP, getUsedVars(currentWhileTerm)).getContentString();
		mainCalculator.getFixpointCache().replace(currentWhileTerm, newLFP);
		double end = System.currentTimeMillis();
		
		output("Success! Calculation Time: " + (end - start)/1000 + "s",1);
		output("Converted fixpoint:",1);
	    output(newLFP,1);
		output("\n" + "*************************",1);


		return newLFP;
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
		output("\n" +"*************************",1);
		output("\n" +"Starting fixpoint evaluation. Information: ",1);
		output("Selected While-Term: " + currentWhileTerm,1);
	    output("LFP: " + getLFP(currentWhileTerm),1);
	    if(fixpointDelta != "" && NumberUtils.isCreatable(fixpointDelta)) {
    	    output("Delta: " + fixpointDelta ,1);
	    }else {
    	    output("The inputted delta: '" + fixpointDelta + "' is not a number!",1);
    	    return;
	    }
	    output("Witness: " + witness,1);
		output("\n" + "*************************",1);

	    mainCalculator.evaluateFixpoint(currentWhileTerm, witness, fixpointDelta, 1, new LinkedHashSet<String>());
	    double end = System.currentTimeMillis();
		
		output("Calculation Time: " + (end - start)/1000 + "s",1);
	}
	
	/*
	 * Prepares the model for calculating and fills it with all necessary inputs from the view
	 */
	@Override
	public boolean prepareCalculationModel(String restriction,String iterationCount, boolean allSigma, String deltaInput) {
		mainView.clearResult();
		mainCalculator.flushWhileLoops();
		mainCalculator.setAllSigmaSelection(allSigma);
		output("Information:",1);
		
		output("Output detail level: " + mainView.getLogLevel(),1);
		if(mainView.getLogToFile()) {
			output("Writing result to file in logs/app.log",1);
		}
		if(allSigma) {
			output("Chosen iteration method: all-sigma fixpoint iteration",1);
		}else {
			output("Chosen iteration method: direct fixpoint iteration",1);
		}
		if(!restriction.isEmpty()) {
			output("Variable restriction set to {0,...," + restriction + "}.",1);
    		mainCalculator.setRestriction(Double.parseDouble(restriction));
		}else {
			//default case
			output("No restriction inputted. Set to default {0,...,1}",1);
			mainCalculator.setRestriction(1);
		}
		if(!iterationCount.isEmpty()) {
			output("Iteration count set to " + iterationCount,1);
			mainCalculator.setIterationCount(Double.parseDouble(iterationCount));
	    }else {
	    	if (allSigma) {
    			output("No iteration count inputted. Taking all-sigma default: infinite iteration",1);
    			mainCalculator.setIterationCount(Double.POSITIVE_INFINITY);
	    	}else {
	    		//default case 
    			output("No iteration count inputted. Taking default count = 10",1);
    			mainCalculator.setIterationCount(10);
	    	}
	    }
		if (allSigma) {
			if(deltaInput.isEmpty()) {
    			output("No delta for the iteration inputted. Taking default delta = 0.001",1);
    			mainCalculator.setIterationDelta(0.001);
			}else {
				mainCalculator.setIterationDelta(Double.parseDouble(deltaInput));
			}
		}
		output("-----------------------------------",1);
		return true;
	}
	
	/*
	 * Helper function for outputting a string to the view (considering recursionDepth)
	 */
	public void output(String text, int logLevel, int recursionDepth) {
		String depthBuffer ="";
		for(int i=0; i<recursionDepth; i++) {
			depthBuffer = depthBuffer + "        "; 
		}
		text = "\n" + depthBuffer + text;
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
	 * Helper function for outputting a string to the view
	 */
	public void output(String text, int logLevel) {
		text = "\n" + text;
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
	 * Helper function that prepares the inputed program for calculation
	 */
	public String prepareProgram(String C) {
		C = C.replace(" ", "");
		C = C.replace(":=", "=");
		return C;
	}
	
	/*
	 * Helper function that extracts the used variables from a given program
	 */
	public String getUsedVars(String C) {
		String usedVars = C.replaceAll("[^A-Za-z]", "");
		usedVars = usedVars.replaceAll("[!^whilepfmnrs]", "");
		String result="";
		for(int i = 0; i < usedVars.length(); i++) {
			String temp = ""+usedVars.charAt(i);
			if(!result.contains(temp)){
				result = result.concat(temp);
			}
		}
		return result;
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
