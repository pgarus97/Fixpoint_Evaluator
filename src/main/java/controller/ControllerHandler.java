package controller;

/*
 * Interface for structure of MainController
 * Information about the single functions can be found in the individual implementations
 */

public interface ControllerHandler {

	void clearFixpointCache();
	void saveFixpointCache();
	void loadFixpointCache();
	boolean prepareCalculationModel(String restriction, String iterationCount, boolean allSigma, String usedVars,
			String deltaInput);
	void output(String string);
	void wp(String C, String f, boolean sigmaForwarding);
	String getLFP(String currentWhileTerm);
	void evaluateFixpoint(String currentWhileTerm, String text, String text2);
	String createAllSigmaFixpoint(String currentWhileTerm, String usedVars);

}
