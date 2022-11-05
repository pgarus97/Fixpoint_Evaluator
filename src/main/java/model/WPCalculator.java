package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.Function;

import com.google.common.collect.Lists;
//Parser Documentation: https://github.com/mariuszgromada/MathParser.org-mXparser

import controller.ControllerHandler;

//main model

public class WPCalculator {

private ControllerHandler mainController;
private ArrayList<State> allSigma = new ArrayList<State>();
private ArrayList<String> whileLoops = new ArrayList<String>();
private LinkedHashMap<String,String> wpCache = new LinkedHashMap<String,String>();
private double restriction;
private double iterationCount;
private int iterationSelection; // 0 = default ; 1 = all-sigma ; 2 = direct
private double iterationDelta;

	/*
	 * Main method that represents the weakest pre-expectation transformer function (wp)
	 * Takes a program (C) and a post expectation (f) as input and recursively calculates the result of the formula wp[C](f) for any sigma (variable assignment).
	 */
	public String wp(String C, String f, int recursionDepth) {
		//sequential process	
		if(!whileLoops.contains(C+" ("+f+")") && C.startsWith("while")) {
			whileLoops.add(C+" ("+f+")");
		}
	
		String result = "";
		mainController.output( "Now computing: wp["+C+"]("+f+")",2,recursionDepth); 
		recursionDepth +=1;
		String C1 = getSequentialCut(C);
		if(!C1.equals(C)) {
			String C2 = C.substring(C1.length()+1);
			mainController.output( "Sequential process. Breaking down into: wp["+C1+"](wp["+C2+"]("+f+"))",2,recursionDepth); 
			result = wp(C1,(wp(C2,f,recursionDepth)),recursionDepth);
		}else {
			if(wpCache.containsKey(C+" ("+f+")")) {
				mainController.output("Skipped because value has been found in WP-Cache.",2,recursionDepth);
				mainController.output("Cached WP-Result: "+ wpCache.get(C+" ("+f+")"),2,recursionDepth);
				return wpCache.get(C+" ("+f+")");
			}
			
			else if(C.startsWith("min{")) {
				//demonic choice process
				mainController.output( "Enter Demonic Choice process:",2,recursionDepth);
				String demC1 = getInsideBracket(C.substring(C.indexOf("{")+1));	
				String demC2 = C.substring(C.indexOf(demC1));
				demC2 = getInsideBracket(demC2.substring(demC2.indexOf("{")+1));
				mainController.output( "Demonic Choice process. Breaking down into: min(" + "wp[" + demC1 + "]("+f+")" + "," + "wp[" + demC2 + "]("+f+")" + ")",2,recursionDepth); 
				String resultC1 = wp(demC1,f,recursionDepth+1);
				String resultC2 = wp(demC2,f,recursionDepth+1);

				mainController.output( "Demonic Choice process result: wp[" + C + "]("+f+") = min(" + resultC1 + "," + resultC2 + ")",2,recursionDepth); 
				result = calculation("min(" + resultC1 + "," + resultC2 + ")");
			}
	
			else if(C.startsWith("if") && !C.startsWith("iff")) {
				//conditional process
				mainController.output( "Enter conditional process:",2,recursionDepth); 
				String condition = getInsideBracket(C.substring(C.indexOf("{")+1));
				String ifC1 = C.substring(condition.length()+4);
				ifC1 = getInsideBracket(ifC1.substring(ifC1.indexOf("{")+1));
				String ifC2 = C.substring(C.indexOf(ifC1));
				ifC2 = getInsideBracket(ifC2.substring(ifC2.indexOf("{")+1));
				mainController.output( "Breaking down into: if("+condition+") then wp["+ ifC1 +"]("+f+") else wp["+ ifC2+ "]("+f+")",2,recursionDepth); 

				String condResult = calculation(condition);
				if(condResult.equals("1.0")) {
					mainController.output( "If-Condition true, only continue with wp[" + ifC1 + "]("+f+")",2,recursionDepth); 
					String resultC1 = wp(ifC1,f,recursionDepth+1);
					result = calculation(resultC1);
				}else if(condResult.equals("0.0")) {
					mainController.output( "If-Condition false, only continue with wp[" + ifC2 + "]("+f+")",2,recursionDepth); 
					String resultC2 = wp(ifC2,f,recursionDepth+1);
					result = calculation(resultC2);
				} else {
				String resultC1 = wp(ifC1,f,recursionDepth+1);
				String resultC2 = wp(ifC2,f,recursionDepth+1);
				mainController.output( "Conditional process result: wp[" + C + "]("+f+") = if("+condition+") then "+ resultC1 +" else "+ resultC2,2,recursionDepth); 

				result = calculation("if(" + condition + "," + resultC1 + "," + resultC2 + ")");
				}
			}
			
			else if(C.startsWith("{")){
				//probability process
				mainController.output( "Enter probability process:",2,recursionDepth); 
				String probC1 = getInsideBracket(C.substring(C.indexOf("{")+1));

				String probC2 = C.substring(probC1.length());
				String probability = probC2.substring(probC2.indexOf("[")+1,probC2.indexOf("]"));
				probC2 = getInsideBracket(probC2.substring(probC2.indexOf("{")+1));

				Expression negProbability = new Expression ("1-"+probability);
				mainController.output( "Breaking down into: " + probability + " * " + "wp[" + probC1 + "]("+f+")" +" + "+ negProbability.calculate() + " * " + "wp[" + probC2 + "]("+f+")",2,recursionDepth); 

				String resultC1 = wp(probC1,f,recursionDepth+1);
				String resultC2 = wp(probC2,f,recursionDepth+1);
				mainController.output( "Probability process result: wp[" + C + "]("+f+") = " + probability + " * " + resultC1 +" + "+ negProbability.calculate() + " * " + resultC2,2,recursionDepth); 
				result = calculation("(" + probability + " * "+ resultC1 +" + "+ negProbability.calculate() + " * " + resultC2+")");

			}
			
			else if(C.startsWith("while")){
				//while process
				mainController.output("Enter while process:",2,recursionDepth); 
				String condition = C.substring(C.indexOf("(")+1,C.indexOf("{")-1);
				String whileC = C.substring(condition.length());
				whileC = getInsideBracket(whileC.substring(whileC.indexOf("{")+1));

				switch(iterationSelection) {
					case 0: 
						result = fixpointIteration(condition, whileC, f, recursionDepth+1);
						break;
					case 1: 
						result = allSigmaFixpointIteration(condition, whileC, f, recursionDepth+1);
						break;
					case 2:	
						result = directFixpointIteration(condition, whileC, f, recursionDepth+1); 
						break;
				}
				
			}else {
				//variable assignments
				if(C.startsWith("skip")){
					mainController.output("Enter skip process:",2,recursionDepth); 
					String skipResult = C.replace("skip", f);
					result = calculation(skipResult);
				}else {
					mainController.output("Enter assignment process:",2,recursionDepth); 
					String indexC = C.substring(0,1);
					String cutC = C.substring(C.indexOf("=")+1);
					String assignResult = f.replace(indexC, "r(" + cutC + ")");
					
					//preemptive if-clause resolution optimization
					if(assignResult.startsWith("if") && !assignResult.startsWith("iff")) {
						String condition = getInsideIf(assignResult.substring(3));
						String assignifC1 = assignResult.substring(condition.length()+4);
						System.out.println("condition=" +condition);
						System.out.println("assignifC1=" +assignifC1);
						System.out.println("assignResult" +assignResult);
						assignifC1 =  getInsideIf(assignifC1);	
						String assignifC2 = assignResult.substring(condition.length()+4+assignifC1.length()+1);

						System.out.println("assignifC1=" +assignifC1);
						if(calculation(condition).equals("1.0")) {
							mainController.output("Found possible if-assignment optimization!",2,recursionDepth); 
							mainController.output("If-Condition true, therefore wp[" + C + "]("+f+") = " + assignifC1,2, recursionDepth); 
							assignResult = calculation(assignifC1);
						}else if(calculation(condition).equals("0.0")) {
							assignifC2 = assignifC2.substring(0,assignifC2.length()-1);
							mainController.output("Found possible if-assignment optimization!",2,recursionDepth); 
							mainController.output("If-Condition false, therefore wp[" + C + "]("+f+") = " + assignifC2,2, recursionDepth); 
							assignResult = calculation(assignifC2);
						}
					}
					mainController.output("Result: " + "wp[" + C + "]("+f+") = " + assignResult, 2, recursionDepth);
					result = calculation(assignResult);
				}
			}	
		}
		wpCache.put(C+" ("+f+")", result);
		mainController.output("Put into Cache: "+ C+"("+f+")" + " " + result,2,recursionDepth); 
		return result;	
	}

	/*
	 * Helper Function that sets all occurrences of a variable in C to null in currentSigma.
	 * Is used for sigmaForwarding.
	 */
	public State setSigmaValuesNull(State currentSigma, String C) {
		for(Map.Entry<String, String> entry : currentSigma.getContentMap().entrySet()) {
			if(C.contains(entry.getKey()+"=")) {
				entry.setValue(null);
			}
		}
		return currentSigma;
	}
	
	/*
	 * Helper Function that replaces occurrences of a variable from currentSigma in input.
	 * Is used for sigmaForwarding.
	 */
	public String replaceStringFromSigma(State currentSigma, String input) {
		for(Map.Entry<String, String> entry : currentSigma.getContentMap().entrySet()) {
			if(entry.getValue()!=null) {
				input = input.replace(entry.getKey(), entry.getValue());
			}
		}
		return input;
	}
	
	/*
	 * Function that allows for optimizing the wp-transformer by pre-parsing the inputted program C.
	 * The algorithm is looking for concrete variable assignments of which we know their value
	 * at given points, like for example in while or if conditions.
	 */
	public String sigmaForwarding(String C, State currentSigma) {
		String result="";
		String C1 = getSequentialCut(C);
		System.out.println("C1: " + C1);
		if(C1.startsWith("min{")) {
			//demonic choice process
			currentSigma = setSigmaValuesNull(currentSigma, C1);
			result += C1+";";	
		}
		
		else if(C1.startsWith("if") && !C1.startsWith("iff")) {
			//conditional process
			String condition = getInsideBracket(C1.substring(C1.indexOf("{")+1));
			String ifC1 = C1.substring(condition.length()+4);
			ifC1 = getInsideBracket(ifC1.substring(ifC1.indexOf("{")+1));
			String ifC2 = C.substring(C.indexOf(ifC1));
			ifC2 = getInsideBracket(ifC2.substring(ifC2.indexOf("{")+1));
			condition = replaceStringFromSigma(currentSigma, condition);
			if(calculation(condition).equals("1.0")) {
				result += ifC1+";";
			}else if(calculation(condition).equals("0.0")) {
				result += ifC2+";";
			}else {
				currentSigma = setSigmaValuesNull(currentSigma, C1);
				result += C1+";";
			}
		}
		
		else if(C1.startsWith("{")){
			//probability process
			currentSigma = setSigmaValuesNull(currentSigma, C1);
			result += C1+";";
		}
		else if(C1.startsWith("while")){
			//while process
			String condition = C1.substring(C1.indexOf("(")+1,C1.indexOf("{")-1);
			String whileC = C1.substring(condition.length());
			whileC = getInsideBracket(whileC.substring(whileC.indexOf("{")+1));
			condition = replaceStringFromSigma(currentSigma, condition);
			if(!calculation(condition).equals("0.0")) {
				currentSigma = setSigmaValuesNull(currentSigma, C1);
				result += C1+";";
			}			
		}else {
			//variable assignments
			if(!C1.startsWith("skip")){ 
				String indexC = C1.substring(0,1);
				String valueC = C1.substring(C1.indexOf("=")+1);
				valueC = replaceStringFromSigma(currentSigma, valueC);
				valueC = calculation(valueC);
				if(NumberUtils.isCreatable(valueC)) {
					currentSigma.put(indexC, valueC);
					result += indexC+"="+valueC+";";
				}else {
					currentSigma.put(indexC, null);
					result += C1+";";
				}	
			}
		}
		if(!C1.equals(C)) {
			String C2 = C.substring(C1.length()+1);
			result += sigmaForwarding(C2, currentSigma);
		}
		return result;
	}
	
	
	/*
	 * Function that takes a mathematical term (term) as input and tries to calculate a concrete result from it using the MathParser component.
	 * If no concrete result can be calculated (e.g. if there are still unresolved variables), the function returns the term as it is. 
	 */
	public String calculation(String term) {
		Function restrictValue = new Function("r", "min(max(0,x),"+restriction+")", "x");
		Expression e = new Expression(term,restrictValue);
		Double result = e.calculate();
		if(!result.isNaN()) {
			return Double.toString(result);
		}else {
			return term;
		}
	}
	
	/*
	 * Function that calculates the direct (up until iterationCount, open sigma) approach of a fixpoint iteration for while loops.
	 * It takes the while condition (condition), the program (C) and the post-expectation (f) as input.
	 * The output is a term that represents the fixpoint of the given input.
	 */
	public String directFixpointIteration(String condition, String C, String f, int recursionDepth) {
		mainController.output("Direct Fixpoint Iteration start. ", 2, recursionDepth);
		String result = "0"; //X^0 initialization
		for(int i=0; i<iterationCount; i++) {
			//TODO increase lines for each iteration to see depth on output
			mainController.output("-----------------------------------",2,recursionDepth);
			mainController.output("Iteration " + (i+1) + "\n",2,recursionDepth);
			String X = wp(C, result,recursionDepth);
			result = "if("+condition+","+X+","+f+")";
		}
		mainController.output("-----------------------------------",2,recursionDepth);
		mainController.output("Finished fixpoint iteration." + "\n" ,2,recursionDepth);


		return result;
	}
	
	/* 
	 * Function that calculates the default approach of a fixpoint iteration for while loops.
	 * It takes the while condition (condition), the program (C) and the post-expectation (f) as input.
	 * The output is an iff term that represents the fixpoint of the given input.
	 */
	public String fixpointIteration(String condition, String C, String f, int recursionDepth) {
		mainController.output("Default fixpoint iteration start. ", 2, recursionDepth);
		boolean loopCondition = true;
		String result = "0"; //X^0 initialization
		Fixpoint previousFixpoint = new Fixpoint();
		int i = 0;
		while(loopCondition){
			mainController.output("-----------------------------------",2,recursionDepth);
			mainController.output("Iteration " + (i+1) + "\n",2,recursionDepth);
			String X = wp(C, result,recursionDepth);
			result = "if("+condition+","+X+","+f+")";
			Fixpoint currentFixpoint = convertFixpoint(result);
			if(previousFixpoint.getContentString().equals(currentFixpoint.getContentString())) {
				loopCondition = false;
			}else {
				previousFixpoint.setContentString(currentFixpoint.getContentString());
			}
			result = currentFixpoint.getContentString();
			i++;
		}
		return result;
	}
	
	/*
	 * Function that calculates the all-sigma approach of a fixpoint iteration for while loops.
	 * It takes the while condition (condition), the program (C) and the post-expectation (f) as input.
	 * The output is an iff term that represents the least fixpoint of the given input.
	 */
	public String allSigmaFixpointIteration(String condition, String C, String f, int recursionDepth) {		
		mainController.output("All-Sigma Fixpoint Iteration start. ", 2, recursionDepth);
		Fixpoint leastFixpoint = new Fixpoint();
		for(State sigma : allSigma) {
			double sigmaResult = 0.0;
			double previousResult = 0.0;
			String caseF = "0"; //X_0 initialization
			String identifier = "";
			String sigmaCondition = condition;
			for(Map.Entry<String, String> entry : sigma.getContentMap().entrySet()) {
				identifier += "&("+entry.getKey()+"="+entry.getValue()+")"; //creates identifier based on variables and values
				sigmaCondition = sigmaCondition.replace(entry.getKey(), entry.getValue());
			}	
			identifier = identifier.replaceFirst("&","");
			mainController.output("***********************************",2,recursionDepth);
			mainController.output("Current program state: " + identifier ,2,recursionDepth);
			mainController.output("***********************************",2,recursionDepth);
			Expression e = new Expression(sigmaCondition);
			if(e.calculate() == 0.0) {
				sigmaResult = calculateSigma(f,sigma);
				mainController.output("Skip iteration since loop condition is wrong." , 2,recursionDepth);
			}else {
				for(int i=0; true; i++) {
					mainController.output("Iteration " + (i+1) + "\n",2,recursionDepth);
					String X = wp(C, caseF,recursionDepth);
					caseF = "if("+condition+","+X+","+f+")";
					sigmaResult = calculateSigma(caseF,sigma);
					mainController.output("Iteration result: " + sigmaResult ,2,recursionDepth);

					//checks if the distance between the iterations has reached the delta threshold and stops the iteration if it is the case
					if(i > iterationCount) {
						if(sigmaResult-previousResult < iterationDelta) {
							mainController.output("Stop iteration as the threshold has been reached.",2,recursionDepth);
							break;
						}else {	
							previousResult = sigmaResult;
						}
					}
					mainController.output("-----------------------------------",2,recursionDepth);
				}
			}
			mainController.output("Finished fixpoint iteration.",2,recursionDepth);
			double roundResult = Math.round(sigmaResult * 100.0) / 100.0;
			mainController.output("Fixpoint iteration result: " + "wp[" + C + "]("+f+") with "+ identifier +" = " + roundResult + "\n",2,recursionDepth);
			leastFixpoint.addContentFromMap(identifier, Double.toString(roundResult));	
			
		}
		return leastFixpoint.setStringFromMap();
	}
	
	/*
	 * Evaluates a given fixpoint / witness based on the "Upside-Down" Theory and checks whether it is the least possible fixpoint already or 
	 * if there is still room to improve it.
	 * It takes a program while loop (currentWhile), a witness (witness), the threshold (delta), the current iteration (interationCount), and 
	 * a set of variable assignments Y' (sigmaSet) as input.
	 */
	public LinkedHashSet<String> evaluateFixpoint(String currentWhile, String witness, String delta, int iterationCount, LinkedHashSet<String> sigmaSet) {
		
		Fixpoint X = new Fixpoint(witness);
		Fixpoint phihashX = calculatePhiHash(X, currentWhile);
		Fixpoint Xslash = new Fixpoint();
		Fixpoint phihashXslash = new Fixpoint();

		//fills the initial sigmaSet (Y') if the iteration is in its first loop
		if(iterationCount == 1) {
			for(Map.Entry<String, String> entry : X.getContentMap().entrySet()) {
				if(!entry.getValue().equals("0.0")) {
					sigmaSet.add(entry.getKey());
				}
			}
		}
		
		//save current set for iteration comparison
		LinkedHashSet<String> previousSigmaSet = new LinkedHashSet<String>();
		for(String copiedSigma : sigmaSet) {
			previousSigmaSet.add(copiedSigma);
		}

		
		//checks whether the witness is a fixpoint or not
		/*System.out.println("LinkedHashMap to string: " +X.getContentString());
		System.out.println("LinkedHashMap to string: " +phihashX.getContentString());

		if(!X.getContentString().equals(phihashX.getContentString())) {
			mainController.output("\n" + "-----------------------------------");
			mainController.output("\n" + "The inputted witness is not a fixpoint! Cannot evaluate non-fixpoints!");
			return sigmaSet;
		} */
		
		for(Map.Entry<String, String> entry : X.getContentMap().entrySet()) {
			String XslashValue = "";
			if(!sigmaSet.contains(entry.getKey())) {
				XslashValue = entry.getValue();
			}else {
				XslashValue = calculation("r("+entry.getValue()+"-"+delta+")");		
			}
			Xslash.addContentFromMap(entry.getKey(),XslashValue);
		}
		Xslash.setStringFromMap();
		phihashXslash = calculatePhiHash(Xslash, currentWhile);
		
		//removes variable assignments from the sigmaSet that do not fulfill the delta threshold
		for(Map.Entry<String, String> entry : phihashX.getContentMap().entrySet()) {
			double entryResult = Double.parseDouble(calculation(entry.getValue() +"-"+ phihashXslash.getContentMap().get(entry.getKey()) +">=" +delta));
			if(entryResult == 0.0) {
				sigmaSet.remove(entry.getKey());
			}
		}
		
		//outputting the result
		mainController.output("\n" + "-----------------------------------",1);
		mainController.output("\n" + "Iteration " + iterationCount,1);
		mainController.output("\n" + "X: " + X.getContentMap(),1);
		mainController.output( "X': " + Xslash.getContentMap(),1);
		mainController.output( "Phi-Hash (X): " + phihashX.getContentMap(),1);
		mainController.output( "Phi-Hash (X'): " + phihashXslash.getContentMap(),1);

		if(sigmaSet.isEmpty()) {
			mainController.output("\n" + "The hash-function's result is an empty set. This means the witness is already the least fixpoint." + "\n",1);
		}else {
			mainController.output("\n" + "The hash-function's result is not an empty set. This means the witness is above the least fixpoint." ,1);
			mainController.output("Following states are still in the result set: " ,1);
			for(String state : sigmaSet) {
				mainController.output(state + ", ",1);
			}
			if(!previousSigmaSet.toString().equals(sigmaSet.toString())) {
				mainController.output("therefore continuing iteration." + "\n",1);
				sigmaSet = evaluateFixpoint(currentWhile, witness, delta, (iterationCount+1), sigmaSet);
			}else {
				mainController.output("but since no change in the set has been detected, the iteration stops now." + "\n",1);
			}
		}
		return sigmaSet;
	}
	
	/*
	 * Function that represents the hash function from the "Upside-Down" theory applied to the Phi function from the wp-transformer. 
	 * It takes a fixpoint as map (input), the analyzed while loop (currentWhile) and the fixpoint in the mathematical iff-term format (fixpointIf) as input
	 * and outputs a new function as a map.
	 */
	private Fixpoint calculatePhiHash(Fixpoint input, String currentWhile){
		String currentC = currentWhile.split(" ")[0];
		String currentF = currentWhile.split(" ")[1];
		currentF = currentF.substring(1,currentF.length()-1);
		String condition = currentC.substring(currentC.indexOf("(")+1,currentC.indexOf("{")-1);
		String whileC = currentC.substring(condition.length());
		whileC = getInsideBracket(whileC.substring(whileC.indexOf("{")+1));
		
		Fixpoint result = new Fixpoint();
		for(Map.Entry<String, String> entry : input.getContentMap().entrySet()) {
			String entryF = currentF;
			String entryCondition = condition;
			String concreteSigma = entry.getKey().replace("&", ";");;
			concreteSigma = concreteSigma.replace("(", "");
			concreteSigma = concreteSigma.replace(")", "");
			String[] entryVariables = concreteSigma.split(";");
			for(int i = 0; i < entryVariables.length; i++) {
				String index = entryVariables[i].substring(0,1);
				String cut = entryVariables[i].substring(entryVariables[i].indexOf("=")+1);
				entryF = entryF.replace(index, cut);
				entryCondition = entryCondition.replace(index, cut);
			}
			if(calculation(entryCondition).equals("0.0")) {
				result.addContentFromMap(entry.getKey(), calculation(entryF));
			}else {
				result.addContentFromMap(entry.getKey(), calculation(wp(concreteSigma+";"+whileC,input.getContentString(),0)));
			}
		}
		result.setStringFromMap();
		return result;
	}

	/*
	 * Function that converts a general fixpoint in if-clause notation including the post-expectation (currentWhileTerm) 
	 * to an iff-clause notation. This is done by calculating the result for every possible program state on the given fixpoint.
	 * usedVars is used to create the cartesian product of all possible variable combinations (fillAllSigma).
	 */
	public Fixpoint convertFixpoint(String fixpointIfFormat){
		Fixpoint leastFixpoint = new Fixpoint();
		for(State sigma : allSigma) {
			String identifier = "";
			Double sigmaResult = calculateSigma(fixpointIfFormat, sigma);
			System.out.println("This is the value:" + sigmaResult);
			for(Map.Entry<String, String> entry : sigma.getContentMap().entrySet()) {
				identifier += "&("+entry.getKey()+"="+entry.getValue()+")"; //creates identifier based on variables and values
				System.out.println("This is the id:" + identifier);
			}	
			identifier = identifier.replaceFirst("&","");
			double roundResult = Math.round(sigmaResult * 100.0) / 100.0;
			leastFixpoint.addContentFromMap(identifier, Double.toString(roundResult));	
		}
		leastFixpoint.setStringFromMap();
		return leastFixpoint;
	}
	
	/*
	 * Function that calculates a concrete mathematical result for a variable term with given variable assignments.
	 * It takes a post-expectation during the fixpoint-iteration (f) and a concrete variable assignment (sigma) as input and
	 * outputs a numerical value or throws an exception in case it cannot be calculated.
	 */
	public Double calculateSigma(String f, State sigma) {
		System.out.println("Concrete Sigma f : " + f);
		for(Map.Entry<String, String> entry : sigma.getContentMap().entrySet()) {
			f = f.replace(entry.getKey(), entry.getValue());
		}
		System.out.println("Concrete Sigma f after replace : " + f);

		Function restrictValue = new Function("r", "min(max(0,x),"+restriction+")", "x");
		Expression e = new Expression(f,restrictValue);

		Double result = e.calculate();
		System.out.println("Concrete Sigma Result: " + result);

		if(result.isNaN()) {
			//throw exception and break + log
			mainController.output("\n" + "There are unknown variables in the formula!",1);
			return null;
		}else {
			return result;
		}
	}
	
	/*
	 * Function that fills a data structure with all possibilities of variable and value combinations.
	 * It takes a string of all used variables (varInput) as input and outputs a List of maps that represent all possible
	 * variable/value combinations.
	 */
	public ArrayList<State> fillAllSigma(String varInput) {
		allSigma.clear();
		
		List<List<Integer>> preCartesianValues = new ArrayList<List<Integer>>(); 
		
		List<Integer> restrictedList = new ArrayList<Integer>();
		for (int i = 0 ; i < restriction+1; i++) {
			restrictedList.add(i);
		}
		
		for(int i = 0 ; i < varInput.length() ; i++) {	
			preCartesianValues.add(restrictedList);
		}

		List<List<Integer>> postCartesianValues = Lists.cartesianProduct(preCartesianValues);

		for(int i = 0 ; i < postCartesianValues.size(); i++){
			State tempState = new State(); 
			for(int j = 0 ; j < postCartesianValues.get(i).size(); j++){
			tempState.put(String.valueOf(varInput.charAt(j)), postCartesianValues.get(i).get(j).toString());
			}
			allSigma.add(tempState);
		}
		return allSigma;
	}		
	
	/*
	 * parser assistance functions
	 */
	
	public String getInsideBracket(String C) {
		int bracketCount = 1;
		String result = "";
		for(int i = 0; i < C.length(); i++) {
			char character = C.charAt(i);
			if(character == '{') {
				bracketCount++;
			}
			if(character == '}') {
				bracketCount--;
			}
			if(bracketCount != 0) {
				result = result + character;
			}else {
				break;
			}
		}
		return result;
	}
	
	
	public int getIffCount(String term) {
		int bracketCount = 1;
		int commaCount = 0;
		for(int i = 0; i < term.length(); i++) {
			char character = term.charAt(i);
			if(character == '(') {
				bracketCount++;
			}
			if(character == ')') {
				bracketCount--;
			}
			if(character == ',') {
				commaCount++;
			}
			if(bracketCount == 0) {
				break;
			}
			
		}
		return commaCount;	
	}
	
	public String getInsideIf(String C) {
		int commaCount = 1;
		String result = "";
		for(int i = 0; i < C.length(); i++) {
			char character = C.charAt(i);
			if(character == 'i') {
				if (C.charAt(i+2) != 'f') {
					//if inside if case
					commaCount += 2;	
				}else {
					//iff inside if case
					commaCount += getIffCount(C.substring(i+4));
				}
			}
			//min inside if case
			if(character == 'm') {
				commaCount += 1;
			}
			if(character =='n') {
				commaCount -= 2;
			}
			if(character == ',') {
				commaCount--;
			}
			if(commaCount != 0) {
				result = result + character;
			}else {
				break;
			}
		}
		return result;
	}
	
	public String getSequentialCut(String C) {
		int bracketCount = 0;
		String result = "";
		for(int i = 0; i < C.length(); i++) {
			char character = C.charAt(i);
			if(character == '{') {
				bracketCount++;
			}
			if(character == '}') {
				bracketCount--;
			}
			if(character == ';' && bracketCount == 0) {
				break;
			}else {
				result = result + character;
			}
		}
		return result;
	}
	
	/*
	 * wp cache methods
	 */
	
	/*
	 * clears the wpCache in the model
	 */
	public void clearWPCache() {
		wpCache.clear();
		mainController.output("\n" + "Cache cleared.",1);

	}
	
	/*
	 * writes a fixpointCache to file
	 */
	public void saveWPCache() {
		new File("Cache").mkdir(); 
	    //boolean res = directory.mkdir();
		FileOutputStream fout;
		try {
			fout = new FileOutputStream("Cache/wpCache");
			try (ObjectOutputStream oos = new ObjectOutputStream(fout)) {
				oos.writeObject(wpCache);
				mainController.output("\n" + "Cache saved.",1);
			}
		} catch (IOException e) {
			mainController.output("\n" + "WARNING: failed to save cache.",1);
			e.printStackTrace();
		}	
	}
	
	/*
	 * reads a saved fixpointCache from file
	 */
	@SuppressWarnings("unchecked")
	public void loadWPCache() {
		FileInputStream fin;
		try {
			fin = new FileInputStream("Cache/wpCache");
			try (ObjectInputStream ois = new ObjectInputStream(fin)) {
				LinkedHashMap<String, String> fileCache = (LinkedHashMap<String, String>) ois.readObject();
				wpCache = fileCache;
				mainController.output("\n" + "Cache loaded.",1);
			}
		} catch (IOException | ClassNotFoundException e) {
			mainController.output("\n" + "WARNING: failed to load cache." ,1);
		}
	}


	/*
	 * Deprecated function to calculate restrictions on variables without MathParser
	 */
	public String truncate(String input) {
		String result ="";
		for(int i = 0; i < input.length(); i++) {
			char character = input.charAt(i);
			if(character == '#') {
				String inside = getInsideBracket(input.substring(i+2));
				String insideCalc = calculation(inside);
				if(NumberUtils.isCreatable(insideCalc)) {
					double insideValue = Double.parseDouble(insideCalc);
					if(insideValue <= 0) {
						input = input.replace("#{"+inside+"}", "0");
						i--;
					}else {
						String truncatedValue = Double.toString(NumberUtils.min(insideValue,restriction));							
						input = input.replace("#{"+inside+"}", truncatedValue);
						i--;
					}
				}else {
					if(inside.contains("#")) {
						String subterm = truncate(inside);
						String replacedInput = input.replace("#{"+inside+"}", "#{"+subterm+"}");
						if(!replacedInput.equals(input)) {
							input = replacedInput;
							i--;
						}else {
							result = result + "#";
						}
					}else {
						result = result + "#";
					}
				}				
			}else {
				result = result + character;
			}
		}
		return result;
  	}
	
	/*
	 * getter & setter methods
	 */
	
	public ArrayList<String> getWhileLoops() {
		return whileLoops;
	}

	public void setWhileLoops(ArrayList<String> whileLoops) {
		this.whileLoops = whileLoops;
	}
	
	public LinkedHashMap<String, String> getWPCache() {
		return wpCache;
	}
	
	public void setWPCache(LinkedHashMap<String, String> wpCache) {
		this.wpCache = wpCache;
	}
	
	public void flushWhileLoops() {
		whileLoops.clear();
	}
	
	public void setHandler(ControllerHandler controller) {
		mainController = controller;
	}
	
	public ControllerHandler getHandler() {
		return mainController;
	}

	public double getRestriction() {
		return restriction;
	}

	public void setRestriction(double restriction) {
		this.restriction = restriction;
	}

	public double getIterationCount() {
		return iterationCount;
	}

	public void setIterationCount(double iterationCount) {
		this.iterationCount = iterationCount;
	}
	
	public double getIterationDelta() {
		return iterationDelta;
	}
	
	public void setIterationDelta(double iterationDelta) {
		this.iterationDelta = iterationDelta;
	}

	public int getIterationSelection() {
		return iterationSelection;
	}

	public void setIterationSelection(int iterationSelection) {
		this.iterationSelection = iterationSelection;
	}
}
