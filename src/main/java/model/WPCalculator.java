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
private ArrayList<LinkedHashMap<String, String>> allSigma = new ArrayList<LinkedHashMap<String, String>>();
private ArrayList<String> whileLoops = new ArrayList<String>();
private LinkedHashMap<String,String> fixpointCache = new LinkedHashMap<String,String>();
private double restriction;
private double iterationCount;
private boolean allSigmaSelection;
private double iterationDelta;

	/*
	 * Main method that represents the weakest precondition transformer function (wp)
	 * Takes a program (C) and a post expectation (f) as input and recursively calculates the result of the formula wp[C](f) for any sigma (variable assignment).
	 */
	public String wp(String C, String f) {
		//sequential process
		//TODO detailed log: mainView.getResult().setText(mainView.getResult().getText() + "\n" + "wp["+C+"]("+f+")"); 
		String C1 = getSequentialCut(C);
		System.out.println("C1: " + C1);
		if(!C1.equals(C)) {
			String C2 = C.substring(C1.length()+1);
			//TODO detailed log: mainView.getResult().setText(mainView.getResult().getText() + "\n" + "Sequential process. Breaking down into: wp["+C1+"](wp["+C2+"]("+f+"))"); 
			return wp(C1,(wp(C2,f)));
		}else {
			if(C.startsWith("min{")) {
				//demonic choice process
				String demC1 = getInsideBracket(C.substring(C.indexOf("{")+1));	
				String demC2 = C.substring(C.indexOf(demC1));
				demC2 = getInsideBracket(demC2.substring(demC2.indexOf("{")+1));
				
				System.out.println("demC1= "+demC1); 
				System.out.println("demC2= "+demC2);
				String resultC1 = wp(demC1,f);
				String resultC2 = wp(demC2,f);

				//TODO detailed log: mainView.getResult().setText(mainView.getResult().getText() + "\n" + "Demonic Choice process. Breaking down into: min(" + resultC1 + "," + resultC2 + ")"); 

				return calculation("min(" + resultC1 + "," + resultC2 + ")");

			}
			
			else if(C.startsWith("if") && !C.startsWith("iff")) {
				//conditional process
				System.out.println("Enter conditional process"); 
				String condition = getInsideBracket(C.substring(C.indexOf("{")+1));
				System.out.println("Conditional: "+condition); 
				String ifC1 = C.substring(condition.length()+4);
				ifC1 = getInsideBracket(ifC1.substring(ifC1.indexOf("{")+1));
				String ifC2 = C.substring(C.indexOf(ifC1));
				ifC2 = getInsideBracket(ifC2.substring(ifC2.indexOf("{")+1));
				
				System.out.println("C1= "+ifC1); 
				System.out.println("C2= "+ifC2);
				
				//TODO detailed log: mainView.getResult().setText(mainView.getResult().getText() + "\n" + "Conditional process. Breaking down into: if("+condition+") then "+ resultC1 +" else "+ resultC2); 
				String condResult = calculation(condition);
				if(condResult.equals("1.0")) {
					String resultC1 = wp(ifC1,f);
					return calculation(resultC1);
				}
				if(condResult.equals("0.0")) {
					String resultC2 = wp(ifC2,f);
					return calculation(resultC2);
				}
				String resultC1 = wp(ifC1,f);
				String resultC2 = wp(ifC2,f);
				return calculation("if(" + condition + "," + resultC1 + "," + resultC2 + ")");

			}
			
			else if(C.startsWith("{")){
				//probability process
				System.out.println("Enter probability process"); 
				String probC1 = getInsideBracket(C.substring(C.indexOf("{")+1));

				String probC2 = C.substring(probC1.length());
				String probability = probC2.substring(probC2.indexOf("[")+1,probC2.indexOf("]"));
				probC2 = getInsideBracket(probC2.substring(probC2.indexOf("{")+1));

				System.out.println("C1= "+probC1); 
				System.out.println("C2= "+probC2);
				System.out.println("Probability:" + probability);
				Expression negProbability = new Expression ("1-"+probability);
				String resultC1 = wp(probC1,f);
				String resultC2 = wp(probC2,f);
				//TODO detailed log: mainView.getResult().setText(mainView.getResult().getText() + "\n" + "Probability process. Breaking down into: " + probability + " * " + resultC1 +" + "+ negProbability.calculate() + " * " + resultC2); 
				return calculation("(" + probability + " * "+ resultC1 +" + "+ negProbability.calculate() + " * " + resultC2+")");

			}
			else if(C.startsWith("while")){
				//while process
				System.out.println("Enter while process"); 
				String condition = C.substring(C.indexOf("(")+1,C.indexOf("{")-1);
				System.out.println("Condition: "+condition);
				String whileC = C.substring(condition.length());
				whileC = getInsideBracket(whileC.substring(whileC.indexOf("{")+1));
				System.out.println("whileC: "+whileC);
				
				if(!whileLoops.contains(C+" ("+f+")")) {
					whileLoops.add(C+" ("+f+")");
				}
				//TODO while in while cache 
				if(!fixpointCache.containsKey(C+" ("+f+")")) {
					String fixpoint="";
					if (allSigmaSelection) {		 
						fixpoint = fixpointIterationAllSigma(condition, whileC, f);
					} else {
						fixpoint = fixpointIterationIterativ(condition, whileC, f); 
					}
					fixpointCache.put(C+" ("+f+")", fixpoint);
					System.out.println("Put into Cache: "+ C+"("+f+")" + " " + fixpoint);
	
					return fixpoint;
						
				}else {
					System.out.println("Skipped because value has been found in fixpoint cache.");
					System.out.println("Cached LFP: "+ fixpointCache.get(C+" ("+f+")"));
					return fixpointCache.get(C+" ("+f+")");
				}
				
				
			}else {
				//variable assignments
				if(C.startsWith("skip")){
					System.out.println("Enter skip process"); 
					String skipResult = C.replace("skip", f);
					//TODO detailed log: mainView.getResult().setText(mainView.getResult().getText() + "\n" + "Assignment skip process." + skipResult);
					return calculation(skipResult);
				}else {
					System.out.println("Enter assignment process"); 
					String indexC = C.substring(0,1);
					String cutC = C.substring(C.indexOf("=")+1);
					String assignResult = f.replace(indexC, "r(" + cutC + ")");

					
					//if mid calculation optimization
					if(assignResult.startsWith("if") && !assignResult.startsWith("iff")) {
						System.out.println("Enter conditional process"); 
						String condition = getInsideIf(assignResult.substring(3));
						System.out.println("Conditional: "+condition); 
						String assignifC1 = assignResult.substring(condition.length()+4);	
						System.out.println("ifC1= "+assignifC1); 
						
						assignifC1 =  getInsideIf(assignifC1);
						String assignifC2 = assignResult.substring(condition.length()+4+assignifC1.length()+1);
						assignifC2 = assignifC2.substring(0,assignifC2.length()-1);
						System.out.println("assignifC1= "+assignifC1); 
						System.out.println("assignifC2= "+assignifC2);
						if(calculation(condition).equals("1.0")) {
							return calculation(assignifC1);
						}
						if(calculation(condition).equals("0.0")) {
							return calculation(assignifC2);
						}
					}
					
					//TODO detailed log: mainView.getResult().setText(mainView.getResult().getText() + "\n" + "Assignment process." + assignResult);
					return calculation(assignResult);
				}
					
					
			}
			
		}
	
	}
	
	/*
	 * Function that allows for optimizing the wp-transformer by pre-parsing the inputted program C.
	 * The algorithm is looking for concrete variable assignments of which we know their value
	 * at given points, like for example in while or if conditions.
	 */
	public String sigmaForwarding(String C,LinkedHashMap<String,String> currentSigma) {
		String result="";
		String C1 = getSequentialCut(C);
		System.out.println("C1: " + C1);
		if(C1.startsWith("min{")) {
			//demonic choice process
			for(Map.Entry<String, String> entry : currentSigma.entrySet()) {
				if(C1.contains(entry.getKey()+"=")) {
					entry.setValue(null);
				}
			}
			result += C1+";";	
		}
		
		else if(C1.startsWith("if") && !C1.startsWith("iff")) {
			//conditional process
			String condition = getInsideBracket(C1.substring(C1.indexOf("{")+1));
			String ifC1 = C1.substring(condition.length()+4);
			ifC1 = getInsideBracket(ifC1.substring(ifC1.indexOf("{")+1));
			String ifC2 = C.substring(C.indexOf(ifC1));
			ifC2 = getInsideBracket(ifC2.substring(ifC2.indexOf("{")+1));
			//check condition
			for(Map.Entry<String, String> entry : currentSigma.entrySet()) {
				if(entry.getValue()!=null) {
					condition = condition.replace(entry.getKey(), entry.getValue());
				}
			}
			if(calculation(condition).equals("1.0")) {
				result += ifC1+";";
			}else if(calculation(condition).equals("0.0")) {
				result += ifC2+";";
			}else {
				result += C1+";";
			}
		}
		
		else if(C1.startsWith("{")){
			//probability process
			result += C1+";";
			for(Map.Entry<String, String> entry : currentSigma.entrySet()) {
				if(C1.contains(entry.getKey()+"=")) {
					entry.setValue(null);
				}
			}
		}
		else if(C1.startsWith("while")){
			//while process
			String condition = C1.substring(C1.indexOf("(")+1,C1.indexOf("{")-1);
			String whileC = C1.substring(condition.length());
			whileC = getInsideBracket(whileC.substring(whileC.indexOf("{")+1));
			for(Map.Entry<String, String> entry : currentSigma.entrySet()) {
				if(entry.getValue()!=null) {
					condition = condition.replace(entry.getKey(), entry.getValue());
				}
			}
			if(calculation(condition).equals("0.0")) {
				result += "skip;";
			} else {
				for(Map.Entry<String, String> entry : currentSigma.entrySet()) {
					if(C1.contains(entry.getKey()+"=")) {
						entry.setValue(null);
					}
				}
				result += C1+";";
			}			
		}else {
			//variable assignments
			if(C1.startsWith("skip")){
				result += C1+";";

			}else {
				String indexC = C1.substring(0,1);
				String cutC = C1.substring(C.indexOf("=")+1);
				for(Map.Entry<String, String> entry : currentSigma.entrySet()) {
					if(entry.getValue()!=null) {
						cutC = cutC.replace(entry.getKey(), entry.getValue());
					}
				}
				cutC = calculation(cutC);
				if(NumberUtils.isCreatable(cutC)) {
					currentSigma.put(indexC, cutC);
					result += indexC+"="+cutC+";";
				}else {
					currentSigma.put(indexC, null);
					result += C1+";";
				}	
			}
		}
		if(!C1.equals(C)) {
			String C2 = C.substring(C1.length()+1);
			result += sigmaForwarding(C2,currentSigma);
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
		System.out.println("Expression:" + term);
		Double result = e.calculate();
		if(!result.isNaN()) {
			System.out.println("Calculation Result: " + result);
			return Double.toString(result);
		}else {
			System.out.println("Calculation Result: " + term);
			return term;
		}
	}
	
	/*
	 * Function that calculates the iterative (up until count) approach of a fixpoint iteration for while loops.
	 * It takes the while condition (condition), the program (C) and the post-expectation (f) as input.
	 * The output is a term that represents the fixpoint of the given input.
	 */
	public String fixpointIterationIterativ(String condition, String C, String f) {
		String caseF = "0"; //X^0 initialization
		for(int i=0; i<iterationCount; i++) {
			String X = wp(C, caseF);
			caseF = "if("+condition+","+X+","+f+")";
		}
		return caseF;
	}
	
	/*
	 * Function that calculates the all-sigma approach of a fixpoint iteration for while loops.
	 * It takes the while condition (condition), the program (C) and the post-expectation (f) as input.
	 * The output is a term that represents the fixpoint of the given input.
	 */
	public String fixpointIterationAllSigma(String condition, String C, String f) {
		LinkedHashMap<String,String> fixpoint = new LinkedHashMap<String, String>();
		
		for(LinkedHashMap<String,String> sigma : allSigma) {
			double sigmaResult = 0.0;
			double previousResult = 0.0;
			String caseF = "0"; //X_0 initialization
			String identifier = "";
			String sigmaCondition = condition;
			for(Map.Entry<String, String> entry : sigma.entrySet()) {
				identifier += "&("+entry.getKey()+"="+entry.getValue()+")"; //creates identifier based on variables and values
				sigmaCondition = sigmaCondition.replace(entry.getKey(), entry.getValue());
			}	
			identifier = identifier.replaceFirst("&","");
			Expression e = new Expression(sigmaCondition);
			if(e.calculate() == 0.0) {
				sigmaResult = calculateConcreteSigma(f,sigma);
			}else {
				for(int i=0; i<iterationCount; i++) {
					String X = wp(C, caseF);
					caseF = "if("+condition+","+X+","+f+")";
					//TODO future improvement: directly input sigma through assignment = f.replace x with sigma x and keep dependency somehow
					sigmaResult = calculateConcreteSigma(caseF,sigma);
					
					//checks if the distance between the iterations has reached the delta threshold and stops the iteration if it is the case
					if(i > 2) {
						if(sigmaResult-previousResult < iterationDelta) {
							break;
						}else {
							previousResult = sigmaResult;
						}
					}
				}
			}
			double roundResult = Math.round(sigmaResult * 100.0) / 100.0;
			fixpoint.put(identifier, Double.toString(roundResult));	
		}
		return fixpointIfConversion(fixpoint);
	}
	
	/*
	 * evaluates a given fixpoint / witness based on the "Upside-Down" Theory and checks whether it is the least possible fixpoint already or 
	 * if there is still room to improve it.
	 * It takes a program while loop (currentWhile), a witness (fixpoint), the threshold (delta), the current iteration (interationCount), and 
	 * a set of variable assignments Y' (sigmaSet) as input.
	 */
	public LinkedHashSet<String> evaluateFixpoint(String currentWhile, String fixpoint, String delta, int iterationCount, LinkedHashSet<String> sigmaSet) {
		
		LinkedHashMap<String,String> Xslash = new LinkedHashMap<String,String>();
		LinkedHashMap<String,String> phihashX = new LinkedHashMap<String,String>();
		LinkedHashMap<String,String> phihashXslash = new LinkedHashMap<String,String>();
		LinkedHashMap<String, String> X = fixpointToMap(fixpoint);

		//fills the initial sigmaSet (Y') if the iteration is in its first loop
		if(iterationCount == 1) {
			for(Map.Entry<String, String> entry : X.entrySet()) {
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

		phihashX = calculatePhiHash(X, currentWhile, fixpoint);
		
		//checks whether the witness is a fixpoint or not
		System.out.println("LinkedHashMap to string: " +X.toString());
		System.out.println("LinkedHashMap to string: " +phihashX.toString());

		if(!X.toString().equals(phihashX.toString())) {
			mainController.output("\n\n" + "-----------------------------------");
			mainController.output("\n\n" + "The inputted witness is not a fixpoint! Cannot evaluate non-fixpoints!");
			return sigmaSet;
		} 
		
		for(Map.Entry<String, String> entry : X.entrySet()) {
			String XslashValue = "";
			if(!sigmaSet.contains(entry.getKey())) {
				XslashValue = entry.getValue();
			}else {
				XslashValue = calculation("r("+entry.getValue()+"-"+delta+")");		
			}
			Xslash.put(entry.getKey(),XslashValue);
		}
		
		phihashXslash = calculatePhiHash(Xslash, currentWhile, fixpointIfConversion(Xslash));
		
		//removes variable assignments from the sigmaSet that do not fulfill the delta threshold
		for(Map.Entry<String, String> entry : X.entrySet()) {
			double entryResult = Double.parseDouble(calculation(phihashX.get(entry.getKey())+"-"+ phihashXslash.get(entry.getKey()) +">=" +delta));
			if(entryResult == 0.0) {
				sigmaSet.remove(entry.getKey());
			}
		}
		
		//outputting the result
		mainController.output("\n\n" + "-----------------------------------");
		mainController.output("\n\n" + "Hash-Function Results: (Iteration " + iterationCount + ")");
		mainController.output("\n\n" + "X: " + X);
		mainController.output("\n" + "X': " + Xslash);
		mainController.output("\n" + "Phi-Hash (X): " + phihashX);
		mainController.output("\n" + "Phi-Hash (X'): " + phihashXslash);

		if(sigmaSet.isEmpty()) {
			mainController.output("\n\n" + "The hash-function's result is an empty set. This means the witness is already the least fixpoint." );
		}else {
			mainController.output("\n\n" + "The hash-function's result is not an empty set. This means the witness is above the least fixpoint." );
			mainController.output("\n" + "Following states are still in the result set: " );
			for(String state : sigmaSet) {
				mainController.output(state + ",");
			}
			if(!previousSigmaSet.toString().equals(sigmaSet.toString())) {
				mainController.output(" therefore continuing iteration.");
				sigmaSet = evaluateFixpoint(currentWhile, fixpoint, delta, (iterationCount+1), sigmaSet);
			}else {
				mainController.output(" but since no change in the set has been detected, the iteration stops now.");
			}
		}
		return sigmaSet;
	}
	
	/*
	 * Function that represents hash function from the "Upside-Down" theory applied to the Phi function from the wp-transformer. 
	 * It takes a fixpoint as map (input), the analyzed while loop (currentWhile) and the fixpoint in the mathematical iff-term format (fixpointIf) as input
	 * and outputs a new function as a map.
	 */
	private LinkedHashMap<String,String> calculatePhiHash(LinkedHashMap<String,String> input, String currentWhile, String fixpointIf){
		
		String currentC = currentWhile.split(" ")[0];
		String currentF = currentWhile.split(" ")[1];
		currentF = currentF.substring(1,currentF.length()-1);
		String condition = currentC.substring(currentC.indexOf("(")+1,currentC.indexOf("{")-1);
		String whileC = currentC.substring(condition.length());
		whileC = getInsideBracket(whileC.substring(whileC.indexOf("{")+1));
		
		LinkedHashMap<String,String> result = new LinkedHashMap<String,String>();
		for(Map.Entry<String, String> entry : input.entrySet()) {
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
				result.put(entry.getKey(), calculation(entryF));
			}else {
				result.put(entry.getKey(), calculation(wp(concreteSigma+";"+whileC,fixpointIf)));
			}
		}
		return result;
	}
	
	/*
	 * Function that transforms a fixpoint in mathematical iff-term format into a map.
	 */
	public LinkedHashMap<String,String> fixpointToMap(String fixpoint) {
		LinkedHashMap<String,String> convFixpoint = new LinkedHashMap<String,String>();
		fixpoint = fixpoint.substring(4,fixpoint.length()-1);
		fixpoint += ";";
		System.out.println("Fixpoint: "+fixpoint);

		while(fixpoint.length()>0) {
			String identifier = fixpoint.substring(0,fixpoint.indexOf(","));
			System.out.println("Id: "+identifier);
			fixpoint = fixpoint.substring(identifier.length()+1);
			String value = fixpoint.substring(0,fixpoint.indexOf(";"));
			System.out.println("Value: "+value);
			convFixpoint.put(identifier, value);
			fixpoint = fixpoint.substring(value.length()+1);
		}
		
		return convFixpoint;
	}
	
	/*
	 * Function that transforms a fixpoint in map format into a mathematical iff-term format.
	 */
	public String fixpointIfConversion(LinkedHashMap<String,String> fixpoint) {
		String result = "iff(";
		for(Map.Entry<String, String> entry : fixpoint.entrySet()) {
			result += ";" + entry.getKey()+","+entry.getValue();
		}
		result = result.replaceFirst(";", "");
		result += ")";
		return result;
	}
	
	//TODO add other possibility of calculating concrete sigma: wp("sigma=x=1;c=1";caseF,null); = Xi
	/*
	 * Function that calculates a concrete mathematical result for a variable term with given variable assignments.
	 * It takes a postexpectation during the fixpoint-iteration (f) and a concrete variable assignment (sigma) as input and
	 * outputs a numerical value or throws an exception in case it cannot be calculated.
	 */
	public Double calculateConcreteSigma(String f, LinkedHashMap<String,String> sigma) {
		System.out.println("Concrete Sigma f : " + f);
		for(Map.Entry<String, String> entry : sigma.entrySet()) {
			f = f.replace(entry.getKey(), entry.getValue());
		}
		System.out.println("Concrete Sigma f after replace : " + f);

		Function restrictValue = new Function("r", "min(max(0,x),"+restriction+")", "x");
		Expression e = new Expression(f,restrictValue);

		Double result = e.calculate();
		System.out.println("Result: " + result);

		if(result.isNaN()) {
			//throw exception and break + log
			System.out.println("There are unknown variables in the formula!");
			mainController.output("\n\n" + "There are unknown variables in the formula!");
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
	public ArrayList<LinkedHashMap<String,String>> fillAllSigma(String varInput) {
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
			LinkedHashMap<String, String> tempMap = new LinkedHashMap<String,String>();
			for(int j = 0 ; j < postCartesianValues.get(i).size(); j++){
			tempMap.put(String.valueOf(varInput.charAt(j)), postCartesianValues.get(i).get(j).toString());
			}
			allSigma.add(tempMap);
		}
		System.out.println(allSigma);
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
	 * fixpoint cache methods
	 */
	
	public void clearFixpointCache() {
		fixpointCache.clear();
		mainController.output("\n\n" + "Cache cleared.");

	}
	
	/*
	 * writes a fixpointCache to file
	 */
	public void saveFixpointCache() {
		new File("Cache").mkdir(); 
	    //boolean res = directory.mkdir();
		FileOutputStream fout;
		try {
			fout = new FileOutputStream("Cache/fixpointCache");
			try (ObjectOutputStream oos = new ObjectOutputStream(fout)) {
				oos.writeObject(fixpointCache);
				mainController.output("\n\n" + "Cache saved.");
			}
		} catch (IOException e) {
			mainController.output("\n\n" + "WARNING: failed to save Cache.");
			e.printStackTrace();
		}	
	}
	
	/*
	 * reads a saved fixpointCache from file
	 */
	@SuppressWarnings("unchecked")
	public void loadFixpointCache() {
		FileInputStream fin;
		try {
			fin = new FileInputStream("Cache/fixpointCache");
			try (ObjectInputStream ois = new ObjectInputStream(fin)) {
				LinkedHashMap<String, String> fileCache = (LinkedHashMap<String, String>) ois.readObject();
				fixpointCache = fileCache;
				mainController.output("\n\n" + "Cache loaded.");
			}
		} catch (IOException | ClassNotFoundException e) {
			mainController.output("\n\n" + "WARNING: failed to load Cache.");
			e.printStackTrace();
		}
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
	
	public LinkedHashMap<String, String> getFixpointCache() {
		return fixpointCache;
	}
	
	public void setFixpointCache(LinkedHashMap<String, String> fixpointCache) {
		this.fixpointCache = fixpointCache;
	}
	
	public void flushWhileLoops() {
		whileLoops.clear();
	}
	
	public void setHandler(ControllerHandler controller) {
		mainController = controller;
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

	public boolean isAllSigmaSelection() {
		return allSigmaSelection;
	}

	public void setAllSigmaSelection(boolean allSigmaSelection) {
		this.allSigmaSelection = allSigmaSelection;
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
	
}
