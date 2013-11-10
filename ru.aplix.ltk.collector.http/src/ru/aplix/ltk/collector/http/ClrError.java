package ru.aplix.ltk.collector.http;

import java.util.Formatter;
import java.util.HashMap;


/**
 * Collector management response codes.
 */
public enum ClrError {

	UNEXPECTED(
			"unexpected",
			"Unexpected error: %s"),
	MISSING_PROFILE_ID(
			"missing.profile_id",
			"Profile identifier not specified"),
	INVALID_PROFILE_ID(
			"invalid.profile_id",
			"Invalid profile identifier"),
	UNKNOWN_PROFILE(
			"unknown.profile",
			"Profile does not exist: %s"),
	UNKNOWN_PROVIDER(
			"unknown.provider",
			"Unsupported RFID provider: %s");

	public static ClrError clrErrorByCode(String code) {

		final ClrError error = Registry.errorsByCode.get(code);

		return error != null ? error : UNEXPECTED;
	}

	private final String code;
	private final String defaultMessage;

	ClrError(String code, String defaultMessage) {
		this.code = code;
		this.defaultMessage = defaultMessage;
		Registry.errorsByCode.put(code, this);
	}

	public final String code() {
		return this.code;
	}

	public final String defaultMessage() {
		return this.defaultMessage;
	}

	@SuppressWarnings("resource")
	public String format(String message, String... args) {
		return new Formatter().format(message, args).toString();
	}

	private static final class Registry {

		private static final HashMap<String, ClrError> errorsByCode =
				new HashMap<>();

	}

}
