package ru.aplix.ltk.core.source;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;


/**
 * RFID tag.
 */
public final class RfTag {

	private final byte[] data;

	/**
	 * Constructs RFID tag..
	 *
	 * @param data binary tag data.
	 */
	public RfTag(byte[] data) {
		requireNonNull(data, "RFID tag data not specified");
		this.data = data;
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
		for (byte b : this.data) {

			final int i = b & 0xff;
			final String s = Integer.toHexString(i);

			if (s.length() == 1) {
				out.append('0');
			}
			out.append(s);
		}

		return out.toString();
	}

}
