package ru.aplix.ltk.store.impl.persist;

import static ru.aplix.ltk.core.collector.RfTagAppearance.RF_TAG_APPEARED;
import static ru.aplix.ltk.core.collector.RfTagAppearance.RF_TAG_DISAPPEARED;

import java.sql.Timestamp;

import javax.persistence.*;

import ru.aplix.ltk.core.collector.RfTagAppearance;
import ru.aplix.ltk.core.collector.RfTagAppearanceMessage;
import ru.aplix.ltk.core.source.RfTag;
import ru.aplix.ltk.store.RfReceiver;


@Entity
@Table(name = "tag_event", schema = "rfstore")
@NamedQueries({
	@NamedQuery(
			name = "lastRfTagEventId",
			query =
				"SELECT max(e.id.eventId)"
				+ " FROM RfTagEventData e"
				+ " WHERE e.id.receiverId = :receiverId"),
	@NamedQuery(
			name = "rfTagEvents",
			query =
				"SELECT e"
				+ " FROM RfTagEventData e"
				+ " WHERE e.id.receiverId = :receiverId"
				+ " and e.id.eventId > :fromId"
				+ " ORDER BY e.id.eventId"),
	@NamedQuery(
			name = "deleteRfTagEvents",
			query =
				"DELETE FROM RfTagEventData e"
				+ " WHERE e.id.receiverId = :receiverId")
})
public class RfTagEventData implements RfTagAppearanceMessage {

	@EmbeddedId
	private RfTagEventId id;

	@Column(name = "tag", nullable = false, updatable = false)
	private String tag;

	@Transient
	private RfTag rfTag;

	@Column(name = "timestamp", nullable = false, updatable = false)
	private Timestamp timestamp;

	@Column(name = "appeared", nullable = false, updatable = false)
	private boolean appeared;

	public RfTagEventData() {
	}

	public RfTagEventData(
			RfReceiver<?> receiver,
			long eventId,
			RfTagAppearanceMessage message) {
		this.id = new RfTagEventId(receiver, eventId);
		this.rfTag = message.getRfTag();
		this.tag = this.rfTag.toHexString();
		this.timestamp = new Timestamp(message.getTimestamp());
		this.appeared = message.getAppearance().isPresent();
	}

	public RfTagEventId getId() {
		return this.id;
	}

	@Override
	public long getEventId() {
		return getId().getEventId();
	}

	public Timestamp getSqlTimestamp() {
		return this.timestamp;
	}

	@Override
	public long getTimestamp() {
		return getSqlTimestamp().getTime();
	}

	public String getTag() {
		return this.tag;
	}

	@Override
	public RfTag getRfTag() {
		if (this.rfTag != null) {
			return this.rfTag;
		}
		return this.rfTag = new RfTag(this.tag);
	}

	public boolean isAppeared() {
		return this.appeared;
	}

	@Override
	public RfTagAppearance getAppearance() {
		return isAppeared() ? RF_TAG_APPEARED : RF_TAG_DISAPPEARED;
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
