package ru.aplix.ltk.driver.log.test;

import java.io.IOException;
import java.nio.channels.FileChannel;

import org.junit.rules.ExternalResource;

import ru.aplix.ltk.driver.log.*;


public abstract class TestReader
		extends ExternalResource
		implements CyclicLogClient {

	private final TestLog log = new TestLog() {
		@Override
		protected void configure(CyclicLogConfig config) {
			TestReader.this.configure(config);
		}
	};
	private CyclicLogReader reader;
	private boolean exhausted;

	public final TestLog log() {
		return this.log;
	}

	public final CyclicLogReader reader() {
		return this.reader;
	}

	public final FileChannel channel() {
		return reader().channel();
	}

	public final boolean isExhausted() {
		return this.exhausted;
	}

	public final void resetExhaust() {
		this.exhausted = false;
	}

	public final int read() throws IOException {
		return reader().read().getInt();
	}

	public final int pull() throws IOException {
		return reader().pull().getInt();
	}

	public final boolean seek(int toFind) throws IOException {
		return reader().seek(new TestLogFilter(toFind));
	}

	@Override
	public void readerExhausted(CyclicLogReader reader) {
		this.exhausted = true;
	}

	@Override
	protected void before() throws Throwable {
		this.log.before();
		resetExhaust();
		this.reader = log().log().read(this);
	}

	protected abstract void configure(CyclicLogConfig config);

	@Override
	protected void after() {
		try {
			this.reader.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			this.log.after();
		}
	}

	private static final class TestLogFilter implements CyclicLogFilter {

		private final int toFind;

		TestLogFilter(int toFind) {
			this.toFind = toFind;
		}

		@Override
		public int filterRecord(CyclicLogReader reader) throws IOException {

			final int value = reader.read().getInt();

			return value - this.toFind;
		}

	}

}
