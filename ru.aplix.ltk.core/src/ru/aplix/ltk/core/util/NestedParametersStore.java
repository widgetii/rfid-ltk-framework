package ru.aplix.ltk.core.util;

import java.util.*;
import java.util.Map.Entry;


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
	public Iterator<Entry<String, String[]>> iterator() {
		return new PrefixedIt(getPrefix(), getParent().iterator());
	}

	@Override
	public String[] getParam(String name) {
		return getParent().getParam(getPrefix() + name);
	}

	@Override
	public String[] setParam(String name, String[] values) {
		return getParent().setParam(getPrefix() + name, values);
	}

	private static final class PrefixedIt
			implements Iterator<Map.Entry<String, String[]>> {

		private final String prefix;
		private final Iterator<Map.Entry<String, String[]>> it;
		private Map.Entry<String, String[]> next;

		PrefixedIt(String prefix, Iterator<Entry<String, String[]>> it) {
			this.prefix = prefix;
			this.it = it;
		}

		@Override
		public boolean hasNext() {
			return this.next != null || findNext() != null;
		}

		@Override
		public Entry<String, String[]> next() {

			final Entry<String, String[]> next = this.next;

			if (next != null) {
				this.next = null;
				return next;
			}

			final Entry<String, String[]> found = findNext();

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

		private Map.Entry<String, String[]> findNext() {
			while (this.it.hasNext()) {

				final Entry<String, String[]> next = this.it.next();

				if (next.getKey().startsWith(this.prefix)) {
					return this.next = next;
				}
			}
			return this.next = null;
		}

	}

}
