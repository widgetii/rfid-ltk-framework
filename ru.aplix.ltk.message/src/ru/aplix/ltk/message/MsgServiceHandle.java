package ru.aplix.ltk.message;

import java.util.Arrays;


/**
 * Message service subscription handle.
 *
 * <p>It can be used to subscribe to other message types. Such subscriptions
 * should be registered using {@link #addSubscription(MsgHandle)} method.
 * When this service subscription revoked, all subscriptions registered through
 * it are also revoked.</p>
 *
 * @param <H> service subscription handle type. Handle implementation class
 * should be derived from {@code H}.
 * @param <M> service message type.
 */
public abstract class MsgServiceHandle<H extends MsgServiceHandle<H, M>, M>
		extends MsgHandle<H, M> {

	private MsgHandle<?, ?>[] handles;

	public MsgServiceHandle(
			MsgService<H, M> service,
			MsgConsumer<? super H, ? super M> consumer) {
		super(service.serviceSubscriptions(), consumer);
	}

	/**
	 * Registers the subscription made through this handle.
	 *
	 * @param handle subscription handle.
	 *
	 * @return the same subscription handle.
	 */
	protected synchronized final
	<HH extends MsgHandle<HH, ?>> HH addSubscription(HH handle) {
		if (this.handles == null) {
			this.handles = new MsgHandle<?, ?>[] {handle};
		} else {

			final int len = this.handles.length;

			this.handles = Arrays.copyOf(this.handles, len + 1);
			this.handles[len] = handle;
		}
		handle.setServiceHandle(this);
		return handle;
	}

	final synchronized void removeHandle(MsgHandle<?, ?> handle) {
		if (this.handles == null) {
			return;
		}

		final int len = this.handles.length;

		if (len == 1) {
			if (this.handles[0] == handle) {
				this.handles = null;
			}
			return;
		}
		for (int i = this.handles.length - 1; i >= 0; --i) {
			if (this.handles[i] != handle) {
				continue;
			}

			final MsgHandle<?, ?>[] handles = new MsgHandle<?, ?>[len - 1];

			System.arraycopy(this.handles, 0, handles, 0, i);
			System.arraycopy(this.handles, i + 1, handles, i, len - i - 1);

			this.handles = handles;

			break;
		}
	}

	final void unsubscribeAll() {

		final MsgHandle<?, ?>[] handles;

		synchronized (this) {
			handles = this.handles;
			if (handles == null) {
				return;
			}
			this.handles = null;
		}

		for (MsgHandle<?, ?> handle : handles) {
			handle.unsubscribe();
		}
	}

}
