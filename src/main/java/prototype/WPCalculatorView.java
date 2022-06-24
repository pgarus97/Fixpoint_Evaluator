package prototype;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
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

private ArrayList<JButton> whileLoops; 
private JScrollPane whileLoopScroll;
private JPanel whileLoopPanel;

private JPanel evaluationPanel;
private JButton lfpButton;
private JTextField witnessInput;
private JButton witnessButton;
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
	    examineFixpointButton.setBounds(650,20,200, 40);
	    examineFixpointButton.setVisible(false);
	    
	    whileLoopPanel = new JPanel();
	    whileLoopPanel.setLayout(new BoxLayout(whileLoopPanel, BoxLayout.PAGE_AXIS));
	    whileLoopPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	    whileLoops = new ArrayList<JButton>();
	    whileLoopScroll = new JScrollPane(whileLoopPanel);
	    whileLoopScroll.setBounds(900,50,250, 250);
	    whileLoopScroll.setVisible(false);
	    
	    
	    fixpointDeltaDesc = new JLabel("Input delta here:");
	    fixpointDeltaInput = new JTextField("0.1");
	    //TODO can change the preferred size if we put it in another panel as the boxlayout makes all components same size.
	    fixpointDeltaInput.setPreferredSize(new Dimension (500,20));
	    fixpointDeltaInput.setMaximumSize(fixpointDeltaInput.getPreferredSize());

	    lfpButton = new JButton("LFP");
	    witnessInput = new JTextField("Witness XY");
	    witnessInput.setPreferredSize(new Dimension (500,20));
	    witnessInput.setMaximumSize(witnessInput.getPreferredSize());
	    witnessButton = new JButton("Witness");
	    evaluationPanel = new JPanel();
	    evaluationPanel.setBounds(900,300,250, 250);
	    evaluationPanel.setLayout(new BoxLayout(evaluationPanel, BoxLayout.PAGE_AXIS));
	    evaluationPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	    evaluationPanel.add(fixpointDeltaDesc);
	    evaluationPanel.add(Box.createRigidArea(new Dimension(0, 10)));
	    evaluationPanel.add(fixpointDeltaInput);
	    evaluationPanel.add(Box.createRigidArea(new Dimension(0, 10)));
	    evaluationPanel.add(lfpButton);
	    evaluationPanel.add(Box.createRigidArea(new Dimension(0, 10)));
	    evaluationPanel.add(witnessInput);
	    evaluationPanel.add(Box.createRigidArea(new Dimension(0, 10)));
	    evaluationPanel.add(witnessButton);
	    evaluationPanel.add(Box.createRigidArea(new Dimension(0, 10)));
	    evaluationPanel.setVisible(false);
	    
	    frame.add(evaluationPanel);



	    
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
	    //get contentpane
		frame.add(whileLoopScroll);
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
	    
	    /* For button hover
	     * button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                System.out.println("entered");
                label.setVisible(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                System.out.println("exited");
                label.setVisible(false);
            }
        });
	     */
	    
	    examineFixpointButton.addActionListener(new ActionListener(){  
	    	public void actionPerformed(ActionEvent e){
	    		whileLoops.clear();
	    		whileLoopPanel.removeAll();
	    		whileLoopScroll.setVisible(true);
	    		int counter = 0;
	    		for (String loop: mainCalculator.getWhileLoops()) {	
	    			whileLoops.add(new JButton(loop));
	    			whileLoops.get(counter).addActionListener(whileLoopAction);
	    			whileLoopPanel.add(whileLoops.get(counter));
	    			whileLoopPanel.add(Box.createRigidArea(new Dimension(0, 10)));
	    			counter++;
	    		}
    	   }  
	    });
	    
	    calcButton.addActionListener(new ActionListener(){  
	    	//TODO log in real time somehow => https://docs.oracle.com/javase/tutorial/uiswing/concurrency/index.html#:~:text=Careful%20use%20of%20concurrency%20is%20particularly%20important%20to,must%20learn%20how%20the%20Swing%20framework%20employs%20threads.
	    	public void actionPerformed(ActionEvent e){  
	    		if(calculationLog() == false) {
	    			return;
	    		}
	    		prepareCalculation();
	    		
	    		String calcResult = "";
	    		double start = System.currentTimeMillis();
	    		calcResult = mainCalculator.calculation(mainCalculator.wp(cInput.getText().replace(" ", ""),fInput.getText())); 
	    		double end = System.currentTimeMillis();
	    		
	    		result.append("\n\n" + "Calculation Time: " + (end - start)/1000 + "s");
	    	    result.append("\n" + "Result: " + calcResult);
	    	    if(cInput.getText().contains("while(")) {
		    	    examineFixpointButton.setVisible(true);
	    	    }
	    	    
    	   }  
	    }); 
	}
	
	public ActionListener whileLoopAction = new ActionListener() {

		public void actionPerformed(ActionEvent e) {
		    evaluationPanel.setVisible(true);
			//TODO write lfp of while into lfp button
		}
		
	};
	
	public void prepareCalculation() {
	    evaluationPanel.setVisible(false);
		examineFixpointButton.setVisible(false);
		whileLoopScroll.setVisible(false);
		mainCalculator.fillAllSigma(usedVars.getText());
		mainCalculator.flushWhileLoops();
		for(JButton whileButton : whileLoops) {
			frame.remove(whileButton);
		
		}
		whileLoops.clear();
		frame.validate();
		frame.repaint();
		
	}
	
	public boolean calculationLog() {
		result.setText("Information:");
		
		if(!restrictionField.getText().isEmpty()) {
			result.append("\n" + "Variable restriction set to {0,...," + restrictionField.getText() + "}.");
    		setRestriction(Double.parseDouble(restrictionField.getText()));
		}else {
			//default case
			result.append("\n" + "No restriction inputted. Set to default {0,...,1}.");
    		setRestriction(1);
		}
		if(!iterationField.getText().isEmpty()) {
			result.append("\n" + "Iteration count set to " + iterationField.getText() + ".");
    		setIterationCount(Integer.parseInt(iterationField.getText()));
	    }else {
	    	if (allSigmaIteration.isSelected()) {
    			result.append("\n" + "No iteration count inputted. Taking all sigma default: infinite iteration.");
    	    	setIterationCount(Double.POSITIVE_INFINITY);
	    	}else {
	    		//default case 
    			result.append("\n" + "No iteration count inputted. Taking default count = 10.");
	    		setIterationCount(10);
	    	}
	    }
		if (allSigmaIteration.isSelected()) {
			if(usedVars.getText().isEmpty()) {
    			result.append("\n\n" + "No used variables inputted! You need to input all variables in C.");
    			return false;
			}
			if(deltaInput.getText().isEmpty()) {
    			result.append("\n" + "No delta for the iteration inputted. Taking default delta = 0.001.");
    			deltaInput.setText("0.001");
			}
		}
		result.append( "\n\n" + "Calculating: wp["+cInput.getText()+"]("+fInput.getText()+")");
		return true;
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
