package ru.aplix.ltk.message;


/**
 * Message service.
 *
 * <p>Subscribe to service messages to gain a {@link MsgServiceHandle service
 * subscription handle}. This handle can be used to subscribe to other message
 * types.</p>
 *
 * @param <H>
 * @param <M>
 */
public abstract class MsgService<H extends MsgServiceHandle<H, M>, M> {

	private final ServiceSubscriptions serviceSubscriptions =
			new ServiceSubscriptions();

	/**
	 * Subscribe to this service.
	 *
	 * @param consumer service consumer.
	 *
	 * @return subscription handle.
	 */
	public final H subscribe(MsgConsumer<? super H, ? super M> consumer) {
		return serviceSubscriptions().subscribe(consumer);
	}

	/**
	 * Service subscriptions.
	 *
	 * @return all subscriptions to this service.
	 */
	protected MsgSubscriptions<H, M> serviceSubscriptions() {
		return this.serviceSubscriptions;
	}

	/**
	 * Invoked to create this service subscription handle.
	 *
	 * @param consumer service subscriber.
	 *
	 * @return new service subscription handle.
	 */
	protected abstract H createServiceHandle(
			MsgConsumer<? super H, ? super M> consumer);

	/**
	 * Starts the service.
	 *
	 * <p>This is invoked after the first subscription.</p>
	 */
	protected abstract void startService();

	/**
	 * Invoked on service subscription.
	 *
	 * <p>Does nothing by default.</p>
	 *
	 * @param handle subscription handle.
	 */
	protected void subscribed(H handle) {
	}

	/**
	 * Receives the message before any subscriber.
	 *
	 * <p>Does nothing by default.</p>
	 *
	 * @param message message received.
	 *
	 * @see MsgSubscriptions#messageReceived(Object)
	 */
	protected void messageReceived(M message) {
	}

	/**
	 * Invoked after service subscription is revoked.
	 *
	 * <p>Does nothing by default.</p>
	 *
	 * @param handle revoked subscription handle.
	 */
	protected void unsubscribed(H handle) {
	}

	/**
	 * Stops the service.
	 *
	 * <p>This is invoked when the last subscription revoked.</p>
	 */
	protected abstract void stopService();

	private final class ServiceSubscriptions extends MsgSubscriptions<H, M> {

		@Override
		protected H createHandle(MsgConsumer<? super H, ? super M> consumer) {
			return createServiceHandle(consumer);
		}

		@Override
		protected void firstSubscribed(H handle) {
			startService();
			super.firstSubscribed(handle);
		}

		@Override
		protected void subscribed(H handle) {
			super.subscribed(handle);
			MsgService.this.subscribed(handle);
		}

		@Override
		protected void messageReceived(M message) {
			super.messageReceived(message);
			MsgService.this.messageReceived(message);
		}

		@Override
		protected void unsubscribed(H handle) {
			handle.unsubscribeAll();
			super.unsubscribed(handle);
			MsgService.this.unsubscribed(handle);
		}

		@Override
		protected void lastUnsubscribed(H handle) {
			super.lastUnsubscribed(handle);
			stopService();
		}

	}

}
