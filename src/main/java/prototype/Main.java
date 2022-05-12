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
	
	//first example: x = 5 ; if(x=5){x = x+1} else {x = x-1}
	public static double wp(String C, String f) {
		//TODO here we need to parse C and select the right submethods
		// first step is cut ; into multiple Cs
		System.out.println("wp["+C+"]("+f+")" );
		
		//nested example: if(x==5){if(y==5){y= y+1} else {y = y-1}} else { x = x+1}
		//second step {if(y==5){y= y+1} else {y = y-1}} else { x = x+1}
		// push if(y==5){y= y+1} else {y = y-1}} else { x = x+1} 0
		// push y = y+1} else {y = y-1}} else { x = x+1}
		
		
		if(C.startsWith(" if ")) {
			System.out.println("Enter conditional if"); 

			String condition = C.substring(C.indexOf("(")+1,C.indexOf(")"));
			System.out.println("Conditional: "+condition); 
			int bracketCount = 1;
			String C1 = C.substring(C.indexOf("{")+1);
			System.out.println("C1 before for: " + C1); 
			//TODO rewrite with 
			/*
			 * for(int i = 0; i < s.length(); i++)
				{
				   char c = s.charAt(i);
				}
			 */
			for (char character : C1.toCharArray()) {
				if(bracketCount != 0) {
					if(character == '{') {
						bracketCount++;
						System.out.println("brackets +"); 
					}
					if(character == '}') {
						bracketCount--;
					}
					C1 = C1 + character;
				}else {
					break;
				}
			}
			String C2 = C.substring(C.indexOf(C1));
			C2 = C2.substring(C2.indexOf("{")+1);
			bracketCount++;
			for (char character2 : C2.toCharArray()) {
				if(bracketCount != 0) {
					if(character2 == '{') {
						bracketCount++;
					}
					if(character2 == '}') {
						bracketCount--;
					}
					C2 = C2 + character2;
				}else {
					break;
				}
			}
			System.out.println("C1= "+C1); 
			System.out.println("C2= "+C2); 
			System.out.println("Breaking down into: if("+condition+") then "+ wp(C1,f)+" else "+ wp(C2,f)); 
			Expression e = new Expression("if("+condition+"),"+ wp(C1,f)+","+ wp(C2,f)+")");
			return e.calculate();
		}
		
		//wont work, need to have priority on other operators
		else if(C.contains(";")) {
			System.out.println("Enter sequential if"); 

			String C1 = C.substring(0, C.indexOf(";")-1);
			String C2 = C.substring(C.indexOf(";")+1);
			System.out.println("Breaking down into: wp["+C1+"](wp[+"+C2+"]("+f+"))"); 
			return (wp(C1, Double.toString(wp(C2,f))));
		}else {
			Argument x = new Argument(C);
			Expression e = new Expression(f, x);
			
			double result = e.calculate();
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
