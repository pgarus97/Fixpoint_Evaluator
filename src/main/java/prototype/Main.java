package prototype;

import java.util.HashMap;
import java.util.Scanner;

import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;

public class Main {

//Parser Documentation: https://github.com/mariuszgromada/MathParser.org-mXparser
	
	public static void main(String[] args) {
	    Scanner input = new Scanner(System.in);

	    //get inputs from console
	    System.out.print("Enter initial variable assignments: (Multiple possible: e.g. 'x=5;y=3;z=2");
	    String sigma = input.nextLine();
	    if(sigma.isEmpty()) {
	    	//throw exception;
	    	System.out.println("You have to input an initial variable assignment");
	    	return;
	    }
	    
	    System.out.print("Enter C: ");
	    String C = input.nextLine();
	    System.out.print("Enter f: ");
	    String f = input.nextLine();
	    C = sigma+C;

	    //TODO new persistent variable implementation
	    HashMap<String, String> variables = new HashMap<String,String>();
		System.out.println("wp["+C+"]("+f+") =" );
		//Variables variables = new Variables(inputtedVariables);
		//append variables C1 = x=5 etc. as C1;C2;C3;C
		System.out.println(wp(C,f));
	}
	
	//TODO probably obsolete if we require initial variable assignments
	public static String calculation(String exp) {
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
	
	
	public static String wp(String C, String f) {
		C = C.replace(" ", "");
		System.out.println("wp["+C+"]("+f+")" );
		System.out.println("C "+ C);
		String C1 = getSequentialCut(C);
		System.out.println("C1: " + C1);
		if(!C1.equals(C)) {
			System.out.println("Enter sequential process"); 
			String C2 = C.substring(C1.length()+1);
			System.out.println("C2: " + C2);
			System.out.println("Breaking down into: wp["+C1+"](wp["+C2+"]("+f+"))"); 
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
				System.out.println("Breaking down into: if("+condition+") then "+ resultC1 +" else "+ resultC2); 
				String result = "if("+condition+","+ resultC1 + "," + resultC2 + ")";
				
				return result;
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
				System.out.println("Breaking down into: "+probability+" * "+ resultC1 +" + "+ negProbability.calculate() +" * "+ resultC2); 
				String result = probability+" * "+resultC1+" + "+negProbability.calculate()+" * "+resultC2;

				return result;
			}
			else if(C.startsWith("while")){
				//while process TODO
				System.out.println("Enter while process"); 
				String condition = C.substring(C.indexOf("(")+1,C.indexOf(")"));
				System.out.println("Condition: "+condition);
						//parse "if condition, then take f, else wp C 0 ? with X(0) = f = 0?
				String result = "";

				return result;
				
			}else {
				//variable assignments
				System.out.println("Enter assignment process"); 
				
				if(C.contains("skip")){
					String result = C.replace("skip", f);
					System.out.println(result);
					return result;
				}else {
					String indexC = C.substring(0,1);
					String cutC = C.substring(C.indexOf("=")+1);
					String result = f.replace(indexC, "("+cutC+")");
					System.out.println(result);
					return result;
				}
				
			}
		
		}
		
	}	
		
	
}
