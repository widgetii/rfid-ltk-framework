package ru.aplix.ltk.core.util;

import java.util.ArrayList;
import java.util.Collection;


/**
 * A most specific handler selector for the given object.
 *
 * @param <H> handlers type.
 * @param <T> target object type.
 */
public abstract class HandlerSelector<T, H> {

	/**
	 * Selects a most specific handler for the given target.
	 *
	 * @param target target object.
	 * @param handlers handlers to select from.
	 *
	 * @return most specific handler, or <code>null</code> if not found.
	 */
	@SafeVarargs
	public final H selectHandlerFor(T target, H... handlers) {

		final ArrayList<H> matching = new ArrayList<>(handlers.length);

		for (H handler : handlers) {
			if (matchingHandler(target, handler)) {
				matching.add(handler);
			}
		}

		return selectFromMatching(target, matching);
	}

	/**
	 * Selects a most specific handler for the given target.
	 *
	 * @param target target object.
	 * @param handlers handlers collection to select from.
	 *
	 * @return most specific handler, or <code>null</code> if not found.
	 */
	public final H selectHandlerFor(
			T target,
			Collection<? extends H> handlers) {

		final ArrayList<H> matching = new ArrayList<>(handlers.size());

		for (H handler : handlers) {
			if (matchingHandler(target, handler)) {
				matching.add(handler);
			}
		}

		return selectFromMatching(target, matching);
	}

	/**
	 * Default handler to use if no other handlers match.
	 *
	 * @return <code>null</code> by default.
	 */
	public H getDefaultHandler() {
		return null;
	}

	/**
	 * Detects whether the given handler matches the target.
	 *
	 * @param target target object.
	 * @param handler handler to check.
	 *
	 * @return <code>true</code> if the {@code handler} matches the
	 * {@code target}, or <code>false</code> otherwise.
	 */
	public abstract boolean matchingHandler(T target, H handler);

	/**
	 * Determines the most specific handler for the given target.
	 *
	 * <p>This method is intended to be called for the handlers
	 * {@link #matchingHandler(Object, Object) matching} the target only.</p>
	 *
	 * @param target target object.
	 * @param handler1 first handler.
	 * @param handler2 second handle.
	 *
	 * @return most specific handler, or <code>null</code> if none can be
	 * selected.
	 */
	public abstract H mostSpecificHandler(T target, H handler1, H handler2);

	private H selectFromMatching(T object, ArrayList<H> matching) {

		final int size = matching.size();

		if (size > 1) {
			return selectMostSpecific(object, matching);
		}
		if (size != 0) {
			return matching.get(0);
		}

		final H defaultHandler = getDefaultHandler();

		if (defaultHandler == null) {
			return null;
		}
		if (!matchingHandler(object, defaultHandler)) {
			return null;
		}

		return defaultHandler;
	}

	private H selectMostSpecific(T target, ArrayList<H> pretenders) {

		final int size = pretenders.size();
		H handler = pretenders.get(0);

		for (int i = 1; i < size; ++i) {

			final H mostSpecific = mostSpecificHandler(
					target,
					handler,
					pretenders.get(i));

			if (mostSpecific != null) {
				handler = mostSpecific;
			}
		}

		return handler;
	}

}
