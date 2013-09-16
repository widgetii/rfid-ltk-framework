package ru.aplix.ltk.store.impl;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.store.RfReceiver;
import ru.aplix.ltk.store.RfStore;


@Component("rfStore")
public class RfStoreImpl implements RfStore {

	private final AtomicInteger idSeq = new AtomicInteger();
	private final ConcurrentHashMap<Integer, RfReceiverImpl<?>> receivers =
			new ConcurrentHashMap<>();

	@Override
	public Collection<? extends RfReceiverImpl<?>> allRfReceivers() {
		return this.receivers.values();
	}

	@Override
	public RfReceiver<?> rfReceiverById(int id) {
		return this.receivers.get(id);
	}

	@Override
	public <S extends RfSettings> RfReceiverEditorImpl<S> newRfReceiver(
			RfProvider<S> provider) {
		return new RfReceiverEditorImpl<>(this, provider);
	}

	<S extends RfSettings> RfReceiverImpl<S> saveReceiver(
			RfReceiverEditorImpl<S> editor) {

		final RfReceiverImpl<S> editedReceiver = editor.getRfReceiver();
		final RfReceiverImpl<S> receiver;

		if (editedReceiver != null) {
			receiver = editedReceiver;
		} else {
			receiver = new RfReceiverImpl<>(
					this,
					this.idSeq.incrementAndGet(),
					editor.getRfProvider());
		}

		receiver.update(editor);

		if (editedReceiver == null) {
			this.receivers.put(receiver.getId(), receiver);
		}

		return receiver;
	}

	void deleteReceiver(RfReceiverImpl<?> receiver) {
		this.receivers.remove(receiver.getId());
	}

}
