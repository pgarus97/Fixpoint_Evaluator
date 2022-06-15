package prototype;

public class Main {

	public static void main(String[] args) {
		WPCalculator mainCalculator = new WPCalculator();
		WPCalculatorView mainView = new WPCalculatorView();
		WPCalculatorAllSigma allSigmaCalculator = new WPCalculatorAllSigma();

		mainCalculator.linkView(mainView);
		allSigmaCalculator.linkView(mainView);
		mainView.linkCalculator(mainCalculator);
		mainView.linkAllSigmaCalculator(allSigmaCalculator);
	}

}
