package ru.aplix.ltk.tester.ui;

import static ru.aplix.ltk.tester.ui.UIUtil.invokeInUI;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.*;

import ru.aplix.ltk.core.RfConnection;
import ru.aplix.ltk.core.collector.RfCollectorHandle;
import ru.aplix.ltk.core.collector.RfTagAppearanceHandle;
import ru.aplix.ltk.core.collector.RfTagAppearanceMessage;
import ru.aplix.ltk.core.source.RfStatusMessage;
import ru.aplix.ltk.message.MsgConsumer;


class ConnectionControls extends JPanel implements RfProviderItemListener {

	private static final long serialVersionUID = 8720255556710116692L;

	private final ConnectionSettings settings;
	private final JButton startButton;
	private final Action startAction;
	private final Action stopAction;
	private RfProviderItem running;
	private RfCollectorHandle collectorHandle;

	ConnectionControls(ConnectionSettings settings) {
		super(new FlowLayout(FlowLayout.RIGHT));
		this.settings = settings;
		this.startAction = new AbstractAction("Начать чтение") {
			private static final long serialVersionUID = 5307110595555127582L;
			@Override
			public void actionPerformed(ActionEvent e) {
				startRead();
			}
		};
		this.stopAction = new AbstractAction("Остановить чтение") {
			private static final long serialVersionUID = 4225706565687719025L;
			@Override
			public void actionPerformed(ActionEvent e) {
				stopRead();
			}
		};
		this.startButton = new JButton(this.startAction);
		this.startButton.setEnabled(false);
		add(this.startButton);
	}

	public final ConnectionSettings getSettings() {
		return this.settings;
	}

	public final TesterContent getContent() {
		return getSettings().getConnectionTab().getContent();
	}

	public final LogTab getLogTab() {
		return getSettings().getLogTab();
	}

	public final Action getStartAction() {
		return this.startAction;
	}

	public final Action getStopAction() {
		return this.stopAction;
	}

	public void start() {
		getSettings().getConnectionTab().addProviderItemListener(this);
		updateButton();
	}

	public void stop() {
		stopRead();
	}

	@Override
	public void itemSelected(RfProviderItem item) {
		updateButton();
	}

	@Override
	public void itemDeselected(RfProviderItem item) {
		updateButton();
	}

	private void startRead() {
		getContent().getProgressTab().clear();
		this.collectorHandle = null;
		this.running = null;
		try {

			final RfProviderItem selected =
					getSettings().getConnectionTab().getSelected();

			if (selected == null) {
				return;
			}

			final RfConnection connection =
					selected.getConnectorUI().buildConnector().connect();

			this.collectorHandle =
					connection.collector().subscribe(new StatusConsumer());
			this.running = selected;
		} catch (Throwable e) {
			getLogTab().log(e);
		} finally {
			updateButton();
		}
		getContent().openProgressTab();
	}

	private void stopRead() {
		if (this.collectorHandle != null) {
			this.collectorHandle.unsubscribe();
		}
	}

	private void readStopped() {
		this.collectorHandle = null;
		this.running = null;
		getLogTab().log("Соединение закрыто");
		updateButton();
	}

	private void updateButton() {
		if (this.running != null) {
			this.startButton.setAction(getStopAction());
			this.startButton.setEnabled(true);
			return;
		}

		this.startButton.setAction(getStartAction());
		this.startButton.setEnabled(
				getSettings().getConnectionTab().getSelected() != null);
	}

	private void statusUpdated(RfStatusMessage status) {
		if (!status.getRfStatus().isError()) {
			getLogTab().append("Соединено с " + status.getRfReaderId());
			return;
		}

		final Throwable cause = status.getCause();

		if (cause != null) {
			getLogTab().append(cause);
			return;
		}

		final String errorMessage = status.getErrorMessage();

		if (errorMessage == null) {
			getLogTab().append("ОШИБКА");
			return;
		}

		getLogTab().append("ОШИБКА: " + errorMessage);
	}

	private void tagAppearanceChanged(RfTagAppearanceMessage message) {

		final ProgressTab progressTab = getContent().getProgressTab();

		if (message.getAppearance().isPresent()) {
			progressTab.addTag(message.getRfTag());
		} else {
			progressTab.removeTag(message.getRfTag());
		}
	}

	private final class StatusConsumer
			implements MsgConsumer<RfCollectorHandle, RfStatusMessage> {

		@Override
		public void consumerSubscribed(RfCollectorHandle handle) {
			handle.requestTagAppearance(new TagsConsumer());
		}

		@Override
		public void messageReceived(final RfStatusMessage message) {
			invokeInUI(new Runnable() {
				@Override
				public void run() {
					statusUpdated(message);
				}
			});
		}

		@Override
		public void consumerUnsubscribed(RfCollectorHandle handle) {
			invokeInUI(new Runnable() {
				@Override
				public void run() {
					readStopped();
				}
			});
		}

	}

	private final class TagsConsumer implements MsgConsumer<
			RfTagAppearanceHandle,
			RfTagAppearanceMessage> {

		@Override
		public void consumerSubscribed(RfTagAppearanceHandle handle) {
		}

		@Override
		public void messageReceived(final RfTagAppearanceMessage message) {
			invokeInUI(new Runnable() {
				@Override
				public void run() {
					tagAppearanceChanged(message);
				}
			});
		}

		@Override
		public void consumerUnsubscribed(RfTagAppearanceHandle handle) {
		}

	}

}
