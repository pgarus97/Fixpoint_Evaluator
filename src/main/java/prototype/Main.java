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
	public static String wp(String C, String f) {
		//TODO here we need to parse C and select the right submethods
		// first step is cut ; into multiple Cs
		System.out.println("wp["+C+"]("+f+")" ); 
		if(C.contains(";")) {
			String C1 = C.substring(0, C.indexOf(";"));
			String C2 = C.substring(C.indexOf(";"));
			System.out.println("Breaking down into: wp["+C1+"](wp["+C2+"](f)" ); 
			return (wp(C1, wp(C2,f)));
		}else {
			Argument x = new Argument(C);
			Expression e = new Expression(f, x);
			
			double result = e.calculate();
			System.out.println(result);
			return Double.toString(result);
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
