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
			//conditional process
			System.out.println("Enter conditional process"); 

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
			String result = "if("+condition+","+ resultC1 + "," + resultC2 + ")";

			return result;
		}
		
		else if(C.contains(";")) {
			//sequential process
			System.out.println("Enter sequential process"); 

			String C1 = C.substring(0, C.indexOf(";")-1);
			String C2 = C.substring(C.indexOf(";")+1);
			System.out.println("Breaking down into: wp["+C1+"](wp["+C2+"]("+f+"))"); 
			return wp(C1,(wp(C2,f)));
		}else {
			//variable assignments
					
			//TODO cutC disregards multiple variables like y = 4 and x = 4 is the same and will both be substituted
			//TODO perhaps check what variables are defined in f and then use those in cutC?
			C = C.replace(" ", "");
			String indexC = C.substring(0,1);
			String cutC = C.substring(C.indexOf("=")+1);
			String result = f.replace(indexC, "("+cutC+")");
			System.out.println(result);
			return result;
		}
	}
	
}
