package ru.aplix.ltk.driver.ctg.impl;

import static java.util.Objects.requireNonNull;
import ru.aplix.ltk.driver.ctg.CtgRfSettings;


public class LLRPReaderId {

	private final String readerHost;
	private final int readerPort;
	private final int roSpecId;

	public LLRPReaderId(CtgRfSettings settings) {
		this(
				settings.getReaderHost(),
				settings.getReaderPort(),
				settings.getROSpecId());
	}

	public LLRPReaderId(String readerHost, int readerPort, int roSpecId) {
		requireNonNull(readerHost, "RFID reader host not specified");
		this.readerHost = readerHost;
		this.readerPort = readerPort;
		this.roSpecId = roSpecId;
	}

	public final String getReaderHost() {
		return this.readerHost;
	}

	public final int getReaderPort() {
		return this.readerPort;
	}

	public final int getRoSpecId() {
		return this.roSpecId;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;

		result = prime * result + this.readerHost.hashCode();
		result = prime * result + this.readerPort;
		result = prime * result + this.roSpecId;

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

		final LLRPReaderId other = (LLRPReaderId) obj;

		if (!this.readerHost.equals(other.readerHost)) {
			return false;
		}
		if (this.readerPort != other.readerPort) {
			return false;
		}
		if (this.roSpecId != other.roSpecId) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		if (this.readerHost == null) {
			return super.toString();
		}
		return "LLRPReaderId["
				+ this.roSpecId
				+ '@' + this.readerHost + ':' + this.readerPort + ']';
	}
}
