package io.github.fablabsmc.fablabs.api.fluidstack.v1;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.fabricmc.api.ModInitializer;

//TODO: move this into proper unit tests
public class FluidStacks implements ModInitializer {
	public static final String MODID = "fablabs-fluidstacks-v1";

	public static final Logger logger = LogManager.getLogger(MODID);

	@Override
	public void onInitialize() {
		Fraction two = Fraction.ofWhole(2);
		Fraction fourhalves = Fraction.of(4, 2);
		Fraction eightBottles = Fraction.ofThirds(8);
		Fraction fiveIngots = Fraction.ofNinths(5);
		Fraction sum = Fraction.add(eightBottles, fiveIngots);
		Fraction difference = Fraction.subtract(eightBottles, fiveIngots);
		Fraction product = Fraction.multiply(eightBottles, fiveIngots);
		Fraction quotient = Fraction.divide(eightBottles, fiveIngots);

		Fraction twentyNineNinths = Fraction.ofNinths(29).simplify(); // 8/3 + 5/9
		Fraction nineteenNinths = Fraction.ofNinths(19); // 8/3 - 5/9
		Fraction fortyTwentySevenths = Fraction.of(40, 27).simplify(); // 8/3 * 5/9
		Fraction seventyTwoFifteenths = Fraction.of(72, 15); // 8/3 / 5/9

		assert Fraction.areEqual(two, fourhalves);
		assert sum.equals(twentyNineNinths);
		assert difference.equals(nineteenNinths);
		assert product.equals(fortyTwentySevenths);
		assert quotient.equals(seventyTwoFifteenths);
	}
}
