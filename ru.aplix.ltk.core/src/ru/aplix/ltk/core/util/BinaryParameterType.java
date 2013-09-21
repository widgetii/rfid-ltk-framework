package ru.aplix.ltk.core.util;


final class BinaryParameterType extends SimpleParameterType<byte[]> {

	@Override
	public byte[][] createValues(int length) {
		return new byte[length][];
	}

	@Override
	public byte[] valueFromString(String string) {

		final int len = string.length();
		final byte[] value = new byte[(len + 1) >> 1];

		for (int i = 0; i < len; i += 2) {

			final int intValue = Integer.parseInt(
					string.substring(i, Math.min(i + 2, len)),
					16);

			value[i >> 1] = (byte) (intValue & 0xff);
		}

		return value;
	}

	@Override
	public String valueToString(byte[] value) {

		final StringBuilder out = new StringBuilder(value.length << 1);

		for (int i = 0; i < value.length; ++i) {

			final String str = Integer.toHexString(value[i] & 0xff);

			if (str.length() < 2) {
				out.append('0');
			}
			out.append(str);
		}

		return out.toString();
	}

}
