package prototype;


import org.junit.jupiter.api.Test;

class MainTest {

	@Test
	void test() {
		assert Main.calculation(Main.wp("x=5", "x^2")) == 25.0;	
		assert Main.calculation(Main.wp("x=5 ; if (x<5) {x=x+1} else {x=x-1}", "x^2")) == 16.0;
	}
}
