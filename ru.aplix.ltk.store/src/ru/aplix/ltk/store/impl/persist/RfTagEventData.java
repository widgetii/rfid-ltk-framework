package ru.aplix.ltk.store.impl.persist;

import java.sql.Timestamp;

import javax.persistence.*;

import ru.aplix.ltk.core.collector.RfTagAppearanceMessage;
import ru.aplix.ltk.store.RfReceiver;


@Entity
@Table(name = "tag_event", schema = "rfstore")
public class RfTagEventData {

	@EmbeddedId
	private RfTagEventId id;

	@Column(name = "tag", nullable = false, updatable = false)
	private String tag;

	@Column(name = "timestamp", nullable = false, updatable = false)
	private Timestamp timestamp;

	@Column(name = "appeared", nullable = false, updatable = false)
	private boolean appeared;

	public RfTagEventData() {
	}

	public RfTagEventData(
			RfReceiver<?> receiver,
			RfTagAppearanceMessage message) {
		this.id = new RfTagEventId(receiver, message);
		this.tag = message.getRfTag().toHexString();
		this.timestamp = new Timestamp(message.getTimestamp());
		this.appeared = message.getAppearance().isPresent();
	}

	public RfTagEventId getId() {
		return this.id;
	}

	public String getTag() {
		return this.tag;
	}

	public Timestamp getTimestamp() {
		return this.timestamp;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;

		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());

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

		final RfTagEventData other = (RfTagEventData) obj;

		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}

		return true;
	}

}
