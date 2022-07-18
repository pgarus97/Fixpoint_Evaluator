package mainClass;

import controller.MainController;
import model.WPCalculator;
import view.WPCalculatorView;

//main class

public class MainClass {

	public static void main(String[] args) {
		WPCalculator mainCalculator = new WPCalculator();
		WPCalculatorView mainView = new WPCalculatorView();
		MainController mainController= new MainController();
		
		mainController.link(mainView, mainCalculator);
		
	}
}
