package io.github.fablabsmc.fablabs.api.fluid.v1;

import io.github.fablabsmc.fablabs.api.fluid.v1.math.Fraction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// TODO: Split up each test into it's own method rather than have several tests, unless necessary
public class FractionTest {
	@Test
	@DisplayName("Test Signs")
	public void testSign() {
		Assertions.assertEquals(Fraction.of(2, 5), Fraction.of(-2, -5));
		Assertions.assertEquals(Fraction.of(-2, 5), Fraction.of(2, -5));
	}

	@Test
	@DisplayName("Test Simplification")
	public void testSimplification() {
		Assertions.assertEquals(Fraction.ofValidDenominator(5, 5), Fraction.ofValidDenominator(47, 47));
		Assertions.assertEquals(Fraction.ofWhole(2), Fraction.ofValidDenominator(4, 2));
		Assertions.assertEquals(Fraction.ZERO, Fraction.ofValidDenominator(0, 234));
		Assertions.assertEquals(Fraction.ofValidDenominator(7, 10), Fraction.ofValidDenominator(42, 60));
	}

	@Test
	@DisplayName("Bad Inverse Test")
	public void badInverseTest() {
		Assertions.assertThrows(ArithmeticException.class, Fraction.ZERO::inverse);
		Assertions.assertThrows(ArithmeticException.class, () -> Fraction.ofWhole(25).divide(Fraction.ofValidDenominator(0, 23)));
	}

	@Test
	@DisplayName("General tests")
	public void generalTest() {
		Fraction eightBottles = Fraction.ofThirds(8);
		Fraction fiveIngots = Fraction.ofNinths(5);

		Assertions.assertEquals(Fraction.ofNinths(29), Fraction.add(eightBottles, fiveIngots)); // 8/3 + 5/9
		Assertions.assertEquals(Fraction.ofNinths(19), Fraction.subtract(eightBottles, fiveIngots)); // 8/3 - 5/9
		Assertions.assertEquals(Fraction.multiply(eightBottles, fiveIngots), Fraction.ofValidDenominator(40, 27)); // 8/3 * 5/9
		Assertions.assertEquals(Fraction.ofValidDenominator(72, 15), Fraction.divide(eightBottles, fiveIngots)); // 8/3 / 5/9
	}

	@Test
	@DisplayName("Test multiplication")
	public void testMultiply() {
		Assertions.assertEquals(Fraction.ofValidDenominator(7, 8), Fraction.multiply(Fraction.ofValidDenominator(8, 10), Fraction.ofValidDenominator(7, 6), Fraction.ofValidDenominator(5, 8), Fraction.ofValidDenominator(6, 4)));
		Assertions.assertEquals(Fraction.ofValidDenominator(-7, 8), Fraction.multiply(Fraction.ofValidDenominator(-8, 10), Fraction.ofValidDenominator(7, 6), Fraction.ofValidDenominator(-5, 8), Fraction.ofValidDenominator(-6, 4)));
	}

	@Test
	@DisplayName("Test Floor with denominator")
	public void testFloorWithDenominator() {
		// 4.75 <- 4.8
		Assertions.assertEquals(Fraction.of(19, 4), Fraction.of(72, 15).floorWithDenominator(4));
		// 1.4 <- 40/27 (1.4814..)
		Assertions.assertEquals(Fraction.of(14, 10), Fraction.of(40, 27).floorWithDenominator(10));
	}
}
