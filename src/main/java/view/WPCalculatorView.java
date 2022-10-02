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
//TODO full log checkbox and shorten normal output

	public WPCalculatorView() {
		
		/*
		 * GUI implementation
		 */
		
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
	    iterationDesc.setBounds(430,40,200, 20);
	    iterationField = new JTextField("");
	    iterationField.setBounds(430,60,200, 20);
	    
	    deltaDesc = new JLabel("Input delta (fixpoint iteration stop) here:");
	    deltaDesc.setBounds(500,80,200, 20);
		deltaInput = new JTextField("0.001");
		deltaInput.setBounds(500,100,200, 20);

	    calcButton = new JButton("Calculate!");
	    calcButton.setBounds(5,100,150, 40);
	    
	    convertDesc = new JLabel("Convert to allSigma format!");
	    convertDesc.setBounds(500,120,400, 20);
	    convertButton = new JButton("Convert!");
	    convertButton.setBounds(500,140,200, 20);
	    
	    allSigmaIteration = new JCheckBox("Enable all-sigma fixpoint iteration.");
	    allSigmaIteration.setBounds(200,100,300, 50);
	    allSigmaIteration.setSelected(true);
	    
	    sigmaForwarding = new JCheckBox("Enable sigma-forwarding.");
	    sigmaForwarding.setBounds(200,135,300, 50);
	    
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
	    examineFixpointButton.setBounds(650,20,200, 40);
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
	    frame.add(logPanel);
	    
	    frame.add(examineFixpointButton);
	    frame.add(allSigmaIteration); 
	    frame.add(sigmaForwarding); 
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
	    		mainController.clearFixpointCache();
	    		prepareCalculationView();
    	   }  
	    });  
	    
	    saveCache.addActionListener(new ActionListener(){  
	    	public void actionPerformed(ActionEvent e){ 
	    		mainController.saveFixpointCache();
    	   }  
	    });
	    
	    loadCache.addActionListener(new ActionListener(){  
	    	public void actionPerformed(ActionEvent e){ 
	    		mainController.loadFixpointCache();
	    		prepareCalculationView();
    	   }  
	    });

	    allSigmaIteration.addActionListener(new ActionListener(){  
	    	public void actionPerformed(ActionEvent e){ 
	    		if (allSigmaIteration.isSelected()) {
	    	    	deltaInput.setVisible(true);
	    	    	deltaDesc.setVisible(true);
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
	    		if(mainController.prepareCalculationModel(restrictionField.getText(),iterationField.getText(),allSigmaIteration.isSelected(),deltaInput.getText()) == false) {
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
	
	public void clearResult() {
		result.setText("");
		result.validate();
		result.repaint();
	}
	
}
