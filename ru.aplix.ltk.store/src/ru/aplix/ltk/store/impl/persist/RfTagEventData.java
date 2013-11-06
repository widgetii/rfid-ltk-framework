package ru.aplix.ltk.store.impl.persist;

import static ru.aplix.ltk.core.collector.RfTagAppearance.RF_TAG_APPEARED;
import static ru.aplix.ltk.core.collector.RfTagAppearance.RF_TAG_DISAPPEARED;

import java.sql.Timestamp;

import javax.persistence.*;

import ru.aplix.ltk.core.collector.RfTagAppearance;
import ru.aplix.ltk.core.collector.RfTagAppearanceMessage;
import ru.aplix.ltk.core.source.RfTag;
import ru.aplix.ltk.store.RfReceiver;
import ru.aplix.ltk.store.RfTagEvent;
import ru.aplix.ltk.store.impl.RfStoreImpl;


@Entity
@Table(name = "tag_event", schema = "rfstore")
@NamedQueries({
	@NamedQuery(
			name = "lastRfTagEventId",
			query =
				"SELECT max(e.eventId)"
				+ " FROM RfTagEventData e"
				+ " WHERE e.receiverId = :receiverId"),
	@NamedQuery(
			name = "rfTagEventById",
			query =
				"SELECT e"
				+ " FROM RfTagEventData e"
				+ " WHERE e.receiverId = :receiverId"
				+ "   and e.eventId = :eventId"),
	@NamedQuery(
			name = "allReceiverRfTagEvents",
			query =
				"SELECT e"
				+ " FROM RfTagEventData e"
				+ " WHERE e.receiverId = :receiverId"
				+ " ORDER BY e.id"),
	@NamedQuery(
			name = "deleteRfTagEvents",
			query =
				"DELETE FROM RfTagEventData e"
				+ " WHERE e.receiverId = :receiverId")
})
public class RfTagEventData implements RfTagEvent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, unique = true, updatable = false)
	private long id;

	@Column(name = "receiver_id", nullable = false, updatable = false)
	private int receiverId;

	@Column(name = "event_id", nullable = true, updatable = false)
	private Long eventId;

	@Column(name = "initial_event", nullable = false, updatable = false)
	private boolean initialEvent;

	@Column(name = "tag", nullable = false, updatable = false)
	private String tag;

	@Transient
	private RfTag rfTag;

	@Column(name = "timestamp", nullable = false, updatable = false)
	private Timestamp timestamp;

	@Column(name = "appeared", nullable = false, updatable = false)
	private boolean appeared;

	@Transient
	private RfReceiver<?> receiver;

	public RfTagEventData() {
	}

	public RfTagEventData(
			RfReceiver<?> receiver,
			RfTagAppearanceMessage message) {
		this.receiverId = receiver.getId();
		this.receiver = receiver;
		this.eventId = message.getEventId();
		this.initialEvent = message.isInitialEvent();
		this.rfTag = message.getRfTag();
		this.tag = this.rfTag.toHexString();
		this.timestamp = new Timestamp(message.getTimestamp());
		this.appeared = message.getAppearance().isPresent();
	}

	public long getId() {
		return this.id;
	}

	@Override
	public int getReceiverId() {
		return this.receiverId;
	}

	@Override
	public long getEventId() {
		return this.eventId == null ? 0 : this.eventId.longValue();
	}

	@Override
	public boolean isInitialEvent() {
		return this.initialEvent;
	}

	@Override
	public RfReceiver<?> getReceiver() {
		return this.receiver;
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

	public void init(RfStoreImpl store) {
		this.receiver = store.rfReceiverById(getReceiverId());
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (this.id ^ (this.id >>> 32));

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

		if (this.id != other.id) {
			return false;
		}

		return true;
	}

}
