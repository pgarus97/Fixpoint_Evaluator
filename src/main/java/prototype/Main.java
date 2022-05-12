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
	
	public static double calculation(String exp) {
		Expression e = new Expression(exp);
		System.out.println("Expression:" + exp);
		System.out.println("Calculation Result: " + e.calculate());
		return e.calculate();
	}
	
	//first example: x = 5 ; if(x=5){x = x+1} else {x = x-1}
	public static String wp(String C, String f) {
		System.out.println("wp["+C+"]("+f+")" );	
		
		if(C.startsWith(" if ")) {
			System.out.println("Enter conditional if"); 

			String condition = C.substring(C.indexOf("(")+1,C.indexOf(")"));
			System.out.println("Conditional: "+condition); 
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
			//TODO no idea what happens in this case. Like page 10 but no x to evaluate phi
			String result = "if("+condition+","+ resultC1 + "," + resultC2 + ")";

			return result;
		}
		
		else if(C.contains(";")) {
			System.out.println("Enter sequential if"); 

			String C1 = C.substring(0, C.indexOf(";")-1);
			String C2 = C.substring(C.indexOf(";")+1);
			System.out.println("Breaking down into: wp["+C1+"](wp["+C2+"]("+f+"))"); 
			return wp(C1,(wp(C2,f)));
		}else {
			// TODO probably need new assignment functionality as variables need to be stored separately to access f
			// TODO wp[x=x+1](x^2) should result in (x+1)^2 String without calculation.
			// TODO calculation should only happen if we have a non dependent assignment
			// TODO on lowest recursion point we need to merge and fix together the strings that we will return later as result of wp
		
			//TODO check here if variable contains only numbers x = 5 or also dependent variables. Actually maybe not, just string it together somehow?
			
			String cutC = C.substring(C.indexOf("=")+1);
			String result = f.replace("x", "("+cutC+")");
			System.out.println(result);
			return result;
		}
	}
	
	// C = x:=...
	//Example:  C: x=5 ; f: x^2
	public static int assignment(String C, String f) {
		Argument x = new Argument(C);
		Expression e = new Expression(f, x);
		return (int) e.calculate();
	}
	
	// C = if (phi) { C1 } else { C2 }
	// Expression e = new Expression("if(sin(x) > 5, 1, 0)", x);
	//if(x,y,z) if x then y else z
	public static int conditional() {
		int result=0;
		Argument x = new Argument("x = 5");
		Expression e = new Expression("sin(x)", x);
		e.calculate();
		return result;
	}
	
	// C = C1 ; C2
	public static int consecutive() {
		int result=0;
		Argument x = new Argument("x = 5");
		Expression e = new Expression("sin(x)", x);
		e.calculate();
		return result;
	}
	
	// C = { C1 } [p] { C2 }
	public static int probability() {
		int result=0;
		Argument x = new Argument("x = 5");
		Expression e = new Expression("sin(x)", x);
		e.calculate();
		return result;
	}
}
