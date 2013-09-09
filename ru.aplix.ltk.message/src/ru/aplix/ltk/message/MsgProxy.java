package ru.aplix.ltk.message;

import static java.util.Objects.requireNonNull;


/**
 * Message proxy.
 *
 * <p>This is a message consumer implementation, which proxies messages to
 * another one. By default, this implementation just passes all implemented
 * methods calls to the proxied consumer, unmodified.</p>
 *
 * @param <H> subscription handle type.
 * @param <M> message type.
 */
public abstract class MsgProxy<H extends MsgHandle<H, M>, M>
		implements MsgConsumer<H, M> {

	private final MsgConsumer<H, M> proxied;

	/**
	 * Constructs a proxy.
	 *
	 * @param proxied proxied message consumer.
	 */
	public MsgProxy(MsgConsumer<H, M> proxied) {
		requireNonNull(proxied, "Proxied message consumer not specified");
		this.proxied = proxied;
	}

	/**
	 * Proxied message consumer.
	 *
	 * @return the message consumer passed to constructor.
	 */
	public final MsgConsumer<H, M> getProxied() {
		return this.proxied;
	}

	@Override
	public void consumerSubscribed(H handle) {
		getProxied().consumerSubscribed(handle);
	}

	@Override
	public void consumerUnsubscribed(H handle) {
		getProxied().consumerUnsubscribed(handle);
	}

}
