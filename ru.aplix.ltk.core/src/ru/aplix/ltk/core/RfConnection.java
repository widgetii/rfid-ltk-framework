package ru.aplix.ltk.core;

import ru.aplix.ltk.core.collector.RfCollector;
import ru.aplix.ltk.core.collector.RfTrackingPolicy;
import ru.aplix.ltk.core.source.RfSource;


/**
 * RFID connection.
 *
 * <p>It can be opened by {@link RfProvider#connect(RfSettings) RFID provider}
 * after proper {@link RfSettings configuration}. This is generally a container
 * of other RFID services, such as {@link RfSource}, and {@link RfCollector}.
 * </p>
 *
 * <p>Connections are not expected to be thread-safe by themselves, while the
 * objects they contain may have their own thread safety constraints.</p>
 */
public abstract class RfConnection {

	private RfCollector collector;
	private RfSource source;
	private RfTrackingPolicy trackingPolicy;

	/**
	 * Constructs RFID connection instance.
	 *
	 * @param settings RFID settings of this connection.
	 */
	public RfConnection(RfSettings settings) {
		this.trackingPolicy = settings.getTrackingPolicy();
	}

	/**
	 * RFID tags collector.
	 *
	 * <p>Construct the collector by calling {@link #createCollector()} method
	 * if not constructed yet.</p>
	 *
	 * @return collector instance.
	 */
	public final RfCollector getCollector() {
		if (this.collector != null) {
			return this.collector;
		}
		return this.collector = createCollector();
	}

	/**
	 * RFID tracking policy.
	 *
	 * @return tracking policy instance from the
	 * {@link RfSettings#getTrackingPolicy() settings} passed to the
	 * constructor.
	 */
	public final RfTrackingPolicy getTrackingPolicy() {
		return this.trackingPolicy;
	}

	/**
	 * RFID data source.
	 *
	 * <p>Construct the data source by calling {@link #createSource()} method if not
	 * constructed yet.</p>
	 *
	 * @return reader instance.
	 */
	public final RfSource getSource() {
		if (this.source != null) {
			return this.source;
		}
		return this.source = createSource();
	}

	/**
	 * Creates new RFID collector.
	 *
	 * <p>This method is called at most once from {@link #getCollector()}
	 * method.</p>
	 *
	 * <p>By default, creates standard collector for the connection's
	 * {@link #getSource() data source}, and using the connection's
	 * {@link #getTrackingPolicy() tracking policy}.</p>
	 *
	 * @return new collector instance.
	 */
	protected RfCollector createCollector() {
		return new RfCollector(getSource(), getTrackingPolicy());
	}

	/**
	 * Creates RFID data source.
	 *
	 * <p>This method is called at most once from {@link #getSource()} method.
	 * </p>
	 *
	 * @return new RFID data source instance.
	 */
	protected abstract RfSource createSource();

}
