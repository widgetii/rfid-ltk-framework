package ru.aplix.ltk.collector.http;

import static java.util.Objects.requireNonNull;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import ru.aplix.ltk.core.RfProvider;


/**
 * An identifier of HTTP collector profile.
 */
public final class ClrProfileId implements Comparable<ClrProfileId> {

	private static final String ENC_HYPHEN = "--";
	private static final String ENC_DOT = "-dot-";
	private static final String ENC_AT = "-at-";

	private static final String ENCS[] = {
		ENC_HYPHEN,
		ENC_DOT,
		ENC_AT
	};

	/**
	 * Reconstructs profile identifier from its string representation.
	 *
	 * <a>The string representation is constructed by {@link #toString()}
	 * method. It does not contain dots or {@code '@'} signs inside identifier
	 * parts. The only {@code '@'} sign separates local identifier from profile,
	 * unless local identifier {@link #isDefault() is empty}.</p>
	 *
	 * @param in string representation to parse.
	 *
	 * @return reconstructed profile identifier.
	 */
	public static ClrProfileId parseClrProfileId(String in) {

		final int atIdx = in.lastIndexOf('@');

		if (atIdx < 0) {
			return new ClrProfileId(decode(in), "");
		}

		return new ClrProfileId(
				decode(in.substring(atIdx + 1)),
				decode(in.substring(0, atIdx)));
	}

	/**
	 * Construct the profile identifier from the given URL-encoded path.
	 *
	 * @param path an {@link #urlEncode() URL-encoded} representation of
	 * profile identifier.
	 *
	 * @return reconstructed profile identifier.
	 */
	public static ClrProfileId urlDecodeClrProfileId(String path) {
		try {
			return urlDecodeClrProfileId(path, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("UTF-8 is not supported", e);
		}
	}

	private static ClrProfileId urlDecodeClrProfileId(
			String path,
			String encoding)
	throws UnsupportedEncodingException {

		final int atIdx = path.lastIndexOf('@');

		if (atIdx < 0) {
			return new ClrProfileId(
					decode(URLDecoder.decode(path, encoding)), "");
		}

		return new ClrProfileId(
				decode(URLDecoder.decode(path.substring(atIdx + 1), encoding)),
				decode(URLDecoder.decode(path.substring(0, atIdx), encoding)));
	}

	private final String providerId;
	private final String id;

	/**
	 * Constructs an identifier of HTTP collector profile.
	 *
	 * @param providerId provider identifier.
	 * @param id local profile id, or <code>null</code> to use an empty string.
	 */
	public ClrProfileId(String providerId, String id) {
		requireNonNull(providerId, "RFID provider identifier not specified");
		if (providerId.isEmpty()) {
			throw new IllegalArgumentException(
					"RFID provider identifier should not be empty");
		}
		this.providerId = providerId;
		this.id = id != null ? id : "";
	}

	/**
	 * RFID provider identifier.
	 *
	 * <p>This identifier should match the target {@link RfProvider#getId()
	 * provider's one}.<p>
	 *
	 * @return identifier passed to the constructor.
	 */
	public final String getProviderId() {
		return this.providerId;
	}

	/**
	 * Local profile identifier.
	 *
	 * @return identifier passed to the constructor, or empty string.
	 */
	public final String getId() {
		return this.id;
	}

	/**
	 * Whether this is a default provider profile.
	 *
	 * @return <code>true</code> if {@link #getId() local} identifier is
	 * empty, or <code>false</code> otherwise.
	 */
	public final boolean isDefault() {
		return getId().isEmpty();
	}

	/**
	 * URL-encodes this identifier.
	 *
	 * <p>The URL-encoded representation of profile identifier may be one of:
	 * <ul>
	 * <li>{@code <id> '@' <providerId>} - containing both profile's identifier
	 * and provider's one, or</li>
	 * <li>{@code <providerId>} - containing only profile's identifier; in this
	 * case the profile's identifier is an empty string.</li>
	 * </ul>
	 * </p>
	 *
	 * @return URL-encoded profile string, which can be included into URL.
	 */
	public String urlEncode() {
		try {
			return urlEncode("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("UTF-8 is not supported", e);
		}
	}

	@Override
	public int compareTo(ClrProfileId o) {

		final int providerIdCmp = this.providerId.compareTo(o.providerId);

		if (providerIdCmp != 0) {
			return providerIdCmp;
		}
		if (isDefault()) {
			if (o.isDefault()) {
				return 0;
			}
			return 1;
		}
		if (o.isDefault()) {
			return -1;
		}

		return this.id.compareTo(o.id);
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;

		result = prime * result + this.id.hashCode();
		result = prime * result + this.providerId.hashCode();

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

		final ClrProfileId other = (ClrProfileId) obj;

		if (!this.id.equals(other.id)) {
			return false;
		}
		if (!this.providerId.equals(other.providerId)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		if (this.id == null) {
			return super.toString();
		}
		if (this.id.isEmpty()) {
			return encode(this.providerId);
		}

		final StringBuilder out = new StringBuilder(
				this.id.length() + 1 + this.providerId.length());

		encode(this.id, out);
		out.append('@');
		encode(this.providerId, out);

		return out.toString();
	}

	final String urlEncode(
			String encoding)
	throws UnsupportedEncodingException {

		final String providerId =
				URLEncoder.encode(encode(getProviderId()), encoding);

		if (getId().isEmpty()) {
			return providerId;
		}

		return URLEncoder.encode(encode(getId()), encoding) + '@' + providerId;
	}

	private static String decode(String in) {

		final int len = in.length();
		StringBuilder out = new StringBuilder(len);
		int i = 0;

		while (i < len) {

			final char c = in.charAt(i);

			if (c == '-') {
				if (contains(in, i, ENC_HYPHEN)) {
					out.append('-');
					i += ENC_HYPHEN.length();
					continue;
				}
				if (contains(in, i, ENC_DOT)) {
					out.append('.');
					i += ENC_DOT.length();
					continue;
				}
				if (contains(in, i, ENC_AT)) {
					out.append('@');
					i += ENC_AT.length();
					continue;
				}
			}
			out.append(c);
			++i;
		}

		return out.length() == len ? in : out.toString();
	}

	private static String encode(String in) {

		final int len = in.length();
		final StringBuilder out = new StringBuilder(len);

		encode(in, out);

		return out.length() == len ? in : out.toString();
	}

	private static void encode(String in, StringBuilder out) {

		final int len = in.length();
		int i = 0;

		while (i < len) {

			final char c = in.charAt(i);

			switch (c) {
			case '@':
				out.append(ENC_AT);
				++i;
				continue;
			case '.':
				out.append(ENC_DOT);
				++i;
				continue;
			case '-':
				i = encodeHyphen(in, i, out);
				continue;
			default:
				out.append(c);
				++i;
				continue;
			}
		}
	}

	private static int encodeHyphen(String in, int i, StringBuilder out) {
		out.append('-');
		for (String enc : ENCS) {
			if (contains(in, i, enc)) {
				out.append(enc);
				return i + enc.length();
			}
		}
		return i + 1;
	}

	private static boolean contains(String str, int start, String substr) {

		final int end = start + substr.length();

		if (end > str.length()) {
			return false;
		}

		return str.substring(start, end).equals(substr);
	}

}
