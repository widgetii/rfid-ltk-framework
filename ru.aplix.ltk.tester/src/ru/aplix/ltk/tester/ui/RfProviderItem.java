package ru.aplix.ltk.tester.ui;

import ru.aplix.ltk.core.RfProvider;


public class RfProviderItem implements Comparable<RfProviderItem> {

	private final RfProvider provider;
	private final String name;
	private final int index;
	private int itemIndex = -1;

	RfProviderItem(RfProvider provider, int index) {
		this.provider = provider;
		this.name = providerName(provider, index);
		this.index = index;
	}

	public final RfProvider getProvider() {
		return this.provider;
	}

	public final String getName() {
		return this.name;
	}

	public final int getItemIndex() {
		return this.itemIndex;
	}

	@Override
	public int compareTo(RfProviderItem o) {

		final int cmp = this.name.compareTo(o.name);

		if (cmp != 0) {
			return cmp;
		}

		return this.index - o.index;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;

		result = prime * result + this.index;
		result = prime * result + this.name.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		final RfProviderItem other = (RfProviderItem) obj;

		if (this.index != other.index) {
			return false;
		}
		if (!this.name.equals(other.name)) {
			return false;
		}

		return true;
	}

	private static String providerName(RfProvider provider, int index) {

		final String name = provider.getName();

		if (name != null) {
			return name;
		}

		return "#" + index;
	}

	final void setItemIndex(int itemIndex) {
		this.itemIndex = itemIndex;
	}

}
