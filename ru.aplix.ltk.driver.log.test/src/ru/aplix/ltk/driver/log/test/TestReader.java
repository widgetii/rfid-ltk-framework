package ru.aplix.ltk.driver.log.test;

import java.io.IOException;
import java.nio.channels.FileChannel;

import org.junit.rules.ExternalResource;

import ru.aplix.ltk.driver.log.CyclicLogConfig;
import ru.aplix.ltk.driver.log.CyclicLogFilter;
import ru.aplix.ltk.driver.log.CyclicLogReader;


public abstract class TestReader extends ExternalResource {

	private final TestLog log = new TestLog() {
		@Override
		protected void configure(CyclicLogConfig config) {
			TestReader.this.configure(config);
		}
	};
	private CyclicLogReader reader;

	public final TestLog log() {
		return this.log;
	}

	public final CyclicLogReader reader() {
		return this.reader;
	}

	public final FileChannel channel() {
		return reader().channel();
	}

	public final boolean seek(int toFind) throws IOException {
		return reader().seek(new TestLogFilter(toFind));
	}

	@Override
	protected void before() throws Throwable {
		this.log.before();
		this.reader = log().log().read();
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
