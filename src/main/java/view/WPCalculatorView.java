package view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import controller.ControllerHandler;

//main view

public class WPCalculatorView {
	

private ControllerHandler mainController;
private String currentWhileTerm;

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

private JLabel restrictionDesc;
private JTextField restrictionField;
private JLabel iterationDesc;
private JTextField iterationField;
private JLabel deltaDesc;
private JTextField deltaInput;
private JButton calcButton;
private JButton convertButton;
private JLabel convertDesc;
private JScrollPane scroll;
private JTextArea result;
private JToggleButton examineFixpointButton;

private JPanel iterationPanel;
private JCheckBox defaultIteration;
private JCheckBox upsideDown;
private JCheckBox directIteration;
private JCheckBox allSigmaIteration;
private JCheckBox sigmaForwarding;

private JPanel logPanel;
private JCheckBox fileLog;
private JCheckBox minimalLog;
private JCheckBox detailedLog;

private ArrayList<JToggleButton> whileLoops; 

private JScrollPane whileLoopScroll;
private JPanel whileLoopPanel;

private JPanel evaluationPanel;
private JPanel convertPanel;
private JButton lfpButton;
private JTextField witnessInput;
private JButton fixpointEvalButton;
private JLabel fixpointDeltaDesc;
private JTextField fixpointDeltaInput;

private JPanel cachePanel;
private JButton resetCache;
private JButton saveCache;
private JButton loadCache;


//TODO implement tips from https://stackoverflow.com/questions/62875613/cannot-refer-to-the-non-final-local-variable-display-defined-in-an-enclosing-sco
//TODO make better input descriptions on hover

	public WPCalculatorView() {
		
		/*
		 * GUI implementation
		 */
		
		frame = new JFrame("Fixpoint-Evaluator");	
	    
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
	    cInput.setToolTipText(cInput.getText());
	        
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
	    restrictionField = new JTextField("1");
	    restrictionField.setBounds(430,20,200, 20);
	    
	    iterationDesc = new JLabel("Input the iteration count here:");
	    iterationDesc.setBounds(450,40,200, 20);
	    iterationDesc.setVisible(false);
	    iterationField = new JTextField("");
	    iterationField.setBounds(450,60,200, 20);
	    iterationField.setVisible(false);

	    
	    deltaDesc = new JLabel("Input delta (fixpoint iteration stop) here:");
	    deltaDesc.setBounds(450,80,200, 20);
	    deltaDesc.setVisible(false);
		deltaInput = new JTextField("0.001");
		deltaInput.setBounds(450,100,200, 20);
	    deltaInput.setVisible(false);


	    calcButton = new JButton("Calculate!");
	    calcButton.setBounds(5,100,150, 40);
	    
	    convertDesc = new JLabel("Convert to allSigma format!");
	    convertDesc.setBounds(500,120,400, 20);
	    convertButton = new JButton("Convert!");
	    convertButton.setBounds(500,140,200, 20);
	    
	    iterationPanel = new JPanel();
	    iterationPanel.setBounds(190,50,220, 120);
		iterationPanel.setLayout(new BoxLayout(iterationPanel, BoxLayout.PAGE_AXIS));
	    
	    allSigmaIteration = new JCheckBox("Enable all-sigma fixpoint iteration.");

	    directIteration = new JCheckBox("Enable direct fixpoint iteration.");
	    
	    defaultIteration = new JCheckBox("Enable default fixpoint iteration.");
	    defaultIteration.setSelected(true);
	    
	    upsideDown = new JCheckBox("Enable Upside-Down method.");
	    
	    sigmaForwarding = new JCheckBox("Enable sigma-forwarding.");
	    
	    iterationPanel.add(defaultIteration);
	    iterationPanel.add(allSigmaIteration);
	    iterationPanel.add(directIteration);
	    iterationPanel.add(upsideDown);
	    iterationPanel.add(sigmaForwarding);
	    iterationPanel.setVisible(true);
	    
	    logPanel = new JPanel();
	    logPanel.setBounds(500,810 ,400, 20);
		logPanel.setLayout(new BoxLayout(logPanel, BoxLayout.LINE_AXIS));
	    
	    fileLog = new JCheckBox("Write Log to File");
	    
	    minimalLog = new JCheckBox("Minimal Log");
	    minimalLog.setSelected(true);
	    
	    detailedLog = new JCheckBox("Detailed Log");
	    	    
	    logPanel.add(fileLog);
	    logPanel.add(Box.createRigidArea(new Dimension(10, 0)));
	    logPanel.add(new JLabel("|"));
	    logPanel.add(Box.createRigidArea(new Dimension(10, 0)));
	    logPanel.add(minimalLog);
	    logPanel.add(detailedLog);
	    logPanel.setVisible(true);
	    
	    examineFixpointButton = new JToggleButton("Examine Fixpoints");
	    examineFixpointButton.setBounds(675,20,200, 40);
	    examineFixpointButton.setVisible(false);
	    
	    whileLoopPanel = new JPanel();
	    whileLoopPanel.setLayout(new BoxLayout(whileLoopPanel, BoxLayout.PAGE_AXIS));
	    whileLoopPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	    whileLoops = new ArrayList<JToggleButton>();
	    whileLoopScroll = new JScrollPane(whileLoopPanel);
	    whileLoopScroll.setBounds(1000,50,250, 250);
	    whileLoopScroll.setVisible(false);
	    
	    //evaluation panel
	    fixpointDeltaDesc = new JLabel("Input delta here:");
	    fixpointDeltaInput = new JTextField("0.1");
	    //TODO can change the preferred size if we put it in another panel as the boxlayout makes all components same size.
	    fixpointDeltaInput.setPreferredSize(new Dimension (500,20));
	    fixpointDeltaInput.setMaximumSize(fixpointDeltaInput.getPreferredSize());
	    
	    lfpButton = new JButton("Select LFP");
	    witnessInput = new JTextField("Witness XY");
	    witnessInput.setToolTipText(witnessInput.getText());
	    witnessInput.setPreferredSize(new Dimension (500,20));
	    witnessInput.setMaximumSize(witnessInput.getPreferredSize());
	    fixpointEvalButton = new JButton("Evaluate Fixpoint");
	    evaluationPanel = new JPanel();
	    evaluationPanel.setBounds(1000,300,250, 250);
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
	    evaluationPanel.add(fixpointEvalButton);
	    evaluationPanel.add(Box.createRigidArea(new Dimension(0, 10)));
	    evaluationPanel.setVisible(false);
	    convertPanel = new JPanel();
	    convertPanel.setBounds(1000,300,250, 250);
	    convertPanel.setLayout(new BoxLayout(convertPanel, BoxLayout.PAGE_AXIS)); 
	    convertPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	    convertPanel.add(convertDesc);
	    convertPanel.add(convertButton);
	    convertPanel.setVisible(false);
	    
	    //cache panel
	    cachePanel = new JPanel();
	    cachePanel.setBounds(10,810 ,400, 20);
		cachePanel.setLayout(new BoxLayout(cachePanel, BoxLayout.LINE_AXIS));
	    resetCache = new JButton("Reset Cache");
	    saveCache = new JButton("Save Cache");
	    loadCache = new JButton("Load Cache");
	    cachePanel.add(resetCache);
	    cachePanel.add(saveCache);
	    cachePanel.add(loadCache);
	    
	    result = new JTextArea();
	    scroll = new JScrollPane(result);
	    scroll.setBounds(10,200 ,950, 600); 
	    result.setEditable(false);
	    
	    /*
	     * add all components to mainframe
	     */
	   
	    frame.add(cachePanel);
	    frame.add(evaluationPanel);
	    frame.add(convertPanel);
	    frame.add(iterationPanel);
	    frame.add(logPanel);
	    
	    frame.add(examineFixpointButton);
	    frame.add(cDesc);
	    frame.add(cInput);
	    frame.add(fDesc);
	    frame.add(fInput);
	    frame.add(restrictionDesc);
	    frame.add(restrictionField);
	    frame.add(iterationDesc);
	    frame.add(iterationField);
	    frame.add(deltaDesc);
	    frame.add(deltaInput);
		frame.add(whileLoopScroll);
	    frame.add(calcButton);

	    frame.getContentPane().add(scroll);
	    
	    frame.add(panel);
	    frame.pack();
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setSize(1300,900);
	    frame.setLayout(null); 
	    frame.setVisible(true);
	    
	    /*
	     * button listeners
	     */
	    detailedLog.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e){ 
	    		minimalLog.setSelected(false);
 		    }
	    });
	    
	    minimalLog.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e){   		
	    			detailedLog.setSelected(false);
	    	}
	    });
	    
	    resetCache.addActionListener(new ActionListener(){  
	    	public void actionPerformed(ActionEvent e){ 
	    		mainController.clearWPCache();
	    		prepareCalculationView();
    	   }  
	    });  
	    
	    saveCache.addActionListener(new ActionListener(){  
	    	public void actionPerformed(ActionEvent e){ 
	    		mainController.saveWPCache();
    	   }  
	    });
	    
	    loadCache.addActionListener(new ActionListener(){  
	    	public void actionPerformed(ActionEvent e){ 
	    		mainController.loadWPCache();
	    		prepareCalculationView();
    	   }  
	    });

	    allSigmaIteration.addActionListener(new ActionListener(){  
	    	public void actionPerformed(ActionEvent e){ 
	    		if (allSigmaIteration.isSelected()) {
	    			deltaInput.setText("0.001");
	    			deltaInput.setVisible(true);
	    	    	deltaDesc.setText("Enter delta for iteration stop.");
	    	    	deltaDesc.setVisible(true);
	    	    	iterationDesc.setText("Enter minimal iteration count.");
	    	    	iterationDesc.setVisible(true);
	    	    	iterationField.setText("5");
	    	    	iterationField.setVisible(true);
	    	    	directIteration.setSelected(false);
	    	    	defaultIteration.setSelected(false);
	    	    	upsideDown.setSelected(false);
	    	    }else {
	    	    	deltaInput.setVisible(false);
	    	    	deltaDesc.setVisible(false);
	    	    	iterationDesc.setVisible(false);
	    	    	iterationField.setVisible(false);
	    	    }
    	   }  
	    });
	    
	    defaultIteration.addActionListener(new ActionListener(){  
	    	public void actionPerformed(ActionEvent e){ 
	    		if (defaultIteration.isSelected()) {
	    	    	deltaInput.setVisible(false);
	    	    	deltaDesc.setVisible(false);
	    	    	iterationDesc.setVisible(false);
	    	    	iterationField.setVisible(false);
	    	    	directIteration.setSelected(false);
	    	    	allSigmaIteration.setSelected(false);
	    	    	upsideDown.setSelected(false);
	    		}
    	   }  
	    });
	    
	    directIteration.addActionListener(new ActionListener(){  
	    	public void actionPerformed(ActionEvent e){ 
	    		if (directIteration.isSelected()) {
	    	    	deltaInput.setVisible(false);
	    	    	deltaDesc.setVisible(false);
	    	    	iterationDesc.setText("Enter maximal iteration count.");
	    	    	iterationDesc.setVisible(true);
	    	    	iterationField.setText("10");
	    	    	iterationField.setVisible(true);
	    	    	//TODO change iterationDesc on press
	    	    	defaultIteration.setSelected(false);
	    	    	allSigmaIteration.setSelected(false);
	    	    	upsideDown.setSelected(false);
	    	    }else {
	    	    	iterationDesc.setVisible(false);
	    	    	iterationField.setVisible(false);
	    	    }
    	   }  
	    });
    
	    upsideDown.addActionListener(new ActionListener(){  
	    	public void actionPerformed(ActionEvent e){ 
	    		if (upsideDown.isSelected()) {
	    			deltaInput.setText("0.1");
	    	    	deltaInput.setVisible(true);
	    	    	deltaDesc.setText("Enter delta for reducing states.");
	    	    	deltaDesc.setVisible(true);
	    	    	iterationDesc.setVisible(false);
	    	    	iterationField.setVisible(false);
	    	    	defaultIteration.setSelected(false);
	    	    	allSigmaIteration.setSelected(false);
	    	    	directIteration.setSelected(false);
	    	    }else {
	    	    	deltaInput.setVisible(false);
	    	    	deltaDesc.setVisible(false);
	    	    }
    	   }  
	    });
	    
	    examineFixpointButton.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent event) {
                if (event.getStateChange() == ItemEvent.SELECTED) {
	    			whileLoopScroll.setVisible(true);	
                } else {
	    			whileLoopScroll.setVisible(false);
	    			evaluationPanel.setVisible(false);
	    	    	convertPanel.setVisible(false);
	    			for(JToggleButton whileButton : whileLoops) {
	            		whileButton.setSelected(false);
	            	}
                }
            }
        });
	    
	    calcButton.addActionListener(new ActionListener(){  
	    	//TODO log in real time somehow => https://docs.oracle.com/javase/tutorial/uiswing/concurrency/index.html#:~:text=Careful%20use%20of%20concurrency%20is%20particularly%20important%20to,must%20learn%20how%20the%20Swing%20framework%20employs%20threads.
	    	public void actionPerformed(ActionEvent e){
	    		if(mainController.prepareCalculationModel(restrictionField.getText(),iterationField.getText(),getIterationSelection(),deltaInput.getText()) == false) {
	    			return;
	    		}
	    		prepareCalculationView();
	    		mainController.wp(cInput.getText(), fInput.getText(), sigmaForwarding.isSelected());    	    
    	   }  
	    }); 
	    
	    convertButton.addActionListener(new ActionListener(){  
	    	public void actionPerformed(ActionEvent e){
	    		if(!mainController.createAllSigmaFixpoint(currentWhileTerm).isEmpty()) {
	    	    	convertPanel.setVisible(false);
		    		evaluationPanel.setVisible(true);
	    		}
	    	}  
	    });
	    
	    lfpButton.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent e){
	    			witnessInput.setText(mainController.getLFP(currentWhileTerm));
	    	}
		}); 
	    
	    witnessInput.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    	    witnessInput.setToolTipText(witnessInput.getText());
	    	}
	    });
	    
	    cInput.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    	    cInput.setToolTipText(cInput.getText());
	    	}
	    });
	    
	    fixpointEvalButton.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		mainController.evaluateFixpoint(currentWhileTerm, witnessInput.getText(), fixpointDeltaInput.getText());
	    	}
	    });
	}
	
	public ItemListener whileLoopToggle = new ItemListener() {
        public void itemStateChanged(ItemEvent event) {
            if (event.getStateChange() == ItemEvent.SELECTED) {
    		    JToggleButton selectedWhileButton = (JToggleButton) event.getSource();
            	for(JToggleButton whileButton : whileLoops) {
            		if(!whileButton.equals(selectedWhileButton)) {
            			whileButton.setSelected(false);
            		}
            	}
    		    currentWhileTerm = selectedWhileButton.getText();
            	if(allSigmaIteration.isSelected()) {
            		evaluationPanel.setVisible(true);
            	}else {
            		if(mainController.isConverted(currentWhileTerm)) {
                		evaluationPanel.setVisible(true);
            		}else {
            			convertPanel.setVisible(true);
            		}
            	}
            	
            } else {
            	currentWhileTerm = "";
            	evaluationPanel.setVisible(false);
    	    	convertPanel.setVisible(false);
            }
        }
    };

    public String createWitnessDialogue(String C, String f, String information, String placeholder) {
    	    	
    	Object[] options1 = { "Test This Witness", "Try Kleene Iteration", "Exit" };
    	Object[] options2 = { "Test This Witness", "Try Kleene Iteration", "Try Automatic Reduction", "Exit" };

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

		panel.add(new JLabel("Currently Evaluating: " + C +" ("+f+")"));
		panel.add(Box.createRigidArea(new Dimension(0, 5)));
		panel.add(new JLabel(information));
		panel.add(Box.createRigidArea(new Dimension(0, 5)));
		panel.add(new JLabel("Input a Witness: "));
		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		JTextField textField = new JTextField(placeholder);
		panel.add(textField);
		
		int result = JOptionPane.showOptionDialog(null, panel, "Witness Input",
		        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
		        null, options1, null);
		switch(result) {
			case 0:
				return textField.getText();
			case 1:
				mainController.setIterationSelection(0);
				String wpResult = mainController.wp(C, f, false);
				mainController.setIterationSelection(3);
				return wpResult;
			case 2: 
				return null;
			default:
				return null;
		}
		
    	/*
    	String witness = (String)JOptionPane.showInputDialog(
                frame,
                "Currently evaluating: " + C +" ("+f+")" + "\n\n" +
                information + "\n\n" + 
                "Input a witness: ",
                "Witness Input",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                placeholder);
    	
				return witness;
				//Button for kleene iteration alternative //TODO needs more custom JOptionPane, maybe as variable and not only for the string
				//Button for automatic reduction of states that still need to be reduced
				 * */
				 
    }
    
    public void prepareEvaluationView(ArrayList<String> modelLoops) {
    	examineFixpointButton.setVisible(true);
	    int counter = 0;
		for (String loop: modelLoops) {	
			JToggleButton tempButton = new JToggleButton(loop);
			tempButton.addItemListener(whileLoopToggle);
			tempButton.setToolTipText(tempButton.getText());
			whileLoops.add(tempButton);
			whileLoopPanel.add(whileLoops.get(counter));
			whileLoopPanel.add(Box.createRigidArea(new Dimension(0, 10)));
			counter++;
		}		
	}
    
	public void prepareCalculationView() {
	    evaluationPanel.setVisible(false);
		examineFixpointButton.setVisible(false);
		whileLoopScroll.setVisible(false);
		examineFixpointButton.setSelected(false);
		whileLoopPanel.removeAll();
		whileLoops.clear();
    	convertPanel.setVisible(false);
		updateFrame();
		
	}
	
	public void updateFrame() {
		frame.validate();
		frame.repaint();
	}
	/*
	 * getter & setter methods
	 */
	
	public JTextArea getResult() {
		return result;
	}

	public void setResult(JTextArea result) {
		this.result = result;
	}
	
	public JCheckBox getAllSigmaIteration() {
		return allSigmaIteration;
	}
	
	public JCheckBox getDefaultIteration() {
		return defaultIteration;
	}
	
	public JCheckBox getDirectIteration() {
		return directIteration;
	}
	
	public JCheckBox getUpsideDown() {
		return upsideDown;
	}
	
	public JCheckBox getMinimalLog() {
		return minimalLog;
	}
	
	public JCheckBox getDetailedLog() {
		return detailedLog;
	}
	
	public JCheckBox getFileLog() {
		return fileLog;
	}
	
	public boolean getLogToFile() {
		if(fileLog.isSelected()) {
			return true;
		}else {
			return false;
		}
	}
	
	public int getLogLevel() {
		if(minimalLog.isSelected()) {
			return 1;
		}else if(detailedLog.isSelected()){
			return 2;
		}
		return 1; //default case
	}

	public int getIterationSelection() {
		if(defaultIteration.isSelected()) {
			return 0;
		} else if(allSigmaIteration.isSelected()) {
			return 1;
		}else if(directIteration.isSelected()) {
			return 2;
		}else if(upsideDown.isSelected()) {
			return 3;
		}
		//default case
		return 0;
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
	
	public String getCurrentWhileTerm() {
		return currentWhileTerm;
	}

	public void setCurrentWhileTerm(String currentWhileTerm) {
		this.currentWhileTerm = currentWhileTerm;
	}
	
	public void setHandler(ControllerHandler controller) {
		mainController = controller;
	}

	public ControllerHandler getHandler() {
		return mainController;
	}
	
	public ArrayList<JToggleButton> getWhileLoops() {
		return whileLoops;
	}
	
	public void setWhileLoops(ArrayList<JToggleButton> whileLoops) {
		this.whileLoops = whileLoops;
	}
	
	public void clearResult() {
		result.setText("");
		result.validate();
		result.repaint();
	}
	
}
