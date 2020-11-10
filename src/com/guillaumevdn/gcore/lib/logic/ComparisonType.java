package com.guillaumevdn.gcore.lib.logic;

import java.util.Comparator;

import com.guillaumevdn.gcore.lib.function.TriFunction;

/**
 * @author GuillaumeVDN
 */
public enum ComparisonType {

	LESS_THAN("<",
			(a, b, comparator) -> comparator.compare(a, b) < 0,
			(value, reference, extraReference) -> value < reference,
			(value, reference, extraReference) -> value < reference,
			null
			),
	LESS_OR_EQUALS("<=",
			(a, b, comparator) -> comparator.compare(a, b) <= 0,
			(value, reference, extraReference) -> value <= reference,
			(value, reference, extraReference) -> value <= reference,
			null
			),
	MORE_OR_EQUALS(">=",
			(a, b, comparator) -> comparator.compare(a, b) >= 0,
			(value, reference, extraReference) -> value >= reference,
			(value, reference, extraReference) -> value >= reference,
			(value, reference, extraReference) -> value - reference
			),
	MORE_THAN(">",
			(a, b, comparator) -> comparator.compare(a, b) > 0,
			(value, reference, extraReference) -> value > reference,
			(value, reference, extraReference) -> value > reference,
			(value, reference, extraReference) -> value - reference
			),
	EQUALS("=",
			(a, b, comparator) -> comparator.compare(a, b) == 0,
			(value, reference, extraReference) -> value == reference,
			(value, reference, extraReference) -> value == reference,
			(value, reference, extraReference) -> 0d
			),
	DIFFERENT("!=",
			(a, b, comparator) -> comparator.compare(a, b) != 0,
			(value, reference, extraReference) -> value != reference,
			(value, reference, extraReference) -> value != reference,
			(value, reference, extraReference) -> value - reference
			),
	IN_RANGE(">=<",
			null,
			(value, reference, extraReference) -> extraReference != null && value >= reference && value <= extraReference,
			(value, reference, extraReference) -> extraReference != null && value >= reference && value <= extraReference,
			(value, reference, extraReference) -> value - reference
			)
	;

	// base
	private String symbol;
	private TriFunction<Comparable, Comparable, Comparator, Boolean> comparableComparator;
	private TriFunction<Double, Double, Double, Boolean> comparatorDouble;
	private TriFunction<Long, Long, Long, Boolean> comparatorLong;
	private TriFunction<Double, Double, Double, Double> taker;

	ComparisonType(String symbol, TriFunction<Comparable, Comparable, Comparator, Boolean> comparableComparator, TriFunction<Double, Double, Double, Boolean> comparatorDouble, TriFunction<Long, Long, Long, Boolean> comparatorLong, TriFunction<Double, Double, Double, Double> taker) {
		this.symbol = symbol;
		this.comparableComparator = comparableComparator;
		this.comparatorDouble = comparatorDouble;
		this.comparatorLong = comparatorLong;
		this.taker = taker;
	}

	// get
	public String getSymbol() {
		return symbol;
	}

	// do
	public <T extends Comparable<T>> boolean compare(Comparable<T> a, Comparable<T> b) {
		return compare(a, b, (a1, b1) -> a1.compareTo(b1));
	}

	public <T extends Comparable<T>> boolean compare(Comparable<T> a, Comparable<T> b, Comparator<T> comparator) {
		return comparableComparator != null && comparableComparator.apply(a, b, comparator);
	}

	public boolean compare(double value, double reference, Double extraReference) {
		return comparatorDouble.apply(value, reference, extraReference);
	}

	public boolean compare(long value, long reference, Long extraReference) {
		return comparatorLong.apply(value, reference, extraReference);
	}

	public Double take(double value, double reference, Double extraReference) {
		return taker == null ? null : taker.apply(value, reference, extraReference);
	}

	public boolean canTake() {
		return taker != null;
	}

	// static
	public static ComparisonType fromSymbolOrNull(String symbol) {
		for (ComparisonType comparison : ComparisonType.values()) {
			if (comparison.symbol.equalsIgnoreCase(symbol)) {
				return comparison;
			}
		}
		return null;
	}

}
