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
	    
		Argument x = new Argument(C);
		System.out.println("wp["+C+"]("+f+") =" );
		System.out.println(calculation(C,f));
	}
	
	private static int calculation(String C, String f) {
		//TODO here we need to parse C and select the right submethods
		Argument x = new Argument(C);
		Expression e = new Expression(f, x);
		return (int) e.calculate();
	}
	
	// C = x:=...
	//Example:  C: x=5 ; f: x^2
	private static int assignment(String C, String f) {
		Argument x = new Argument(C);
		Expression e = new Expression(f, x);
		return (int) e.calculate();
	}
	
	// C = if (phi) { C1 } else { C2 }
	private static int conditional() {
		int result=0;
		Argument x = new Argument("x = 5");
		Expression e = new Expression("sin(x)", x);
		e.calculate();
		return result;
	}
	
	// C = C1 ; C2
	private static int consecutive() {
		int result=0;
		Argument x = new Argument("x = 5");
		Expression e = new Expression("sin(x)", x);
		e.calculate();
		return result;
	}
	
	// C = { C1 } [p] { C2 }
		private static int probability() {
			int result=0;
			Argument x = new Argument("x = 5");
			Expression e = new Expression("sin(x)", x);
			e.calculate();
			return result;
		}
}
