package prototype;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.commons.lang3.math.NumberUtils;
import org.mariuszgromada.math.mxparser.Expression;
//Parser Documentation: https://github.com/mariuszgromada/MathParser.org-mXparser

public class WPCalculatorAllSigma {

private HashMap<String, String> variables = new HashMap<String,String>();
JCheckBox allSigmaIteration = new JCheckBox("Enable all-sigma fixpoint-iteration.");
private ArrayList<HashMap<String, String>> allSigma = new ArrayList<HashMap<String, String>>();
public JTextArea result = new JTextArea();
private double restriction;
private int iterationCount = 10;



	public WPCalculatorAllSigma() {
		JFrame frame = new JFrame("wp-Calculator");	
	    
		
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
	    
	    JLabel iterationDesc = new JLabel("Input the restriction (k) here:");
	    iterationDesc.setBounds(430,0,200, 20);
	    final JTextField iterationField = new JTextField();
	    iterationField.setBounds(430,50,200, 20);
	    
	    
	    JLabel sigmaDesc = new JLabel("Enter initial variable assignments: (Multiple possible: e.g. 'x=5;y=3;z=2')");
	    sigmaDesc.setBounds(5,50,400, 20);
	    final JTextField sigma = new JTextField();
	    sigma.setBounds(5,70,400, 20);
	    
	    JLabel usedVarsDesc = new JLabel("Enter all used variables (separate with , ) ");
	    usedVarsDesc.setBounds(500,90,400, 20);
	    final JTextField usedVars = new JTextField();
	    usedVars.setBounds(500,110,400, 20);

	    JButton calcButton = new JButton("Calculate!");
	    calcButton.setBounds(5,100,150, 40); 
	       
	    
	    allSigmaIteration.setBounds(200,100,300, 50);
	    frame.add(allSigmaIteration);
	  
	    frame.add(Cdesc);
	    frame.add(C);
	    frame.add(Fdesc);
	    frame.add(F);
	    frame.add(RestrictionDesc);
	    frame.add(restrictionField);
	    frame.add(iterationField);
	    frame.add(sigmaDesc);
	    frame.add(sigma);
	    frame.add(calcButton);
	    
	    
	    JScrollPane scroll = new JScrollPane(result);
	    scroll.setBounds(10,200 ,800, 400); 
	    result.setEditable(false);
	    
	    frame.getContentPane().add(scroll);
	    
	    
	    frame.setSize(1000,700);
	    frame.setLayout(null); 
	    frame.setVisible(true);
	    
	    frame.add(usedVars);
	    frame.add(usedVarsDesc);
	    usedVars.setVisible(false);
    	usedVarsDesc.setVisible(false);
	    
	    allSigmaIteration.addActionListener(new ActionListener(){  
	    	public void actionPerformed(ActionEvent e){  
	    		if (allSigmaIteration.isSelected()) {
	    	    	usedVars.setVisible(true);
	    	    	usedVarsDesc.setVisible(true);
	    	    }else {
	    	    	usedVars.setVisible(false);
	    	    	usedVarsDesc.setVisible(false);
	    	    }
    	   }  
	    });
	    
	    calcButton.addActionListener(new ActionListener(){  
	    	public void actionPerformed(ActionEvent e){  
	    		
	    		result.setText(""); 	
	    		
	    		/*if(sigma.getText().isEmpty()) {
	    	    	//throw exception;
	    			result.setText("You have to input an initial variable assignment!");
	    	    	return;
	    	    } */
	    		if(restrictionField.getText().isEmpty()) {
	    	    	//throw exception;
	    			result.setText("You have to set a restriction for the variables!");
	    	    	return;
	    	    } 
	    		setRestriction(Double.parseDouble(restrictionField.getText()));
	    		if(!iterationField.getText().isEmpty()) {
		    		setIterationCount(Integer.parseInt(iterationField.getText()));
	    	    }
	    		String calcResult = "";
	    		if (allSigmaIteration.isSelected()) {
	    	    	allSigma = getVariableCombinations(usedVars.getText(),restriction); //TODO still needs to be implemented
	    	    }
	    		if(sigma.getText().isEmpty()) {
		    	    calcResult = calculation(wp(sigma.getText()+C.getText(),F.getText(),null));
	    		}else {
		    	    calcResult = calculation(wp(sigma.getText()+";"+C.getText(),F.getText(),null)); 
	    		}
	    	    result.setText(result.getText() + "\n" + "Result: " + calcResult);
    	   }  
	    }); 
	    

	}
	
	public String wp(String C, String f, HashMap<String, String> sigma) {
		//TODO check which calculations can be skipped
		
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
			return wp(C1,(wp(C2,f,sigma)),sigma);
		}else {
			if(C.startsWith("min{")) {
				//demonic choice process
				System.out.println("Enter demonic choice process"); 
				String demC1 = getInsideBracket(C.substring(C.indexOf("{")+1));	
				String demC2 = C.substring(C.indexOf(demC1));
				demC2 = getInsideBracket(demC2.substring(demC2.indexOf("{")+1));
				
				System.out.println("demC1= "+demC1); 
				System.out.println("demC2= "+demC2);
				String resultC1 = wp(demC1,f,sigma);
				String resultC2 = wp(demC2,f,sigma);

				result.setText(result.getText() + "\n" + "Demonic Choice process. Breaking down into: min(" + resultC1 + "," + resultC2 + ")"); 

				return calculation("min(" + resultC1 + "," + resultC2 + ")");

			}
			
			else if(C.startsWith("if")) {
				//conditional process
				System.out.println("Enter conditional process"); 
				String condition = getInsideBracket(C.substring(C.indexOf("{")+1));
				System.out.println("Conditional: "+condition); 
				String ifC1 = C.substring(condition.length()+4);
				ifC1 = getInsideBracket(ifC1.substring(ifC1.indexOf("{")+1));	//does not get correct value
				String ifC2 = C.substring(C.indexOf(ifC1));
				ifC2 = getInsideBracket(ifC2.substring(ifC2.indexOf("{")+1));
				
				System.out.println("C1= "+ifC1); 
				System.out.println("C2= "+ifC2);
				String resultC1 = wp(ifC1,f,sigma);
				String resultC2 = wp(ifC2,f,sigma);
				result.setText(result.getText() + "\n" + "Conditional process. Breaking down into: if("+condition+") then "+ resultC1 +" else "+ resultC2); 
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
				Expression negProbability = new Expression ("1-"+probability); //TODO does this work for greater 1 probabilities? Or should it even work?
				String resultC1 = wp(probC1,f,sigma);
				String resultC2 = wp(probC2,f,sigma);
				result.setText(result.getText() + "\n" + "Probability process. Breaking down into: " + probability + " * " + resultC1 +" + "+ negProbability.calculate() + " * " + resultC2); 
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
				
				if (allSigmaIteration.isSelected()) {
					 
					//return fixpointIterationAllSigma(condition, whileC, f, iterationCount); still in development ; needs HashMap<String, String> sigma as parameter in wp
					return fixpointIterationIterativ(condition, whileC, f, iterationCount); 

				 
				} else {
				 
					return fixpointIterationIterativ(condition, whileC, f, iterationCount);
				 
				}
				
				
			}else {
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
					String assignResult = f.replace(indexC, "min(" + cutC + "," + restriction + ")");

					
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
		
	 // Deprecated method to calculate restrictions on variables
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

	
	public String fixpointIterationIterativ(String condition, String C, String f, int count) {
		String caseF = "0"; //X^0 initialization
		for(int i=0; i<count; i++) {
			String X = wp(C, caseF, null);
			caseF = "if("+condition+","+X+","+f+")";	
		}
		//TODO round calculation result here if it is a number = how much should we round?
		return calculation(caseF);
	}
	
	public String fixpointIterationAllSigma(String condition, String C, String f, int count) {
		//iterate through array of all sigmas and get X for each sigma => save that and compare to next loop => Result Hashmap
		//check condition first, if wrong with sigma then skip
		
		//TODO how do we do the while fixpoint iteration on a concrete sigma for only one step??? calculate only one step at a time and insert?
		
		String caseF = "0"; //X^0 initialization
		for(int i=0; i<count; i++) {
			String X = wp(C, caseF,null); //TODO iteration with all sigma here
			caseF = "if("+condition+","+X+","+f+")";	
		}
		
		return caseF;
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
	
	public ArrayList<HashMap<String,String>> getVariableCombinations(String varInput, double restriction) {
		for(int i = 0; i<varInput.length(); i++) {
		//TODO guava cartesian product 	
		}
		return null;
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
