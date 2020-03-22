package io.github.fablabsmc.fablabs.api.fluidstack.v1;

import static io.github.fablabsmc.fablabs.api.fluidstack.v1.Fraction.multiply;
import static io.github.fablabsmc.fablabs.api.fluidstack.v1.Fraction.ofValidDenominator;
import static io.github.fablabsmc.fablabs.api.fluidstack.v1.Fraction.ofWhole;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FractionTest {

	@Test
	public void testSimplification() {
		Assertions.assertEquals(ofValidDenominator(5, 5), ofValidDenominator(47, 47));
		Assertions.assertEquals(ofWhole(2), ofValidDenominator(4, 2));
		Assertions.assertEquals(Fraction.ZERO, ofValidDenominator(0, 234));
		Assertions.assertEquals(ofValidDenominator(7, 10), ofValidDenominator(42, 60));
	}

	@Test
	public void badInverseTest() {
		Assertions.assertThrows(ArithmeticException.class, Fraction.ZERO::inverse);
		Assertions.assertThrows(ArithmeticException.class, () -> ofWhole(25).divide(ofValidDenominator(0, 23)));
	}

	@Test
	public void generalTest() {
		Fraction eightBottles = Fraction.ofThirds(8);
		Fraction fiveIngots = Fraction.ofNinths(5);

		Assertions.assertEquals(Fraction.ofNinths(29), Fraction.add(eightBottles, fiveIngots)); // 8/3 + 5/9
		Assertions.assertEquals(Fraction.ofNinths(19), Fraction.subtract(eightBottles, fiveIngots)); // 8/3 - 5/9
		Assertions.assertEquals(multiply(eightBottles, fiveIngots), Fraction.ofValidDenominator(40, 27)); // 8/3 * 5/9
		Assertions.assertEquals(ofValidDenominator(72, 15), Fraction.divide(eightBottles, fiveIngots)); // 8/3 / 5/9
	}

	@Test
	public void testMultiply() {
		Assertions.assertEquals(ofValidDenominator(7, 8), multiply(ofValidDenominator(8, 10), ofValidDenominator(7, 6), ofValidDenominator(5, 8), ofValidDenominator(6, 4)));
		Assertions.assertEquals(ofValidDenominator(-7, 8), multiply(ofValidDenominator(-8, 10), ofValidDenominator(7, 6), ofValidDenominator(-5, 8), ofValidDenominator(-6, 4)));
	}
}
