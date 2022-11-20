package controller;

import java.util.LinkedHashSet;

/*
 * Interface for structure of MainController
 * Information about the single functions can be found in the individual implementations
 */

public interface ControllerHandler {

	void clearWPCache();
	void saveWPCache();
	void loadWPCache();
	boolean prepareCalculationModel(String restriction, String iterationCount, int iterationSelection, String deltaInput);
	void output(String string, int logLevel, int recursionDepth);
	void output(String string, int logLevel);
	String wp(String C, String f, boolean sigmaForwarding);
	String getLFP(String currentWhileTerm);
	LinkedHashSet<String> evaluateFixpoint(String currentWhileTerm, String text, String text2);
	String createAllSigmaFixpoint(String currentWhileTerms);
	boolean isConverted(String currentWhileTerm);
	String startWitnessProcess(String C, String f, String information, String placeholder, LinkedHashSet<String> reductionSet);
	void setIterationSelection(int iterationSelection);
	String automaticReduction(String C, String f, String witness, LinkedHashSet<String> reductionSet);

}
