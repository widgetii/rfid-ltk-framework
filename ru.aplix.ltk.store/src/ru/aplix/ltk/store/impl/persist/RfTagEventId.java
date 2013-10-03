package ru.aplix.ltk.store.impl.persist;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import ru.aplix.ltk.store.RfReceiver;


@Embeddable
public class RfTagEventId implements Serializable {

	private static final long serialVersionUID = 3987613916595154414L;

	@Column(name = "receiver", nullable = false, updatable = false)
	protected int receiverId;

	@Column(name = "id", nullable = false, updatable = false)
	protected long eventId;

	public RfTagEventId() {
	}

	public RfTagEventId(RfReceiver<?> receiver, long eventId) {
		this.receiverId = receiver.getId();
		this.eventId = eventId;
	}

	public int getReceiverId() {
		return this.receiverId;
	}

	public long getEventId() {
		return this.eventId;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;

		result = prime * result + (int) (this.eventId ^ (this.eventId >>> 32));
		result = prime * result + this.receiverId;

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

		final RfTagEventId other = (RfTagEventId) obj;

		if (this.eventId != other.eventId) {
			return false;
		}
		if (this.receiverId != other.receiverId) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {

		final StringBuilder out = new StringBuilder();

		out.append("RfTagEventId[receiverId=");
		out.append(this.receiverId);
		out.append(", eventId=");
		out.append(this.eventId);
		out.append("]");

		return out.toString();
	}

}
