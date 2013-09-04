package ru.aplix.ltk.driver.ctg.impl;

import java.util.Arrays;

import org.llrp.ltk.generated.interfaces.EPCParameter;

import ru.aplix.ltk.core.source.RfTag;


public class LLRPTag implements RfTag {

	private final byte[] data;

	public LLRPTag(EPCParameter epc) {
		this.data = epc.encodeBinary().toByteArray();
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

		final LLRPTag other = (LLRPTag) obj;

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
