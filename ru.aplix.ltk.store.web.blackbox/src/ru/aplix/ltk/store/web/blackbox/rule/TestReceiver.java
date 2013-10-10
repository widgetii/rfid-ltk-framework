package ru.aplix.ltk.store.web.blackbox.rule;

import java.net.URL;

import org.junit.rules.ExternalResource;

import ru.aplix.ltk.collector.http.client.HttpRfSettings;
import ru.aplix.ltk.store.RfReceiver;
import ru.aplix.ltk.store.RfReceiverEditor;
import ru.aplix.ltk.store.web.blackbox.BlackboxRunner;


public class TestReceiver extends ExternalResource {

	private final String profileId;
	private final boolean active;
	private RfReceiver<HttpRfSettings> rfReceiver;

	public TestReceiver(String profileId, boolean active) {
		this.profileId = profileId;
		this.active = active;
	}

	public final RfReceiver<HttpRfSettings> getRfReceiver() {
		return this.rfReceiver;
	}

	public CollectorConsumer subscribe() {

		final CollectorConsumer consumer = new CollectorConsumer();

		getRfReceiver().getRfCollector().subscribe(consumer);

		return consumer;
	}

	@Override
	protected void before() throws Throwable {

		final BlackboxRunner runner = BlackboxRunner.getInstance();
		final RfReceiverEditor<HttpRfSettings> editor =
				runner.getRfStore().newRfReceiver(runner.getRfProvider());

		create(editor);

		this.rfReceiver = editor.save();
	}

	protected void create(
			RfReceiverEditor<HttpRfSettings> editor)
	throws Throwable {

		final HttpRfSettings settings = editor.getRfSettings();

		settings.setClientURL(
				new URL("http://localhost:18080/rfid/clr-client"));
		settings.setCollectorURL(
				new URL("http://localhost:28080/collector/" + this.profileId));

		editor.setActive(this.active);
	}

	@Override
	protected void after() {
		try {
			this.rfReceiver.delete();
		} finally {
			this.rfReceiver = null;
		}
	}

}
