package prototype;


import org.junit.jupiter.api.Test;

class MainTest {

	@Test
	void test() {
		assert Main.calculation(Main.wp("x=5", "x^2")) == 25.0;
		assert Main.calculation(Main.wp("x=5 ; x=10", "x^2")) == 100.0;	
		assert Main.calculation(Main.wp("x=5 ; x=10 ; y=2", "x^2")) == 100.0;	
		assert Main.calculation(Main.wp("x=5 ; y=10", "x^2")) == 25.0;	//does not work yet as only one variable supported so far
		assert Main.calculation(Main.wp("x=5 ; if (x<5) {x=x+1} else {x=x-1}", "x^2")) == 16.0;
		// assert Main.calculation(Main.wp("if (x<5) {x=x+1} else {x=x-1}", "x^2")) == ?; What would this compute to?
	}
}
