package ru.aplix.ltk.core.util;

import java.util.UUID;


final class UUIDParameterType extends SimpleParameterType<UUID> {

	@Override
	public UUID[] createValues(int length) {
		return new UUID[length];
	}

	@Override
	public UUID valueFromString(String string) {
		if (string.isEmpty()) {
			return null;
		}
		return UUID.fromString(string);
	}

	@Override
	public String valueToString(UUID value) {
		return value.toString();
	}

}
