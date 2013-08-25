package ru.aplix.ltk.message;

import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Message subscription handle.
 *
 * <p>This handle is created for each {@link MsgConsumer message consumer} by
 * when it is {@link MsgSubscriptions#subscribe(MsgConsumer)} on a messages.
 * </p>
 *
 * <p>This handle can be used to unsubscribe that consumer, or to perform other
 * provider-specific actions.</p>
 *
 * @param <H> subscription handle type. Handle implementation class should be
 * derived from {@code H}.
 * @param <M> message type.
 */
public abstract class MsgHandle<H extends MsgHandle<H, M>, M> {

	private final MsgSubscriptions<H, M> subscriptions;
	private final MsgConsumer<? super H, ? super M> consumer;
	private AtomicBoolean unsubscribed = new AtomicBoolean();
	private H prev;
	private H next;
	private MsgServiceHandle<?, ?> serviceHandle;

	public MsgHandle(
			MsgSubscriptions<H, M> subscriptions,
			MsgConsumer<? super H, ? super M> consumer) {
		this.subscriptions = subscriptions;
		this.consumer = consumer;
	}

	/**
	 * Message consumer this handle is created for.
	 *
	 * @return subscribed message consumer.
	 */
	public final MsgConsumer<? super H, ? super M> getConsumer() {
		return this.consumer;
	}

	/**
	 * Whether the message consumer is still subscribed.
	 *
	 * @return <code>false</code> after consumer is
	 * {@link #unsubscribe() unsubscribed}.
	 */
	public final boolean isSubscribed() {
		return !this.unsubscribed.get();
	}

	/**
	 * Unsubscribes the consumer from receiving messages.
	 *
	 * <p>If consumer is not subscribed, then does nothing.</p>
	 *
	 * <p>This method is thread safe.</p>
	 */
	public final void unsubscribe() {
		if (!this.unsubscribed.compareAndSet(false, true)) {
			return;
		}
		this.subscriptions.unsubscribe(self());
	}

	final void subscribed() {
		getConsumer().consumerSubscribed(self());
		if (this.serviceHandle != null) {
			this.serviceHandle.removeHandle(this);
		}
	}

	final void unsubscribed() {
		getConsumer().consumerUnsubscribed(self());
	}

	final H prev() {
		return this.prev;
	}

	final H next() {
		return this.next;
	}

	final void addNext(H next) {
		this.next = next;

		final MsgHandle<H, M> n = next;

		n.prev = self();
	}

	final void remove() {

		final H prev = this.prev;
		final H next = this.next;

		this.prev = null;
		this.next = null;
		if (prev != null) {

			final MsgHandle<H, M> p = prev;

			p.next = next;
		}
		if (next != null) {

			final MsgHandle<H, M> n = next;

			n.prev = prev;
		}
	}

	final void setServiceHandle(MsgServiceHandle<?, ?> serviceHandle) {
		this.serviceHandle = serviceHandle;
	}

	@SuppressWarnings("unchecked")
	private final H self() {
		return (H) this;
	}

}
