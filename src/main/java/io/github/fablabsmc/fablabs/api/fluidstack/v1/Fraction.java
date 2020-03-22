package io.github.fablabsmc.fablabs.api.fluidstack.v1;

import java.util.Objects;

import net.minecraft.nbt.CompoundTag;

public final class Fraction {
	public static final Fraction ZERO = ofWhole(0);
	public static final Fraction ONE = ofWhole(1);

	private final int numerator;
	private final int denominator;

	private Fraction(int numerator, int denominator) {
		this.numerator = numerator;
		this.denominator = denominator;
	}

	public static Fraction of(int numerator, int denominator) {
		return new Fraction(numerator, denominator);
	}

	//TODO: What shortcuts should we have, and how should we name them? This is in a Minecraft context, after all.
	public static Fraction ofWhole(int numerator) {
		return new Fraction(numerator, 1);
	}

	public static Fraction ofThirds(int numerator) {
		return new Fraction(numerator, 3);
	}

	public static Fraction ofNinths(int numerator) {
		return new Fraction(numerator, 9);
	}

	public static Fraction ofThousandths(int numerator) {
		return new Fraction(numerator, 1000);
	}

	public int getNumerator() {
		return numerator;
	}

	public int getDenominator() {
		return denominator;
	}

	public boolean isNegative() {
		return numerator < 0;
	}

	public boolean isPositive() {
		return numerator > 0;
	}

	public int signum() {
		return Integer.signum(numerator);
	}

	public double toDouble() {
		return (double) numerator / (double) denominator;
	}

	public Fraction inverse() {
		return of(denominator, numerator);
	}

	public Fraction add(Fraction other) {
		int commonMultiple = this.denominator * other.denominator;
		int leftNumerator = this.numerator * other.denominator;
		int rightNumerator = other.numerator * this.denominator;
		return of(leftNumerator + rightNumerator, commonMultiple);
	}

	public Fraction subtract(Fraction other) {
		int commonMultiple = this.denominator * other.denominator;
		int leftNumerator = this.numerator * other.denominator;
		int rightNumerator = other.numerator * this.denominator;
		return of(leftNumerator - rightNumerator, commonMultiple);
	}

	public Fraction multiply(Fraction other) {
		return of(this.numerator * other.numerator, this.denominator * other.denominator);
	}

	public Fraction divide(Fraction other) {
		return multiply(other.inverse());
	}

	public static Fraction add(Fraction... addends) {
		Fraction ret = ZERO;

		for (Fraction addend : addends) {
			ret = ret.add(addend);
		}

		return ret.simplify();
	}

	public static Fraction subtract(Fraction minuend, Fraction subtrahend) {
		return minuend.subtract(subtrahend);
	}

	public static Fraction multiply(Fraction... factors) {
		Fraction ret = ONE;

		for (Fraction factor : factors) {
			ret = ret.multiply(factor);
		}

		return ret.simplify();
	}

	public static Fraction divide(Fraction dividend, Fraction divisor) {
		return dividend.divide(divisor);
	}

	public Fraction simplify() {
		if (numerator == 0) return ZERO;
		if (numerator == denominator) return ONE;
		int gcd;

		if (numerator > denominator) {
			gcd = gcd(numerator, denominator);
		} else {
			gcd = gcd(denominator, numerator);
		}

		return of(numerator / gcd, denominator / gcd);
	}

	private int gcd(int larger, int smaller) {
		int currentLarger = larger;
		int currentSmaller = smaller;

		while (currentSmaller > 0) {
			int remainder = currentLarger % currentSmaller;
			currentLarger = currentSmaller;
			currentSmaller = remainder;
		}

		return currentLarger;
	}

	public static boolean areEqual(Fraction left, Fraction right) {
		return left.simplify().equals(right.simplify());
	}

	public int compareTo(Fraction other) {
		if (areEqual(this, other)) return 0;
		int leftNum = this.numerator * other.denominator;
		int rightNum = other.numerator * this.denominator;
		return Integer.compare(leftNum, rightNum);
	}

	public static Fraction max(Fraction left, Fraction right) {
		return left.compareTo(right) > 0 ? left : right;
	}

	public static Fraction min(Fraction left, Fraction right) {
		return left.compareTo(right) < 0 ? left : right;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Fraction fraction = (Fraction) o;
		return numerator == fraction.numerator && denominator == fraction.denominator;
	}

	@Override
	public int hashCode() {
		return Objects.hash(numerator, denominator);
	}

	@Override
	public String toString() {
		return "Fraction{"
				+ "numerator=" + numerator
				+ ", denominator=" + denominator
				+ '}';
	}

	//TODO: NBT serialization in here or in another class?
	public static Fraction fromTag(CompoundTag tag) {
		return Fraction.of(tag.getInt("Numerator"), tag.getInt("Denominator"));
	}

	public CompoundTag toTag(CompoundTag tag) {
		tag.putInt("Numerator", numerator);
		tag.putInt("Denominator", denominator);
		return tag;
	}
}
