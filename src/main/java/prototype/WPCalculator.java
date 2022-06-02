package prototype;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.commons.lang3.math.NumberUtils;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;
//Parser Documentation: https://github.com/mariuszgromada/MathParser.org-mXparser

public class WPCalculator {

private HashMap<String, String> variables = new HashMap<String,String>();
public JTextArea result = new JTextArea();
private double restriction;
private int iterationCount = 10;



	public WPCalculator() {
		JFrame frame = new JFrame("wp-Calculator");	

	    //TODO new persistent variable implementation
	    //TODO check last value empty , index
	    /*sigma = sigma.replace(" ", "");
	    for(int i=0; i < sigma.length(); i++) {
	    	String variableName = C.substring(0,1);
			String variableValue = C.substring(C.indexOf("=")+1,C.indexOf(','));
			variables.put(variableName,variableValue);
			sigma = sigma.substring(variableValue.length());
	    }*/
	    
	    JLabel Cdesc = new JLabel("Input the Program (C) here:");
	    Cdesc.setBounds(5,0,170, 20);
	    final JTextField C = new JTextField();
	    C.setBounds(5,20,170, 20);
	    
	    JLabel Fdesc = new JLabel("Input the postexpectation (f) here:");
	    Fdesc.setBounds(200,0,200, 20);
	    final JTextField F = new JTextField();
	    F.setBounds(200,20,200, 20);
	    
	    JLabel RestrictionDesc = new JLabel("Input the restriction (k) here:");
	    RestrictionDesc.setBounds(430,0,200, 20);
	    final JTextField restrictionField = new JTextField();
	    restrictionField.setBounds(430,20,200, 20);
	    
	    JLabel sigmaDesc = new JLabel("Enter initial variable assignments: (Multiple possible: e.g. 'x=5;y=3;z=2')");
	    sigmaDesc.setBounds(5,50,400, 20);
	    final JTextField sigma = new JTextField();
	    sigma.setBounds(5,70,400, 20);

	    JButton calcButton = new JButton("Calculate!");
	    calcButton.setBounds(5,100,100, 40); 
	       
	    
	    frame.add(Cdesc);
	    frame.add(C);
	    frame.add(Fdesc);
	    frame.add(F);
	    frame.add(RestrictionDesc);
	    frame.add(restrictionField);
	    frame.add(sigmaDesc);
	    frame.add(sigma);
	    frame.add(calcButton);
	    
	    result.setBounds(10,200 ,800, 400); 
	    result.setEditable(false);
	    frame.add(result);
	    
	    
	    frame.setSize(1000,700);
	    frame.setLayout(null); 
	    frame.setVisible(true);
	    
	    calcButton.addActionListener(new ActionListener(){  
	    	public void actionPerformed(ActionEvent e){  
	    		
	    		result.setText(""); 	
	    		
	    		if(sigma.getText().isEmpty()) {
	    	    	//throw exception;
	    			result.setText("You have to input an initial variable assignment!");
	    	    	return;
	    	    }
	    		if(restrictionField.getText().isEmpty()) {
	    	    	//throw exception;
	    			result.setText("You have to set a restriction for the variables!");
	    	    	return;
	    	    }
	    		setRestriction(Double.parseDouble(restrictionField.getText()));
	    	    String calcResult = calculation(wp(sigma.getText()+";"+C.getText(),F.getText())); 
	    	    result.setText(result.getText() + "\n" + "Result: " + calcResult);
    	   }  
	    }); 
	    

	}
	
	public String wp(String C, String f) {
		//TODO somehow parse through C and check on calculation if there is any assignment > k ; 
		//=> substitute all assignments to min function if we still work with terms 
		C = C.replace(" ", "");
		result.setText(result.getText() + "\n" + "wp["+C+"]("+f+")");
		System.out.println("C "+ C);
		String C1 = getSequentialCut(C);
		System.out.println("C1: " + C1);
		if(!C1.equals(C)) {
			System.out.println("Enter sequential process"); 
			String C2 = C.substring(C1.length()+1);
			System.out.println("C2: " + C2);
			result.setText(result.getText() + "\n" + "Sequential process. Breaking down into: wp["+C1+"](wp["+C2+"]("+f+"))"); 
			return wp(C1,(wp(C2,f)));
		}else {
			if(C.startsWith("if")) {
				//conditional process
				System.out.println("Enter conditional process"); 
				String condition = C.substring(C.indexOf("(")+1,C.indexOf(")"));
				System.out.println("Conditional: "+condition); 
				String ifC1 = getInsideBracket(C.substring(C.indexOf("{")+1));	
				String ifC2 = C.substring(C.indexOf(ifC1));
				ifC2 = getInsideBracket(ifC2.substring(ifC2.indexOf("{")+1));
				
				System.out.println("C1= "+ifC1); 
				System.out.println("C2= "+ifC2);
				String resultC1 = wp(ifC1,f);
				String resultC2 = wp(ifC2,f);
				result.setText(result.getText() + "\n" + "Conditional process. Breaking down into: if("+condition+") then "+ resultC1 +" else "+ resultC2); 
				//TODO here we could calculate if clauses directly before putting them together into long strings
				//these calculations can be skipped as it never should be the case
				if(calculation(condition).equals("1.0")) {
					return resultC1;
				}
				if(calculation(condition).equals("0.0")) {
					return resultC2;
				}
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
				String resultC1 = calculation(wp(probC1,f));
				String resultC2 = calculation(wp(probC2,f));
				result.setText(result.getText() + "\n" + "Probability process. Breaking down into: "+probability+" * "+ resultC1 +" + "+ negProbability.calculate() +" * "+ resultC2); 
				String result = calculation("("+probability+" * "+resultC1+" + "+negProbability.calculate()+" * "+resultC2+")");

				return result;
			}
			else if(C.startsWith("while")){
				//while process TODO
				//TODO implement sigma forward parsing? then we could check condition before doing fixpoint iteration for performance boost
				//TODO try first how fast it grows
				System.out.println("Enter while process"); 
				String condition = C.substring(C.indexOf("(")+1,C.indexOf(")"));
				System.out.println("Condition: "+condition);
				String whileC = C.substring(condition.length());
				whileC = getInsideBracket(whileC.substring(whileC.indexOf("{")+1));
				System.out.println("whileC: "+whileC);
				
				return fixpointIterationIterativ(condition, whileC, f, iterationCount);
				
			}else {
				//variable assignments
				/*System.out.println("Enter assignment process"); 
				
				if(C.startsWith("skip")){
					for(int i=0; i < f.length(); i++) {
						char varName = f.charAt(i);
						if(variables.containsKey(Character.toString(varName))) {
							f = f.replace(Character.toString(varName), "("+variables.get(Character.toString(varName))+")");
						}
					}
					String result = C.replace("skip", f);
					System.out.println(result);
					return result;
				}else {
					//updates variable values on assignment
					String varName = C.substring(0,1); 
					String assignExp = C.substring(C.indexOf("=")+1); 
					if(variables.containsKey(varName)) {
						Expression calculatedValue = new Expression (assignExp.replace(varName,variables.get(varName)));
						variables.put(varName,Double.toString(calculatedValue.calculate())); 

						String result = f.replace(varName, "("+variables.get(varName)+")");
						System.out.println(result);
						return result;
					}else {
						//throw exception
						System.out.println("There is an unknown variable assignment");
						//else it would also be possible to create a new variable.
						return null;
					}*/
					 //Old assignment from behind
					 //variable assignments
				
				if(C.contains("skip")){
					System.out.println("Enter skip process"); 
					String skipResult = C.replace("skip", f);
					result.setText(result.getText() + "\n" + "Assignment skip process." + skipResult);
					return calculation(skipResult);
				}else {
					System.out.println("Enter assignment process"); 
					String indexC = C.substring(0,1);
					String cutC = C.substring(C.indexOf("=")+1);
					String assignResult = f.replace(indexC, "#{("+cutC+")}");
					
					//truncation after assignment
					if(NumberUtils.isDigits(cutC) && !assignResult.equals(f)) {
						assignResult = truncate(assignResult);
					}
					
					//if mid calculation optimization ; can increase the if clause with more strict conditions to make more runtime optimizations
					if(assignResult.startsWith("if")) {
						System.out.println("Enter conditional process"); 
						String condition = assignResult.substring(assignResult.indexOf("(")+1,assignResult.indexOf(","));
						System.out.println("Conditional: "+condition); 
						String ifC1 = assignResult.substring(condition.length()+4);	
						System.out.println("ifC1= "+ifC1); 
						
						ifC1 =  getInsideIf(ifC1);
						String ifC2 = assignResult.substring(condition.length()+4+ifC1.length()+1);
						ifC2 = ifC2.substring(0,ifC2.length()-1);
						System.out.println("ifC1= "+ifC1); 
						System.out.println("ifC2= "+ifC2);
						if(calculation(condition).equals("1.0")) {
							return calculation(ifC1);
						}
						if(calculation(condition).equals("0.0")) {
							return calculation(ifC2);
						}
					}
					
					result.setText(result.getText() + "\n" + "Assignment process." + assignResult);
					return calculation(assignResult);
				}
					
					
			}
			
		}
	
	}
	
	
	//TODO probably obsolete if we require initial variable assignments
	public String calculation(String exp) {
		Expression e = new Expression(exp);
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
						i--; //reduce i to read back from new values? TODO check
						//TODO check if input length actually gets updated
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
	
	public String fixpointIterationIterativ(String condition, String C, String f, int count) {
		String caseF = "0"; //X^0 initialization
		for(int i=0; i<count; i++) {
			String X = wp(C, caseF);
			caseF = "if("+condition+","+X+","+f+")";	
		}
		//sigma has to be parameterized via forwardparsing or result of while stays in term form.
		//TODO problem with that is that we cannot do the delta comparison
		//String result = wp(sigma,caseF);
		return caseF;
	}
	
	//start with C in one index after first appearance of start char
	public static String getInsideBracket(String C) {
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
	
	public static String getInsideIf(String C) {
		int commaCount = 1;
		String result = "";
		for(int i = 0; i < C.length(); i++) {
			char character = C.charAt(i);
			if(character == 'i') {
				commaCount += 2;
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
	
	public static String getSequentialCut(String C) {
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
	
	
	public HashMap<String, String> getVariables() {
		return variables;
	}

	public void setVariables(HashMap<String, String> variables) {
		this.variables = variables;
	}	
	

	public JTextArea getResult() {
		return result;
	}

	public void setResult(JTextArea result) {
		this.result = result;
	}
	
	public double getRestriction() {
		return restriction;
	}

	public void setRestriction(double restriction) {
		this.restriction = restriction;
	}
	

	public int getIterationCount() {
		return iterationCount;
	}

	public void setIterationCount(int iterationCount) {
		this.iterationCount = iterationCount;
	}
}
