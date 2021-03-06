package ru.aplix.ltk.core.source;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;


/**
 * RFID tag.
 */
public final class RfTag {

	private final byte[] data;

	/**
	 * Constructs RFID tag.
	 *
	 * @param data binary tag data.
	 */
	public RfTag(byte[] data) {
		requireNonNull(data, "RFID tag data not specified");
		this.data = data;
	}

	/**
	 * Reconstructs RFID tag from its string representation.
	 *
	 * @param string a tag string representation created by
	 * {@link #toHexString()}, or {@link #toString()}.
	 */
	public RfTag(String string) {
		requireNonNull(string, "RFID tag string not specified");
		this.data = parseData(string);
	}

	/**
	 * Returns binary RFID tag data.
	 *
	 * <p>Do not modify the returned array!</p>
	 *
	 * @return array of bytes passed to tag constructor.
	 */
	public byte[] getData() {
		return this.data;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(this.data);
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

		final RfTag other = (RfTag) obj;

		return Arrays.equals(this.data, other.data);
	}

	public void toHexString(StringBuilder out) {
		for (byte b : this.data) {

			final int i = b & 0xff;
			final String s = Integer.toHexString(i);

			if (s.length() == 1) {
				out.append('0');
			}
			out.append(s);
		}
	}

	public String toHexString() {

		final int length = this.data.length;
		final StringBuilder out = new StringBuilder(length << 1);

		toHexString(out);

		return out.toString();
	}

	@Override
	public String toString() {
		if (this.data == null) {
			return super.toString();
		}

		final int length = this.data.length;

		if (length == 0) {
			return "0x00";
		}

		final StringBuilder out = new StringBuilder(2 + (length << 1));

		out.append("0x");
		toHexString(out);

		return out.toString();
	}

	private static byte[] parseData(String string) {

		final String str = removePrefix(string);
		final int len = str.length();
		final byte[] data = new byte[(len + 1) >> 1];

		for (int i = 0; i < len; i += 2) {

			final int val = Integer.parseInt(
					str.substring(i, Math.min(len, i + 2)),
					16);

			data[i >> 1] = (byte) val;
		}

		return data;
	}

	private static String removePrefix(String string) {
		final String str;

		if (string.startsWith("0x")) {
			str = string.substring(2);
		} else {
			str = string;
		}
		return str;
	}

}
