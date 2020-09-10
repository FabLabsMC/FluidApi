package io.github.fablabsmc.fablabs.api.fluid.v1.math;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.math.IntMath;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DynamicSerializable;

import java.util.HashMap;
import java.util.Map;

/**
 * a number whose value is represented as 3 ints, a whole, numerator and denominator.
 *
 * <p>This object is immutable and self-simplifies on instantiation.
 */
public final class MixedNumber extends Number implements Comparable<MixedNumber>, DynamicSerializable {
	public static final MixedNumber ZERO = new MixedNumber(0, 0, 1);
	public static final MixedNumber ONE = new MixedNumber(1, 0, 1);

	private final int whole;
	private final int numerator;
	private final /*Positive*/ int denominator;

	/**
	 * Should be only called if denominator is positive and numerator & denominator are coprime.
	 */
	private MixedNumber(int whole, int numerator, int denominator) {
		if (denominator <= 0) throw new ArithmeticException("denominator cannot be less than or equal to 0");

		if (IntMath.gcd(Math.abs(numerator), denominator) != 1) {
			throw new ArithmeticException("mixed number not simplified!");
		}

		this.whole = whole;
		this.numerator = numerator;
		this.denominator = denominator;
	}

	public static MixedNumber fromTag(CompoundTag tag) {
		return of(tag.getInt("whole"), tag.getInt("numerator"), tag.getInt("denominator"));
	}

	public static MixedNumber of(int whole, int numerator, int denominator) {
		if (denominator == 0) throw new ArithmeticException("Zero denominator");
		return denominator < 0 ? ofValidDenominator(whole, -numerator, -denominator) : ofValidDenominator(whole, numerator, denominator);
	}

	//TODO: What shortcuts should we have, and how should we name them? This is in a Minecraft context, after all.
	public static MixedNumber ofWhole(int numerator) {
		if (numerator == 0) return ZERO;
		if (numerator == 1) return ONE;
		return new MixedNumber(0, numerator, 1);
	}

	public static MixedNumber ofThirds(int numerator) {
		return ofValidDenominator(0, numerator, 3);
	}

	public static MixedNumber ofNinths(int numerator) {
		return ofValidDenominator(0, numerator, 9);
	}

	public static MixedNumber ofThousandths(int numerator) {
		return ofValidDenominator(0, numerator, 1000);
	}

	// should be only called if denom is positive
	@VisibleForTesting
	public static MixedNumber ofValidDenominator(int whole, int numerator, int denominator) {
		if (numerator == 0) return ZERO;
		if (numerator == denominator) return ONE;

		int gcd = IntMath.gcd(Math.abs(numerator), denominator);

		return new MixedNumber(whole, numerator / gcd, denominator / gcd);
	}

	public static MixedNumber add(MixedNumber... addends) {
		final int len;
		if ((len = addends.length) == 0) return ZERO;
		MixedNumber first = addends[0];

		int denominator = first.denominator;

		for (int i = 1; i < len; i++) {
			denominator = lcm(denominator, addends[i].denominator);
		}

		int numerator = 0;

		for (MixedNumber addend : addends) {
			numerator += denominator / addend.denominator * addend.numerator;
		}

		int whole = first.whole;

		while (numerator >= denominator) {
			numerator -= denominator;
			++whole;
		}

		return ofValidDenominator(whole, numerator, denominator);
	}

	public static MixedNumber subtract(MixedNumber minuend, MixedNumber subtrahend) {
		return minuend.subtract(subtrahend);
	}

	public static MixedNumber multiply(MixedNumber... factors) {
		final int len;
		if ((len = factors.length) == 0) return ONE;

		MixedNumber first = factors[0];
		int signum;
		if ((signum = first.signum()) == 0) return ZERO; // shortcut
		int whole = first.whole;
		int denominator = first.denominator;
		int numerator = Math.abs(first.numerator + whole * denominator);

		for (int i = 1; i < len; i++) {
			MixedNumber factor = factors[i];
			whole *= factor.whole;
			signum *= factor.signum();
			if (signum == 0) return ZERO; // shortcut
			int factorDenom = factor.denominator;
			int factorNum = Math.abs(factor.numerator);
			int gcd1 = IntMath.gcd(numerator, factorDenom);
			int gcd2 = IntMath.gcd(denominator, factorNum);
			numerator = (numerator / gcd1) * (factorNum / gcd2);
			denominator = (denominator / gcd2) * (factorDenom / gcd1);
			assert IntMath.gcd(numerator, denominator) == 1;
		}

		// should be simplified by here
		return new MixedNumber(whole, numerator * signum, denominator).simplify();
	}

	public static MixedNumber divide(MixedNumber dividend, MixedNumber divisor) {
		return dividend.divide(divisor);
	}

	private static int lcm(int a, int b) {
		return a / IntMath.gcd(a, b) * b; // divide first to prevent overflow
	}

	public static MixedNumber max(MixedNumber left, MixedNumber right) {
		return left.compareTo(right) > 0 ? left : right;
	}

	public static MixedNumber min(MixedNumber left, MixedNumber right) {
		return left.compareTo(right) < 0 ? left : right;
	}

	public int getNumerator(int denominator) {
		return numerator * (denominator / this.denominator);
	}

	public int getNumerator() {
		return numerator;
	}

	public boolean divisible(MixedNumber other) {
		return divide(other).getDenominator() == 1;
	}

	public int getDenominator() {
		return denominator;
	}

	public MixedNumber divide(MixedNumber other) {
		return multiply(other.inverse());
	}

	public MixedNumber multiply(MixedNumber other) {
		int whole = this.whole * other.whole;
		int gcd1 = IntMath.gcd(Math.abs(numerator), other.denominator);
		int gcd2 = IntMath.gcd(denominator, Math.abs(other.numerator));
		return new MixedNumber(whole,signum() * other.signum() * (numerator / gcd1) * (other.numerator / gcd2), (denominator / gcd2) * (other.denominator / gcd1)).simplify();
	}

	public MixedNumber inverse() throws ArithmeticException {
		// don't need to simplify
		switch (signum()) {
		case 1:
			return new MixedNumber(0, denominator, numerator + (whole * denominator));
		case -1:
			return new MixedNumber(0, -denominator, -numerator - (whole * denominator));
		default:
			throw new ArithmeticException("Cannot invert zero mixed number!");
		}
	}

	public int signum() {
		return Long.signum(numerator);
	}

	public MixedNumber floorNearest(MixedNumber other) {
		return other.multiply(ofWhole(floorDiv(other)));
	}

	public int floorDiv(MixedNumber other) {
		return this.divide(other).intValue();
	}

	public boolean isNegative() {
		return numerator < 0;
	}

	public boolean isPositive() {
		return numerator > 0;
	}

	@Override
	public double doubleValue() {
		return ((double) numerator) / denominator;
	}

	public MixedNumber simplify() {
		int whole = -this.whole;
		int numerator = -this.numerator;
		if (numerator < 0) {
			while (numerator < 0 && whole > 0) {
				--whole;
				numerator += this.denominator;
			}
		} else {
			while (numerator > this.denominator) {
				++whole;
				numerator -= this.denominator;
			}
		}
		int lcm = lcm(numerator, this.denominator);
		return of(whole, numerator / lcm, this.denominator / lcm);
	}

	public MixedNumber negate() {
		return of(-whole, -numerator, this.denominator).simplify();
	}

	public MixedNumber multiply(int amount) {
		return of(whole * amount, numerator * amount, this.denominator).simplify();
	}

	public MixedNumber add(MixedNumber other) {
		int commonMultiple = lcm(denominator, other.denominator);
		int leftNumerator = commonMultiple / denominator * numerator;
		int rightNumerator = commonMultiple / other.denominator * other.numerator;
		return ofValidDenominator(whole + other.whole, leftNumerator + rightNumerator, commonMultiple).simplify();
	}

	public MixedNumber subtract(MixedNumber other) {
		int commonMultiple = lcm(denominator, other.denominator);
		int leftNumerator = commonMultiple / denominator * numerator;
		int rightNumerator = commonMultiple / other.denominator * other.numerator;
		return ofValidDenominator(whole - other.whole, leftNumerator - rightNumerator, commonMultiple).simplify();
	}

	public MixedNumber floorWithDenominator(int denominator) {
		Preconditions.checkArgument(denominator > 0, "New denominator must be positive!");
		if (denominator == getDenominator()) return this;
		return MixedNumber.ofValidDenominator(whole, Math.floorDiv(numerator * denominator, this.denominator), denominator);
	}

	public boolean isGreaterThan(MixedNumber other) {
		return compareTo(other) > 0;
	}

	@Override
	public int compareTo(MixedNumber other) {
		int leftNum = (whole * denominator + numerator) * other.denominator;
		int rightNum = (other.whole * other.denominator + other.numerator) * denominator;
		return Long.compare(leftNum, rightNum);
	}

	public boolean isGreaterThanOrEqualTo(MixedNumber other) {
		return compareTo(other) >= 0;
	}

	@Override
	public int hashCode() {
		int result = (int) (numerator ^ (numerator >>> 32));
		result = 31 * result + (int) (denominator ^ (denominator >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MixedNumber other = (MixedNumber) o;
		return whole == other.whole && numerator == other.numerator && denominator == other.denominator;
	}

	@Override
	public String toString() {
		return whole + " " + numerator + "/" + denominator;
	}

	public void toTag(CompoundTag tag) {
		tag.putLong("whole", whole);
		tag.putLong("numerator", numerator);
		tag.putLong("denominator", denominator);
	}

	@Override
	public int intValue() {
		return (int) longValue();
	}

	@Override
	public long longValue() {
		return (whole * denominator + numerator) / denominator;
	}

	@Override
	public float floatValue() {
		return (float) doubleValue();
	}

	public Fraction fraction() {
		return Fraction.of(whole * denominator + numerator, denominator);
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		final Map<T, T> value = new HashMap<>();
		value.put(ops.createString("whole"), ops.createLong(this.whole));
		value.put(ops.createString("numerator"), ops.createLong(this.numerator));
		value.put(ops.createString("denominator"), ops.createLong(this.denominator));

		return ops.createMap(value);
	}

	public static <T> MixedNumber deserialize(Dynamic<T> dynamic) {
		final int whole = dynamic.get("whole").asInt(1);
		final int numerator = dynamic.get("numerator").asInt(0);
		final int denominator = dynamic.get("denominator").asInt(1);

		return of(whole, numerator, denominator);
	}
}
