package prototype;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class WPCalculatorView {
	
JCheckBox allSigmaIteration = new JCheckBox("Enable all-sigma fixpoint-iteration.");

//TODO read those values directly from the fields instead of variables to be uniform
private double restriction = 2;
private double iterationCount = 10;
private WPCalculator mainCalculator;

//Declaration of components
private JFrame frame;
private JLabel cDesc;
private JTextField cInput;
private JPanel panel;
private JPanel inputPanel;
private JPanel descPanel;
private JPanel cPanel;
private JPanel fPanel;
private JLabel fDesc;
private JTextField fInput;
private JTextField usedVars;
private JLabel usedVarsDesc;
private JLabel restrictionDesc;
private JTextField restrictionField;
private JLabel iterationDesc;
private JTextField iterationField;
private JLabel deltaDesc;
private JTextField deltaInput;
private JButton calcButton;
private JScrollPane scroll;
private JTextArea result;
private JButton examineFixpointButton;
private JButton[] whileFixpoints;
private JButton witnessButton;
private JButton evaluateFixpointButton;
private JLabel fixpointDeltaDesc;
private JTextField fixpointDeltaInput;
private JScrollPane fixpointResultScroll;
private JTextArea fixpointResult;



//TODO implement tips from https://stackoverflow.com/questions/62875613/cannot-refer-to-the-non-final-local-variable-display-defined-in-an-enclosing-sco
//TODO make better input descriptions on hover
//TODO full log checkbox and shorten normal output

	public WPCalculatorView() {
		frame = new JFrame("WP-Calculator");	
	    
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		
		cPanel = new JPanel();
		cPanel.setLayout(new BoxLayout(cPanel, BoxLayout.LINE_AXIS));
		fPanel = new JPanel();
		fPanel.setLayout(new BoxLayout(fPanel, BoxLayout.LINE_AXIS));

		
		descPanel = new JPanel();
		descPanel.setLayout(new BoxLayout(descPanel, BoxLayout.PAGE_AXIS));
		inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.PAGE_AXIS));
		
	    cDesc = new JLabel("Input the Program (C) here:");
	    cDesc.setBounds(5,0,170, 20);
	    cInput = new JTextField("while(c=1){{x=x+1}[1/2]{c=0}}");
	    cInput.setBounds(5,20,170, 20);
	    
	    
	    fDesc = new JLabel("Input the postexpectation (f) here:");
	    fDesc.setBounds(200,0,200, 20);
		fInput = new JTextField("x");
		fInput.setBounds(200,20,200, 20);
		
	    cPanel.add(cDesc);
	    cPanel.add(cInput);
	    fPanel.add(fDesc);
	    fPanel.add(fInput);
	    
	   /* descPanel.add(inputPanel);
	    inputPanel.add(cPanel);
	    inputPanel.add(fPanel);
	    panel.add(descPanel);
	    panel.add(inputPanel);
	    */
	    restrictionDesc = new JLabel("Input the restriction (k) here:");
	    restrictionDesc.setBounds(430,0,200, 20);
	    restrictionField = new JTextField("2");
	    restrictionField.setBounds(430,20,200, 20);
	    
	    iterationDesc = new JLabel("Input the iteration count here:");
	    iterationDesc.setBounds(430,40,200, 20);
	    iterationField = new JTextField("10");
	    iterationField.setBounds(430,60,200, 20);
	    
	    usedVarsDesc = new JLabel("Enter all used variables (e.g. xyz) ");
	    usedVarsDesc.setVisible(false);
	    usedVarsDesc.setBounds(500,80,200, 20);
	    usedVars = new JTextField("xc");
	    usedVars.setVisible(false);
	    usedVars.setBounds(500,100,200, 20);
	    
	    deltaDesc = new JLabel("Input delta (fixpoint iteration stop) here:");
	    deltaDesc.setBounds(500,120,400, 20);
	    deltaDesc.setVisible(false);
		deltaInput = new JTextField("0.01");
	    deltaInput.setVisible(false);
		deltaInput.setBounds(500,140,200, 20);

	    calcButton = new JButton("Calculate!");
	    calcButton.setBounds(5,100,150, 40);
	    
	    allSigmaIteration.setBounds(200,100,300, 50);
	    
	    examineFixpointButton = new JButton("Examine Fixpoints");
	    examineFixpointButton.setBounds(650,20,200, 20);
	    examineFixpointButton.setVisible(false);
	    
	    frame.add(examineFixpointButton);
	    frame.add(allSigmaIteration); 
	    frame.add(cDesc);
	    frame.add(cInput);
	    frame.add(fDesc);
	    frame.add(fInput);
	    frame.add(restrictionDesc);
	    frame.add(restrictionField);
	    frame.add(iterationDesc);
	    frame.add(iterationField);
	    frame.add(usedVars);
	    frame.add(usedVarsDesc);
	    frame.add(deltaDesc);
	    frame.add(deltaInput);
	    
	    frame.add(calcButton);
	    
	    result = new JTextArea();
	    scroll = new JScrollPane(result);
	    scroll.setBounds(10,200 ,800, 600); 
	    result.setEditable(false);
	    
	    frame.getContentPane().add(scroll);
	    
	    frame.add(panel);
	    frame.pack();
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setSize(1300,900);
	    frame.setLayout(null); 
	    frame.setVisible(true);
	    
	    allSigmaIteration.addActionListener(new ActionListener(){  
	    	public void actionPerformed(ActionEvent e){  
	    		if (allSigmaIteration.isSelected()) {
	    	    	usedVars.setVisible(true);
	    	    	usedVarsDesc.setVisible(true);
	    	    	deltaInput.setVisible(true);
	    	    	deltaDesc.setVisible(true);
	    	    }else {
	    	    	usedVars.setVisible(false);
	    	    	usedVarsDesc.setVisible(false);
	    	    	deltaInput.setVisible(false);
	    	    	deltaDesc.setVisible(false);
	    	    }
    	   }  
	    });
	    
	    calcButton.addActionListener(new ActionListener(){  
	    	//TODO important: catch empty allsigma textboxes and set defaults; currently breaks
	    	public void actionPerformed(ActionEvent e){  
	    		
	    		result.setText(""); 	
	    		
	    		if(restrictionField.getText().isEmpty()) {
	    	    	//throw exception;
	    			//TODO output warning and set default case
	    			result.setText("You have to set a restriction for the variables!");
	    	    	return;
	    	    } 
	    		setRestriction(Double.parseDouble(restrictionField.getText()));
	    		String calcResult = "";
	    		double start = System.currentTimeMillis();
    			if(!iterationField.getText().isEmpty()) {
		    		setIterationCount(Integer.parseInt(iterationField.getText()));
	    	    }else {
	    	    	if (allSigmaIteration.isSelected()) {
	    	    		//TODO log everything
		    	    	setIterationCount(Double.POSITIVE_INFINITY);
	    	    	}else {
	    	    		//default case 
	    	    		//TODO log everything
	    	    		setIterationCount(10);
	    	    	}
	    	    }
    			mainCalculator.fillAllSigma(usedVars.getText());
    	    	calcResult = mainCalculator.calculation(mainCalculator.wp(cInput.getText(),fInput.getText())); 

	    		double end = System.currentTimeMillis();
	    		result.setText(result.getText() + "\n\n" + "Calculation Time: " + (end - start)/1000 + "s");
	    	    result.setText(result.getText() + "\n" + "Result: " + calcResult);
	    	    if(cInput.getText().contains("while(")) {
		    	    examineFixpointButton.setVisible(true);
	    	    }
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

	public double getIterationCount() {
		return iterationCount;
	}

	public void setIterationCount(double iterationCount) {
		this.iterationCount = iterationCount;
	}
	
	public JCheckBox getAllSigmaIteration() {
		return allSigmaIteration;
	}

	public void setAllSigmaIteration(JCheckBox allSigmaIteration) {
		this.allSigmaIteration = allSigmaIteration;
	}
	
	public JTextField getDeltaInput() {
		return deltaInput;
	}

	public void setDeltaInput(JTextField deltaInput) {
		this.deltaInput = deltaInput;
	}

	public void linkCalculator(WPCalculator mainCalculator) {
		this.mainCalculator = mainCalculator;
	}
	
}
