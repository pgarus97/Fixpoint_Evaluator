package prototype;


import org.junit.jupiter.api.Test;

class MainTest {

	@Test
	void test() {
		assert Main.wp("x = 5", "x^2").equals("25.0");	}
}
