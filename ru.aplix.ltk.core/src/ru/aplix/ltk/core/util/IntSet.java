package ru.aplix.ltk.core.util;


/**
 * A set of integer values.
 *
 * <p>This implementation supports values from 0 to 31.</p>
 *
 * <p>Instances of this class are immutable.</p>
 */
public final class IntSet {

	/**
	 * A set of integers parameter type.
	 */
	public static final ParameterType<IntSet> INT_SET_PARAMATER_TYPE =
			new IntSetParameterType();

	/**
	 * Empty set of integers.
	 */
	public static final IntSet EMPTY_INT_SET = new IntSet(0);

	/**
	 * Full set of integers, including values more than 31 and less than 0.
	 */
	public static final IntSet FULL_INT_SET = new IntSet(0xFFFFFFFF);

	private static final IntSet[] SINGLETON_SETS = new IntSet[32];

	static {
		for (int i = 0; i < 32; ++i) {
			SINGLETON_SETS[i] = new IntSet(1 << i);
		}
	}

	/**
	 * Creates a set containing a integer.
	 *
	 * @param value an integer the constructed set should contain.
	 *
	 * @return a set containing {@code value}, or {@link #EMPTY_INT_SET empty
	 * set} if the given value is negative, or is greater than 31.
	 */
	public static final IntSet singletonIntSet(int value) {
		if (value < 0 || value > 31) {
			return EMPTY_INT_SET;
		}
		return SINGLETON_SETS[value];
	}

	/**
	 * Restores a set of integers from its string representation.
	 *
	 * <p>A string representation is a comma-separated list of integers or
	 * intervals. A special cases are:
	 * <ul>
	 * <li>asterisk ({@code "*"}), which represents a {@link #FULL_INT_SET
	 * full set},</li>
	 * <li>hyphen-minus ({@code "-"}), which represents an
	 * {@link #EMPTY_INT_SET empty set}, and</li>
	 * <li>empty string, which also represents an {@link #EMPTY_INT_SET empty
	 * set}.</li>
	 * </ul>
	 * </p>
	 *
	 * @param string string representation to parse.
	 *
	 * @return restored set of integers.
	 *
	 * @throws NumberFormatException if failed to parse some number.
	 * @throws IllegalArgumentException if the string given is not a valid
	 * string representation.
	 */
	public static IntSet parseIntSet(String string) {

		final String in = string.trim();

		if (in.isEmpty()) {
			return EMPTY_INT_SET;
		}
		if ("*".equals(in)) {
			return FULL_INT_SET;
		}
		if ("-".equals(in)) {
			return EMPTY_INT_SET;
		}

		int mask = 0;
		int start = 0;

		for (;;) {

			final int comma = in.indexOf(',', start);
			final String part;

			if (comma < 0) {
				part = in.substring(start).trim();
			} else {
				part = in.substring(start, comma).trim();
			}
			mask |= parsePart(part);
			if (comma < 0) {
				break;
			}
			start = comma + 1;
		}

		if (mask == 0) {
			return EMPTY_INT_SET;
		}

		return new IntSet(mask);
	}

	private static int parsePart(String part) {
		if (part.isEmpty()) {
			return 0;
		}

		final int hyphen = part.indexOf('-');

		if (hyphen < 0) {
			return 1 << parseValue(part);
		}

		final int start = parseValue(part.substring(0, hyphen).trim());
		final int end = parseValue(part.substring(hyphen + 1).trim());
		int mask = 0;

		for (int i = start; i <= end; ++i) {
			mask |= 1 << i;
		}

		return mask;
	}

	private static int parseValue(String part) {

		final int value = Integer.parseInt(part);

		if (value > 31) {
			throw new IllegalArgumentException("Value too big: " + value);
		}
		if (value < 0) {
			throw new IllegalArgumentException("Value too small: " + value);
		}

		return value;
	}

	private final int mask;

	private IntSet(int mask) {
		this.mask = mask;
	}

	/**
	 * Whether this set is empty.
	 *
	 * @return <code>true</code> if this set contains no integers, or
	 * <code>false</code> otherwise.
	 *
	 * @see #EMPTY_INT_SET
	 */
	public final boolean isEmpty() {
		return this.mask == 0;
	}

	/**
	 * Whether this set is full.
	 *
	 * @return <code>true</code> if this set contains all integers, or
	 * <code>false</code> otherwise.
	 *
	 * @see #FULL_INT_SET
	 */
	public final boolean isFull() {
		return this == FULL_INT_SET;
	}

	/**
	 * Checks whether the given value contained this this set.
	 *
	 * @param value value to check.
	 *
	 * @return <code>true</code> if so, or <code>false</code> otherwise.
	 */
	public final boolean contains(int value) {
		if (isFull()) {
			return true;
		}
		if (value < 0 || value > 31) {
			return false;
		}
		return (this.mask & (1 << value)) != 0;
	}

	/**
	 * Unions two sets of integers.
	 *
	 * @param other a set to union with
	 *
	 * @return a new set containing integers from both of sets.
	 */
	public final IntSet union(IntSet other) {
		if (isFull()) {
			return this;
		}
		if (other.isFull()) {
			return other;
		}

		final int mask = this.mask | other.mask;

		if (this.mask == mask) {
			return this;
		}
		if (other.mask == mask) {
			return other;
		}

		return new IntSet(mask);
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;

		result = prime * result + this.mask;

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		final IntSet other = (IntSet) obj;

		if (this.mask != other.mask) {
			return false;
		}
		if (this.mask == 0xFFFFFFFF) {
			// Full set can be equal to full set only.
			return !isFull() && !other.isFull();
		}

		return true;
	}

	@Override
	public String toString() {
		if (isFull()) {
			return "*";
		}
		if (isEmpty()) {
			return "-";
		}

		int mask = this.mask;
		final StringBuilder out = new StringBuilder();
		int first = -1;
		int last = -1;

		for (int i = 0; i < 31; ++i) {
			if ((mask & 1) == 0) {
				if (first >= 0) {
					appendValues(out, first, last);
					first = -1;
				}
			} else {
				if (first < 0) {
					first = i;
				}
				last = i;
			}
			mask >>>= 1;
			if (mask == 0) {
				break;
			}
		}
		if (first >= 0) {
			appendValues(out, first, last);
		}

		return out.toString();
	}

	private void appendValues(
			StringBuilder out,
			int first,
			int last) {
		if (out.length() != 0) {
			out.append(',');
		}
		out.append(first);
		if (last != first) {
			out.append('-').append(last);
		}
	}

	private static final class IntSetParameterType
			extends ParameterType<IntSet> {

		@Override
		public IntSet[] getValues(Parameters params, String name) {

			final String[] strings = params.valuesOf(name);

			if (strings == null) {
				return null;
			}
			if (strings.length == 0) {
				return new IntSet[0];
			}

			IntSet set = parseIntSet(strings[0]);

			for (int i = 1; i < strings.length; ++i) {
				set = set.union(parseIntSet(strings[i]));
			}

			return new IntSet[] {set};
		}

		@Override
		public void setValues(
				Parameters params,
				String name,
				IntSet[] values) {

			IntSet set = values[0];

			for (int i = 1; i < values.length; ++i) {
				set = set.union(values[i]);
			}

			params.set(name, set.toString());
		}

	}

}
