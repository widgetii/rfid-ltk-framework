package ru.aplix.ltk.core.util;

import java.util.Iterator;
import java.util.NoSuchElementException;


class NestedParametersStore implements ParametersStore {

	private final ParametersStore parent;
	private final String prefix;

	NestedParametersStore(ParametersStore parent, String prefix) {
		this.parent = parent;
		this.prefix = prefix;
	}

	public final ParametersStore getParent() {
		return this.parent;
	}

	public final String getPrefix() {
		return this.prefix;
	}

	@Override
	public Iterator<String> iterator() {
		return new PrefixedIt(getPrefix(), getParent().iterator());
	}

	@Override
	public String[] getParam(String name) {
		return getParent().getParam(name(name));
	}

	@Override
	public String[] setParam(String name, String[] values) {
		return getParent().setParam(name(name), values);
	}

	@Override
	public ParametersStore nestedStore(String prefix) {
		return null;
	}

	private String name(String name) {

		final String prefix = getPrefix();

		if (!name.isEmpty()) {
			return prefix + name;
		}

		// Remove the last dot.
		return prefix.substring(0, prefix.length() - 1);
	}

	private static final class PrefixedIt
			implements Iterator<String> {

		private final String prefix;
		private final Iterator<String> it;
		private String next;

		PrefixedIt(String prefix, Iterator<String> it) {
			this.prefix = prefix;
			this.it = it;
		}

		@Override
		public boolean hasNext() {
			return this.next != null || findNext() != null;
		}

		@Override
		public String next() {

			final String next = this.next;

			if (next != null) {
				this.next = null;
				return next;
			}

			final String found = findNext();

			if (found == null) {
				throw new NoSuchElementException();
			}

			return found;
		}

		@Override
		public void remove() {
			if (this.next != null) {
				this.next = null;
				this.it.remove();
			}
			throw new IllegalStateException("Nothing to remove");
		}

		private String findNext() {
			while (this.it.hasNext()) {

				final String next = this.it.next();

				if (next.startsWith(this.prefix)) {
					return this.next = next;
				}
			}
			return this.next = null;
		}

	}

}
