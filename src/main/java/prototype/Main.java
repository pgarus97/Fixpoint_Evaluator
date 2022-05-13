package prototype;

import java.util.Scanner;

import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;

public class Main {

//Parser Documentation: https://github.com/mariuszgromada/MathParser.org-mXparser
	
	public static void main(String[] args) {
	    Scanner input = new Scanner(System.in);

	    //get inputs from console
	    System.out.print("Enter C: ");
	    String C = input.nextLine();
	    System.out.print("Enter f: ");
	    String f = input.nextLine();
	    
		System.out.println("wp["+C+"]("+f+") =" );
		System.out.println(wp(C,f));
	}
	
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
	
	//first example: x = 5 ; if(x=5){x = x+1} else {x = x-1}
	public static String wp(String C, String f) {
		C = C.replace(" ", "");

		System.out.println("wp["+C+"]("+f+")" );	
		
		if(C.startsWith("if")) {
			//conditional process
			System.out.println("Enter conditional process"); 
			String condition = C.substring(C.indexOf("(")+1,C.indexOf(")"));
			System.out.println("Conditional: "+condition); 
			int bracketCount = 1;
			String C1 = "";
			String tempC1 = C.substring(C.indexOf("{")+1);
			//TODO makea separate method for getInsideBracket(String)
			for(int i = 0; i < tempC1.length(); i++) {
				char character = tempC1.charAt(i);
				if(character == '{') {
					bracketCount++;
				}
				if(character == '}') {
					bracketCount--;
				}
				if(bracketCount != 0) {
					C1 = C1 + character;
				}else {
					break;
				}
			}
			
			String C2 = "";
			String tempC2 = C.substring(C.indexOf(C1));
			tempC2 = tempC2.substring(tempC2.indexOf("{")+1);
			bracketCount++;
			for(int i = 0; i < tempC2.length(); i++) {
				char character = tempC2.charAt(i);
				if(character == '{') {
					bracketCount++;
				}
				if(character == '}') {
					bracketCount--;
				}
				if(bracketCount != 0) {
					C2 = C2 + character;
				}else {
					break;
				}
			}
			System.out.println("C1= "+C1); 
			System.out.println("C2= "+C2);
			String resultC1 = wp(C1,f);
			String resultC2 = wp(C2,f);
			System.out.println("Breaking down into: if("+condition+") then "+ resultC1 +" else "+ resultC2); 
			String result = "if("+condition+","+ resultC1 + "," + resultC2 + ")";

			return result;
		}
		
		else if(C.startsWith("{")){
			//probability process
			System.out.println("Enter probability process"); 
			
			int bracketCount = 1;
			String C1 = "";
			String tempC1 = C.substring(C.indexOf("{")+1);

			for(int i = 0; i < tempC1.length(); i++) {
				char character = tempC1.charAt(i);
				if(character == '{') {
					bracketCount++;
				}
				if(character == '}') {
					bracketCount--;
				}
				if(bracketCount != 0) {
					C1 = C1 + character;
				}else {
					break;
				}
			}
			String C2 = "";
			String tempC2 = C.substring(C.indexOf(C1));
			String probability = tempC2.substring(tempC2.indexOf("[")+1,C.indexOf("]")-1);
			tempC2 = tempC2.substring(tempC2.indexOf("{")+1);
			bracketCount++;
			for(int i = 0; i < tempC2.length(); i++) {
				char character = tempC2.charAt(i);
				if(character == '{') {
					bracketCount++;
				}
				if(character == '}') {
					bracketCount--;
				}
				if(bracketCount != 0) {
					C2 = C2 + character;
				}else {
					break;
				}
			}
			System.out.println("C1= "+C1); 
			System.out.println("C2= "+C2);
			System.out.println("Probability:" + probability);
			Expression negprobability = new Expression ("1-"+probability);
			String resultC1 = wp(C1,f);
			String resultC2 = wp(C2,f);
			System.out.println("Breaking down into: "+probability+" * "+ resultC1 +" + "+ negprobability.calculate() +" * "+ resultC2); 
			String result = probability+" * "+resultC1+" + "+negprobability.calculate()+" * "+resultC2;

			return result;
		}
		
		else if(C.contains(";")) {
			//sequential process
			System.out.println("Enter sequential process"); 

			String C1 = C.substring(0, C.indexOf(";"));
			String C2 = C.substring(C.indexOf(";")+1);
			System.out.println("Breaking down into: wp["+C1+"](wp["+C2+"]("+f+"))"); 
			return wp(C1,(wp(C2,f)));
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
