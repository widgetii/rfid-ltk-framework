package ru.aplix.ltk.store.impl.persist;

import javax.persistence.*;


@Entity
@Table(name = "receiver", schema = "rfstore")
@NamedQueries({
	@NamedQuery(
			name = "allRfReceivers",
			query = "SELECT r FROM RfReceiverData r"),
	@NamedQuery(
			name = "providerRfReceivers",
			query = "SELECT r FROM RfReceiverData r"
			+ " WHERE r.provider = :providerId"),
	@NamedQuery(
			name = "deleteRfReceiver",
			query = "DELETE FROM RfReceiverData r WHERE r.id = :id")
})
public class RfReceiverData {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, unique = true, updatable = false)
	private int id;

	@Column(name = "provider", nullable = false)
	private String provider;

	@Column(name = "active", nullable = false)
	private boolean active;

	@Column(name = "settings", nullable = false)
	private String settings;

	public RfReceiverData() {
	}

	public RfReceiverData(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public String getProvider() {
		return this.provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getSettings() {
		return this.settings;
	}

	public void setSettings(String settings) {
		this.settings = settings;
	}

}
