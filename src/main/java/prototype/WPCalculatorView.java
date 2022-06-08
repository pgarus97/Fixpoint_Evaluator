package prototype;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class WPCalculatorView {
	
public JTextArea result = new JTextArea();
JCheckBox allSigmaIteration = new JCheckBox("Enable all-sigma fixpoint-iteration.");

private double restriction;
private int iterationCount = 10;
private WPCalculator mainCalculator;

	public WPCalculatorView() {
		JFrame frame = new JFrame("wp-Calculator");	
	    
	    JLabel Cdesc = new JLabel("Input the Program (C) here:");
	    Cdesc.setBounds(5,0,170, 20);
	    JTextField C = new JTextField();
	    C.setBounds(5,20,170, 20);
	    
	    JLabel Fdesc = new JLabel("Input the postexpectation (f) here:");
	    Fdesc.setBounds(200,0,200, 20);
	    JTextField F = new JTextField();
	    F.setBounds(200,20,200, 20);
	    
	    JLabel RestrictionDesc = new JLabel("Input the restriction (k) here:");
	    RestrictionDesc.setBounds(430,0,200, 20);
	    JTextField restrictionField = new JTextField();
	    restrictionField.setBounds(430,20,200, 20);
	    
	    JLabel iterationDesc = new JLabel("Input the restriction (k) here:");
	    iterationDesc.setBounds(430,0,200, 20);
	    JTextField iterationField = new JTextField();
	    iterationField.setBounds(430,50,200, 20);
	    
	    
	    JLabel sigmaDesc = new JLabel("Enter initial variable assignments: (Multiple possible: e.g. 'x=5;y=3;z=2')");
	    sigmaDesc.setBounds(5,50,400, 20);
	    JTextField sigma = new JTextField();
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
	    	    	//allSigma = getVariableCombinations(usedVars.getText(),restriction); //TODO still needs to be implemented
	    	    }
	    		if(sigma.getText().isEmpty()) {
		    	    calcResult = mainCalculator.calculation(mainCalculator.wp(sigma.getText()+C.getText(),F.getText())); 
	    		}else {
		    	    calcResult = mainCalculator.calculation(mainCalculator.wp(sigma.getText()+";"+C.getText(),F.getText())); 
	    		}
	    	    result.setText(result.getText() + "\n" + "Result: " + calcResult);
    	   }  
	    }); 
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
	
	public JCheckBox getAllSigmaIteration() {
		return allSigmaIteration;
	}

	public void setAllSigmaIteration(JCheckBox allSigmaIteration) {
		this.allSigmaIteration = allSigmaIteration;
	}

	public void linkCalculator(WPCalculator mainCalculator) {
		this.mainCalculator = mainCalculator;
	}
}
