package prototype;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.Function;

import com.google.common.collect.Lists;
//Parser Documentation: https://github.com/mariuszgromada/MathParser.org-mXparser

public class WPCalculator {

private LinkedHashMap<String, String> variables = new LinkedHashMap<String,String>();
private WPCalculatorView mainView;
private ArrayList<LinkedHashMap<String, String>> allSigma = new ArrayList<LinkedHashMap<String, String>>();

	
	public String wp(String C, String f) {
		//TODO check which calculations can be skipped for runtime improvement
		
		C = C.replace(" ", "");
		mainView.getResult().setText(mainView.getResult().getText() + "\n" + "wp["+C+"]("+f+")");
		System.out.println("C "+ C);
		String C1 = getSequentialCut(C);
		System.out.println("C1: " + C1);
		if(!C1.equals(C)) {
			System.out.println("Enter sequential process"); 
			String C2 = C.substring(C1.length()+1);
			System.out.println("C2: " + C2);
			mainView.getResult().setText(mainView.getResult().getText() + "\n" + "Sequential process. Breaking down into: wp["+C1+"](wp["+C2+"]("+f+"))"); 
			return wp(C1,(wp(C2,f)));
		}else {
			if(C.startsWith("min{")) {
				//demonic choice process
				System.out.println("Enter demonic choice process"); 
				String demC1 = getInsideBracket(C.substring(C.indexOf("{")+1));	
				String demC2 = C.substring(C.indexOf(demC1));
				demC2 = getInsideBracket(demC2.substring(demC2.indexOf("{")+1));
				
				System.out.println("demC1= "+demC1); 
				System.out.println("demC2= "+demC2);
				String resultC1 = wp(demC1,f);
				String resultC2 = wp(demC2,f);

				mainView.getResult().setText(mainView.getResult().getText() + "\n" + "Demonic Choice process. Breaking down into: min(" + resultC1 + "," + resultC2 + ")"); 

				//TODO change back to truncation method
				return calculation("min(" + resultC1 + "," + resultC2 + ")");

			}
			
			else if(C.startsWith("if")) {
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
				String resultC1 = wp(ifC1,f);
				String resultC2 = wp(ifC2,f);
				mainView.getResult().setText(mainView.getResult().getText() + "\n" + "Conditional process. Breaking down into: if("+condition+") then "+ resultC1 +" else "+ resultC2); 
				if(calculation(condition).equals("1.0")) {
					return calculation(resultC1);
				}
				if(calculation(condition).equals("0.0")) {
					return calculation(resultC2);
				}
				return calculation("if(" + condition + "," + resultC1 + "," + resultC2 + ")"); //TODO can skip calculation?

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
				mainView.getResult().setText(mainView.getResult().getText() + "\n" + "Probability process. Breaking down into: " + probability + " * " + resultC1 +" + "+ negProbability.calculate() + " * " + resultC2); 
				String result = calculation("(" + probability + " * "+ resultC1 +" + "+ negProbability.calculate() + " * " + resultC2+")");

				return result;
			}
			else if(C.startsWith("while")){
				//while process
				//TODO implement sigma forward parsing? then we could check condition before doing fixpoint iteration for performance boost
				System.out.println("Enter while process"); 
				String condition = C.substring(C.indexOf("(")+1,C.indexOf(")"));
				System.out.println("Condition: "+condition);
				String whileC = C.substring(condition.length());
				whileC = getInsideBracket(whileC.substring(whileC.indexOf("{")+1));
				System.out.println("whileC: "+whileC);
				
				if (mainView.getAllSigmaIteration().isSelected()) {
					 
					return fixpointIterationAllSigma(condition, whileC, f, mainView.getIterationCount());				 

				} else {
				 
					return fixpointIterationIterativ(condition, whileC, f, mainView.getIterationCount());
				 
				}
				
				
			}else {
				//variable assignments
				//TODO need to implement new WP that handles concrete sigma assignments
				if(C.contains("skip")){
					System.out.println("Enter skip process"); 
					String skipResult = C.replace("skip", f);
					mainView.getResult().setText(mainView.getResult().getText() + "\n" + "Assignment skip process." + skipResult);
					return calculation(skipResult);
				}else {
					System.out.println("Enter assignment process"); 
					String indexC = C.substring(0,1);
					String cutC = C.substring(C.indexOf("=")+1);
					String assignResult = f.replace(indexC, "r(" + cutC + ")");

					
					//if mid calculation optimization
					if(assignResult.startsWith("if")) {
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
					
					mainView.getResult().setText(mainView.getResult().getText() + "\n" + "Assignment process." + assignResult);
					return calculation(assignResult);
				}
					
					
			}
			
		}
	
	}
	
	public String calculation(String exp) {
		Function restrictValue = new Function("r", "min(max(0,x),"+mainView.getRestriction()+")", "x");
		Expression e = new Expression(exp,restrictValue);
		System.out.println("Expression:" + exp);
		Double result = e.calculate();
		if(!result.isNaN()) {
			System.out.println("Calculation Result: " + result);
			return Double.toString(result);
		}else {
			System.out.println("Calculation Result: " + exp);
			return exp;
		}
	}
	
	public String fixpointIterationIterativ(String condition, String C, String f, int count) {
		String caseF = "0"; //X^0 initialization
		for(int i=0; i<count; i++) {
			String X = wp(C, caseF);
			caseF = "if("+condition+","+X+","+f+")";	
		}
		return caseF;
	}
	
	//TODO write tests
	public String fixpointIterationAllSigma(String condition, String C, String f, int count) {
		LinkedHashMap<String,Double> fixpoint = new LinkedHashMap<String, Double>();
		
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
				for(int i=0; i<count; i++) {
					String X = wp(C, caseF);
					caseF = "if("+condition+","+X+","+f+")";
					//TODO future improvement directly input sigma through assignment = f.replace x with sigma x and keep dependency somehow
					sigmaResult = calculateConcreteSigma(caseF,sigma);
					
					//TODO after second iteration we can start checking for delta?
					if(i > 2) {
						if(sigmaResult-previousResult < Double.parseDouble(mainView.getDeltaInput().getText())) {
							break;
						}else {
							previousResult = sigmaResult;
						}
					}
				}
			}			
			fixpoint.put(identifier, sigmaResult);	
		}
		return fixpointIfConversion(fixpoint);
	}
	
	public String fixpointIfConversion(LinkedHashMap<String,Double> fixpoint) {
		String result = "iff(";
		for(Map.Entry<String, Double> entry : fixpoint.entrySet()) {
			result += ";" + entry.getKey()+","+entry.getValue();
		}
		result = result.replaceFirst(";", "");
		result += ")";
		return result;
	}
	
	//TODO add other possibility of calculating concrete sigma: wp("sigma=x=1;c=1";caseF,null); = Xi
	public Double calculateConcreteSigma(String f, LinkedHashMap<String,String> sigma) {
		for(Map.Entry<String, String> entry : sigma.entrySet()) {
			f = f.replace(entry.getKey(), entry.getValue());
		}
		Function restrictValue = new Function("r", "min(max(0,x),"+mainView.getRestriction()+")", "x");
		Expression e = new Expression(f,restrictValue);
		Double result = e.calculate();
		if(result.isNaN()) {
			//throw exception and break
			System.out.println("There are unknown variables in the formula!");
			return null;
		}else {
			return result;
		}
	}
	
	//fills allSigma with all possibilities of variable and value combinations
		public ArrayList<LinkedHashMap<String,String>> fillAllSigma(String varInput) {
			allSigma.clear();
			int varCount = varInput.length();
			
			List<List<Character>> preCartesianValues = new ArrayList<List<Character>>(); 
			
			String restrictedSet = "";
			for (int i = 0 ; i < mainView.getRestriction()+1; i++) {
				restrictedSet += i;
			}
			
			List<Character> restrictedList = new ArrayList<Character>(Lists.charactersOf(restrictedSet));
			
			for(int i = 0 ; i < varCount ; i++) {	
				preCartesianValues.add(restrictedList);
			}

			List<List<Character>> postCartesianValues = Lists.cartesianProduct(preCartesianValues);
			
			for(int i = 0 ; i < postCartesianValues.size(); i++){
				LinkedHashMap<String, String> tempMap = new LinkedHashMap<String,String>();
				for(int j = 0 ; j < postCartesianValues.get(i).size(); j++){
				tempMap.put(String.valueOf(varInput.charAt(j)), postCartesianValues.get(i).get(j).toString());
				}
				allSigma.add(tempMap);
			}
			return allSigma;
		}	
	
	//start with C in one index after first appearance of start char
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
	
	public String getInsideIf(String C) {
		int commaCount = 1;
		String result = "";
		for(int i = 0; i < C.length(); i++) {
			char character = C.charAt(i);
			//if inside if case
			if(character == 'f') {
				commaCount += 2;
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
	
 //Deprecated method to calculate restrictions on variables without MathParser
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
					String truncatedValue = Double.toString(NumberUtils.min(insideValue,mainView.getRestriction()));							
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
  
	public LinkedHashMap<String, String> getVariables() {
		return variables;
	}

	public void setVariables(LinkedHashMap<String, String> variables) {
		this.variables = variables;
	}	

	public void linkView(WPCalculatorView mainView) {
		this.mainView = mainView;
	}
	
}
