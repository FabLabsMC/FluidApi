package io.github.fablabsmc.fablabs.api.fluid.v1;

import io.github.fablabsmc.fablabs.api.fluid.v1.math.MixedNumber;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// TODO: Split up each test into it's own method rather than have several tests, unless necessary
public class MixedNumberTest {
	@Test
	@DisplayName("Test Signs")
	public void testSign() {
		Assertions.assertEquals(MixedNumber.of(0, 2, 5), MixedNumber.of(0, -2, -5));
		Assertions.assertEquals(MixedNumber.of(0, -2, 5), MixedNumber.of(0, 2, -5));
	}

	@Test
	@DisplayName("Test Simplification")
	public void testSimplification() {
		Assertions.assertEquals(MixedNumber.ofValidDenominator(0, 5, 5), MixedNumber.ofValidDenominator(0, 47, 47));
		Assertions.assertEquals(MixedNumber.ofWhole(2), MixedNumber.ofValidDenominator(0, 4, 2));
		Assertions.assertEquals(MixedNumber.ZERO, MixedNumber.ofValidDenominator(0, 0, 234));
		Assertions.assertEquals(MixedNumber.ofValidDenominator(0, 7, 10), MixedNumber.ofValidDenominator(0, 42, 60));
	}

	@Test
	@DisplayName("Bad Inverse Test")
	public void badInverseTest() {
		Assertions.assertThrows(ArithmeticException.class, MixedNumber.ZERO::inverse);
		Assertions.assertThrows(ArithmeticException.class, () -> MixedNumber.ofWhole(25).divide(MixedNumber.ofValidDenominator(0, 0, 23)));
	}

	@Test
	@DisplayName("General tests")
	public void generalTest() {
		MixedNumber eightBottles = MixedNumber.ofThirds(8);
		MixedNumber fiveIngots = MixedNumber.ofNinths(5);

		Assertions.assertEquals(MixedNumber.ofNinths(29), MixedNumber.add(eightBottles, fiveIngots)); // 8/3 + 5/9
		Assertions.assertEquals(MixedNumber.ofNinths(19), MixedNumber.subtract(eightBottles, fiveIngots)); // 8/3 - 5/9
		Assertions.assertEquals(MixedNumber.multiply(eightBottles, fiveIngots), MixedNumber.ofValidDenominator(0, 40, 27)); // 8/3 * 5/9
		Assertions.assertEquals(MixedNumber.ofValidDenominator(0, 72, 15), MixedNumber.divide(eightBottles, fiveIngots)); // 8/3 / 5/9
	}

	@Test
	@DisplayName("Test multiplication")
	public void testMultiply() {
		Assertions.assertEquals(MixedNumber.ofValidDenominator(0, 7, 8), MixedNumber.multiply(MixedNumber.ofValidDenominator(0, 8, 10), MixedNumber.ofValidDenominator(0, 7, 6), MixedNumber.ofValidDenominator(0, 5, 8), MixedNumber.ofValidDenominator(0, 6, 4)));
		Assertions.assertEquals(MixedNumber.ofValidDenominator(0, -7, 8), MixedNumber.multiply(MixedNumber.ofValidDenominator(0, -8, 10), MixedNumber.ofValidDenominator(0, 7, 6), MixedNumber.ofValidDenominator(0, -5, 8), MixedNumber.ofValidDenominator(0, -6, 4)));
	}

	@Test
	@DisplayName("Test Floor with denominator")
	public void testFloorWithDenominator() {
		// 4.75 <- 4.8
		Assertions.assertEquals(MixedNumber.of(0, 19, 4), MixedNumber.of(0, 72, 15).floorWithDenominator(4));
		// 1.4 <- 40/27 (1.4814..)
		Assertions.assertEquals(MixedNumber.of(0, 14, 10), MixedNumber.of(0, 40, 27).floorWithDenominator(10));
	}
}
