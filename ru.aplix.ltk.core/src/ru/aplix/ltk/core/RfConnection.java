package ru.aplix.ltk.core;

import ru.aplix.ltk.core.collector.DefaultRfTracker;
import ru.aplix.ltk.core.collector.RfCollector;
import ru.aplix.ltk.core.collector.RfTracker;
import ru.aplix.ltk.core.reader.RfReader;
import ru.aplix.ltk.core.reader.RfReaderDriver;


/**
 * RFID reader connection.
 *
 * <p>It can opened by {@link RfConnector#open() RFID connector} after proper
 * configuration.</p>
 *
 * <p>Connections are not expected to be thread-safe, in contrast to services
 * they create.</p>
 */
public abstract class RfConnection {

	private RfCollector collector;
	private RfTracker tracker;
	private RfReader reader;
	private RfReaderDriver readerDriver;

	/**
	 * RFID tags collector.
	 *
	 * <p>Construct the collector by calling {@link #createCollector()} method
	 * if not constructed yet.</p>
	 *
	 * @return collector instance.
	 */
	public final RfCollector collector() {
		if (this.collector != null) {
			return this.collector;
		}
		return this.collector = createCollector();
	}

	/**
	 * RFID tags tracker.
	 *
	 * <p>Construct the tracker by calling {@link #createTracker()} method if
	 * not constructed yet.</p>
	 *
	 * @return tracker instance.
	 */
	public final RfTracker tracker() {
		if (this.tracker != null) {
			return this.tracker;
		}
		return this.tracker = createTracker();
	}

	/**
	 * RFID reader.
	 *
	 * <p>Construct the reader by calling {@link #createReader()} method if not
	 * constructed yet.</p>
	 *
	 * @return reader instance.
	 */
	public final RfReader reader() {
		if (this.reader != null) {
			return this.reader;
		}
		return this.reader = createReader();
	}

	/**
	 * RFID reader driver.
	 *
	 * <p>Constructs the driver by calling {@link #createReaderDriver()} method
	 * if not constructed yet.</p>
	 *
	 * @return reader driver instance.
	 */
	public final RfReaderDriver readerDriver() {
		if (this.readerDriver != null) {
			return this.readerDriver;
		}
		return this.readerDriver = createReaderDriver();
	}

	/**
	 * Creates new RFID collector.
	 *
	 * <p>This method is called at most once from {@link #collector()}
	 * method.</p>
	 *
	 * <p>By default, creates standard collector for the given
	 * {@link #reader() reader} using the given {@link #tracker()}
	 * tracker.</p>
	 *
	 * @return new collector instance.
	 */
	protected RfCollector createCollector() {
		return new RfCollector(reader(), tracker());
	}

	/**
	 * Creates new RFID tags tracker to use by collector.
	 *
	 * <p>This method is called at most once from {@link #tracker()} method.
	 * </p>
	 *
	 * <p>By default, constructs a {@link DefaultRfTracker default tracker}
	 * instance with default settings.</p>
	 *
	 * @return new tracker instance.
	 */
	protected RfTracker createTracker() {
		return new DefaultRfTracker();
	}

	/**
	 * Creates new RFID reader.
	 *
	 * <p>This method is called at most once from {@link #reader()} method.
	 * </p>
	 *
	 * <p>By default, creates standard reader with the given
	 * {@link #readerDriver() driver}.</p>
	 *
	 * @return new reader instance.
	 */
	protected RfReader createReader() {
		return new RfReader(readerDriver());
	}

	/**
	 * Constructs new RFID reader driver.
	 *
	 * <p>This method is called at most once from {@link #readerDriver()}
	 * method.</p>
	 *
	 * @return new driver.
	 */
	protected abstract RfReaderDriver createReaderDriver();

}
