package io.github.fablabsmc.fablabs.api.fluidvolume.v1.math;

import java.util.stream.LongStream;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.math.LongMath;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DynamicSerializable;

/**
 * a number whose value is represented as 2 long, a numerator and denominator.
 *
 * <p>This object is immutable and self-simplifies on instantiation.
 */
public final class Fraction extends Number implements Comparable<Fraction>, DynamicSerializable {
	public static final Fraction ZERO = new Fraction(0, 1);
	public static final Fraction ONE = new Fraction(1, 1);

	private final long numerator;
	private final /*Positive*/ long denominator;

	/**
	 * Should be only called if denominator is positive and numerator & denominator are coprime.
	 */
	private Fraction(long numerator, long denominator) {
		if (denominator <= 0) throw new ArithmeticException("denominator cannot be less than 0");

		if (LongMath.gcd(Math.abs(numerator), denominator) != 1) {
			throw new ArithmeticException("fraction not simplified!");
		}

		this.numerator = numerator;
		this.denominator = denominator;
	}

	public static Fraction fromTag(CompoundTag tag) {
		return of(tag.getLong("numerator"), tag.getLong("denominator"));
	}

	public static Fraction of(long numerator, long denominator) {
		if (denominator == 0) throw new ArithmeticException("Zero denominator");
		return denominator < 0 ? ofValidDenominator(-numerator, -denominator) : ofValidDenominator(numerator, denominator);
	}

	//TODO: What shortcuts should we have, and how should we name them? This is in a Minecraft context, after all.
	public static Fraction ofWhole(long numerator) {
		if (numerator == 0) return ZERO;
		if (numerator == 1) return ONE;
		return new Fraction(numerator, 1);
	}

	public static Fraction ofThirds(long numerator) {
		return ofValidDenominator(numerator, 3);
	}

	public static Fraction ofNinths(long numerator) {
		return ofValidDenominator(numerator, 9);
	}

	public static Fraction ofThousandths(long numerator) {
		return ofValidDenominator(numerator, 1000);
	}

	// should be only called if denom is positive
	@VisibleForTesting
	public static Fraction ofValidDenominator(long numerator, long denominator) {
		if (numerator == 0) return ZERO;
		if (numerator == denominator) return ONE;

		long gcd = LongMath.gcd(Math.abs(numerator), denominator);

		return new Fraction(numerator / gcd, denominator / gcd);
	}

	public static Fraction add(Fraction... addends) {
		final int len;
		if ((len = addends.length) == 0) return ZERO;
		Fraction first = addends[0];

		long denominator = first.denominator;

		for (int i = 1; i < len; i++) {
			denominator = lcm(denominator, addends[i].denominator);
		}

		long numerator = 0;

		for (Fraction addend : addends) {
			numerator += denominator / addend.denominator * addend.numerator;
		}

		return ofValidDenominator(numerator, denominator);
	}

	public static Fraction subtract(Fraction minuend, Fraction subtrahend) {
		return minuend.subtract(subtrahend);
	}

	public static Fraction multiply(Fraction... factors) {
		final int len;
		if ((len = factors.length) == 0) return ONE;

		Fraction first = factors[0];
		long signum;
		if ((signum = first.signum()) == 0) return ZERO; // shortcut
		long numerator = Math.abs(first.numerator);
		long denominator = first.denominator;

		for (int i = 1; i < len; i++) {
			Fraction factor = factors[i];
			signum *= factor.signum();
			if (signum == 0) return ZERO; // shortcut
			long factorDenom = factor.denominator;
			long factorNum = Math.abs(factor.numerator);
			long gcd1 = LongMath.gcd(numerator, factorDenom);
			long gcd2 = LongMath.gcd(denominator, factorNum);
			numerator = (numerator / gcd1) * (factorNum / gcd2);
			denominator = (denominator / gcd2) * (factorDenom / gcd1);
			assert LongMath.gcd(numerator, denominator) == 1;
		}

		// should be simplified by here
		return new Fraction(numerator * signum, denominator);
	}

	public static Fraction divide(Fraction dividend, Fraction divisor) {
		return dividend.divide(divisor);
	}

	private static long lcm(long a, long b) {
		return a / LongMath.gcd(a, b) * b; // divide first to prevent overflow
	}

	public static Fraction max(Fraction left, Fraction right) {
		return left.compareTo(right) > 0 ? left : right;
	}

	public static Fraction min(Fraction left, Fraction right) {
		return left.compareTo(right) < 0 ? left : right;
	}

	public static <T> Fraction deserialize(Dynamic<T> dynamic) {
		long[] arr = dynamic.asLongStream().toArray();

		if (arr.length == 0) {
			return ZERO;
		}

		if (arr.length == 1) {
			return ofWhole(arr[0]);
		}

		return of(arr[0], arr[1]);
	}

	public long getNumerator(long denominator) {
		return this.numerator * (denominator / this.denominator);
	}

	public long getNumerator() {
		return this.numerator;
	}

	public boolean divisible(Fraction fraction) {
		return this.divide(fraction).getDenominator() == 1;
	}

	public long getDenominator() {
		return this.denominator;
	}

	public Fraction divide(Fraction other) {
		return this.multiply(other.inverse());
	}

	public Fraction multiply(Fraction other) {
		long gcd1 = LongMath.gcd(Math.abs(this.numerator), other.denominator);
		long gcd2 = LongMath.gcd(this.denominator, Math.abs(other.numerator));
		// guaranteed simplified
		return new Fraction(this.signum() * other.signum() * (this.numerator / gcd1) * (other.numerator / gcd2), (this.denominator / gcd2) * (other.denominator / gcd1));
	}

	public Fraction inverse() throws ArithmeticException {
		// don't need to simplify
		switch (this.signum()) {
		case 1:
			return new Fraction(this.denominator, this.numerator);
		case -1:
			return new Fraction(-this.denominator, -this.numerator);
		default:
			throw new ArithmeticException("Cannot invert zero fraction!");
		}
	}

	public int signum() {
		return Long.signum(this.numerator);
	}

	public Fraction floorNearest(Fraction fraction) {
		return fraction.multiply(ofWhole(this.floorDiv(fraction)));
	}

	public long floorDiv(Fraction fraction) {
		return Math.floorDiv(this.numerator * fraction.denominator, fraction.numerator * this.denominator);
	}

	public boolean isNegative() {
		return this.numerator < 0;
	}

	public boolean isPositive() {
		return this.numerator > 0;
	}

	@Override
	public double doubleValue() {
		return ((double) this.numerator) / this.denominator;
	}

	public Fraction negate() {
		// don't need to simplify
		return new Fraction(-this.numerator, this.denominator);
	}

	public Fraction multiply(long amount) {
		return of(this.numerator * amount, this.denominator);
	}

	public Fraction add(Fraction other) {
		long commonMultiple = lcm(this.denominator, other.denominator);
		long leftNumerator = commonMultiple / this.denominator * this.numerator;
		long rightNumerator = commonMultiple / other.denominator * other.numerator;
		return ofValidDenominator(leftNumerator + rightNumerator, commonMultiple);
	}

	public Fraction subtract(Fraction other) {
		long commonMultiple = lcm(this.denominator, other.denominator);
		long leftNumerator = commonMultiple / this.denominator * this.numerator;
		long rightNumerator = commonMultiple / other.denominator * other.numerator;
		return ofValidDenominator(leftNumerator - rightNumerator, commonMultiple);
	}

	public Fraction floorWithDenominator(long denominator) {
		Preconditions.checkArgument(denominator > 0, "New denominator must be positive!");
		if (denominator == this.getDenominator()) return this;
		return Fraction.ofValidDenominator(Math.floorDiv(this.numerator * denominator, this.denominator), denominator);
	}

	public boolean isGreaterThan(Fraction fraction) {
		return this.compareTo(fraction) > 0;
	}

	@Override
	public int compareTo(Fraction other) {
		long leftNum = this.numerator * other.denominator;
		long rightNum = other.numerator * this.denominator;
		return Long.compare(leftNum, rightNum);
	}

	public boolean isGreaterThanOrEqualTo(Fraction fraction) {
		return this.compareTo(fraction) >= 0;
	}

	@Override
	public int hashCode() {
		int result = (int) (this.numerator ^ (this.numerator >>> 32));
		result = 31 * result + (int) (this.denominator ^ (this.denominator >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		Fraction fraction = (Fraction) o;
		return this.numerator == fraction.numerator && this.denominator == fraction.denominator;
	}

	@Override
	public String toString() {
		long whole = this.numerator / this.denominator;
		long part = this.numerator % this.denominator;
		return whole + " " + part + "/" + this.denominator;
	}

	public void toTag(CompoundTag tag) {
		tag.putLong("numerator", this.numerator);
		tag.putLong("denominator", this.denominator);
	}

	@Override
	public int intValue() {
		return (int) this.longValue();
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		return ops.createLongList(LongStream.of(this.numerator, this.denominator));
	}

	@Override
	public long longValue() {
		return (int) (this.numerator / this.denominator);
	}

	@Override
	public float floatValue() {
		return (float) this.doubleValue();
	}
}
