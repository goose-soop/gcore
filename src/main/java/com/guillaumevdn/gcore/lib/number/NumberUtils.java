package com.guillaumevdn.gcore.lib.number;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.guillaumevdn.gcore.lib.exception.CalculationError;
import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * @author GuillaumeVDN
 */
public final class NumberUtils {

	// ----- integer
	public static Integer integerOrNull(Object raw) {
		try {
			return Integer.valueOf(raw.toString().trim());
		} catch (Throwable ignored) {
			return null;
		}
	}

	public static int integerOrElse(Object raw, int def) {
		Integer integer = integerOrNull(raw);
		return integer != null ? integer : def;
	}

	public static Integer orZero(Integer obj) {
		return obj != null ? obj : 0;
	}

	public static List<Integer> integersIn(String... raw) {
		return integersIn(Stream.of(raw)).collect(Collectors.toList());
	}

	public static List<Integer> integersIn(Collection<String> raw) {
		return streamIntegersIn(raw).collect(Collectors.toList());
	}

	public static Stream<Integer> streamIntegersIn(Collection<String> list) {
		return list.stream().map(str -> integerOrNull(str)).filter(nb -> nb != null);
	}

	public static Stream<Integer> integersIn(Stream<String> stream) {
		return stream.map(str -> integerOrNull(str)).filter(nb -> nb != null);
	}

	public static List<Integer> range(int includedStart, int includedEnd) {
		return rangeStream(includedStart, includedEnd).collect(Collectors.toList());
	}

	public static Stream<Integer> rangeStream(int includedStart, int includedEnd) {
		return Stream.iterate(includedStart, i -> i + 1).limit(includedEnd - includedStart + 1);
	}

	public static int square(int nb) {
		return nb * nb;
	}

	public static double square(double nb) {
		return nb * nb;
	}

	// ----- long
	public static Long longOrNull(Object raw) {
		try {
			return Long.valueOf(raw.toString().trim());
		} catch (Throwable ignored) {
			return null;
		}
	}

	// ----- double
	public static Double doubleOrNull(Object raw) {
		try {
			return Double.valueOf(raw.toString().trim());
		} catch (Throwable ignored) {
			return null;
		}
	}

	private static Map<Integer, DecimalFormat> roundFormatCache = new HashMap<>(1);

	public static double round(double value, int places) {
		if (places <= 0) {
			return (double) ((int) value);
		}
		return Double.parseDouble(roundString(value, places));
	}

	public static String roundString(double value, int places) {
		if (places <= 0) {
			return "" + (int) value;
		}
		DecimalFormat format = roundFormatCache.computeIfAbsent(places, __ -> new DecimalFormat("#." + StringUtils.repeatString("#", places)));
		return format.format(value).replace(',', '.');  // happens sometimes for some reason, wrong locale maybe
	}

	public static String roundStringFillZerosRight(double value, int places) {
		String format = roundString(value, places);
		final String[] split = format.split("\\.");
		if (split.length <= 1) {
			return split[0] + "." + StringUtils.repeatString("0", places);
		} else {
			return format + StringUtils.repeatString("0", places - split[1].length());
		}
	}

	private static final double DOUBLE_COMPARISON_THRESHOLD = 0.0001d;

	public static boolean doubleEquals(double a, double b) {
		return doubleEquals(a, b, DOUBLE_COMPARISON_THRESHOLD);
	}

	public static boolean doubleEquals(double a, double b, double threshold) {
		return Math.abs(a - b) <= threshold;
	}

	// ----- float
	public static Float floatOrNull(Object raw) {
		try {
			return Float.valueOf(raw.toString().trim());
		} catch (Throwable ignored) {
			return null;
		}
	}

	// ----- byte
	public static Byte byteOrNull(Object raw) {
		try {
			return Byte.valueOf(raw.toString().trim());
		} catch (Throwable ignored) {
			return null;
		}
	}

	// ----- random
	public static int random(int min, int max) {
		return min == max ? min /* error thrown otherwise */ : ThreadLocalRandom.current().nextInt(min, max + 1);
	}

	public static float random(float min, float max) {
		return min == max ? min /* error thrown otherwise */ : (float) random((double) min, (double) max);
	}

	public static double random(double min, double max) {
		return min == max ? min /* error thrown otherwise */ : ThreadLocalRandom.current().nextDouble(min, max /* this is a double so no +1 */);
	}

	public static long random(long min, long max) {
		return min == max ? min /* error thrown otherwise */ : ThreadLocalRandom.current().nextLong(min, max + 1L);
	}

	public static boolean random() {
		return ThreadLocalRandom.current().nextBoolean();
	}

	// ----- range
	public static boolean isInRange(Integer number, int min, int max) {
		return number != null && number >= min && number <= max;
	}

	public static boolean isInRange(Double number, double min, double max) {
		return number != null && number >= min && number <= max;
	}

	public static int inRange(int number, int min, int max) {
		return number < min ? min : (number > max ? max : number);
	}

	public static double inRange(double number, double min, double max) {
		return number < min ? min : (number > max ? max : number);
	}

	// ----- calculation
	// ----- https://stackoverflow.com/questions/3422673/how-to-evaluate-a-math-expression-given-in-string-form, that guy is a real programmer, Pog
	public static double calculateExpression(String expression) throws CalculationError {
		final String str = expression.trim().toLowerCase();
		return new Object() {
			int pos = -1, ch;

			void nextChar() {
				ch = (++pos < str.length()) ? str.charAt(pos) : -1;
			}

			boolean eat(int charToEat) {
				while (ch == ' ') nextChar();
				if (ch == charToEat) {
					nextChar();
					return true;
				}
				return false;
			}

			double parse() {
				nextChar();
				double x = parseExpression();
				if (pos < str.length()) throw new CalculationError("when parsing expression '" + expression + "', unexpected character '" + new Character((char) ch).toString() + "'");
				return x;
			}

			// Grammar:
			// expression = term | expression `+` term | expression `-` term
			// term = factor | term `*` factor | term `/` factor
			// factor = `+` factor | `-` factor | `(` expression `)`
			//        | number | functionName factor | factor `^` factor

			double parseExpression() {
				double x = parseTerm();
				for (;;) {
					if      (eat('+')) x += parseTerm(); // addition
					else if (eat('-')) x -= parseTerm(); // subtraction
					else return x;
				}
			}

			double parseTerm() {
				double x = parseFactor();
				for (;;) {
					if      (eat('*')) x *= parseFactor(); // multiplication
					else if (eat('/')) x /= parseFactor(); // division
					else return x;
				}
			}

			double parseFactor() {
				if (eat('+')) return parseFactor(); // unary plus
				if (eat('-')) return -parseFactor(); // unary minus

				double x;
				int startPos = this.pos;
				if (eat('(')) { // parentheses
					x = parseExpression();
					eat(')');
				} else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
					while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
					x = Double.parseDouble(str.substring(startPos, this.pos));
				} else if (ch >= 'a' && ch <= 'z') { // functions
					while (ch >= 'a' && ch <= 'z') nextChar();
					String func = str.substring(startPos, this.pos);
					x = parseFactor();
					if (func.equals("sqrt")) x = Math.sqrt(x);
					else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
					else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
					else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
					else if (func.equals("ln")) x = Math.log(x);
					else if (func.equals("log")) x = Math.log10(x);
					else if (func.equals("ceil")) x = Math.ceil(x);
					else if (func.equals("floor")) x = Math.floor(x);
					else if (func.equals("round")) x = Math.round(x);
					else if (func.equals("rand")) x = random(0d, x);
					else if (func.equals("randint")) x = random(0, (int) x);
					else if (func.equals("abs")) x = Math.abs(x);
					else if (func.equals("posorzero")) x = x >= 0d ? x : 0d;
					else if (func.equals("posorone")) x = x >= 0d ? x : 1d;
					else throw new CalculationError("when parsing expression '" + expression + "', unknown function '" + func + "'");
				} else {
					throw new CalculationError("when parsing expression '" + expression + "', unexpected character : '" + (char) ch + "'");
				}

				if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

				return x;
			}
		}.parse();
	}

}
