package ru.aplix.ltk.tester.ui;

import java.util.EventListener;


public interface RfProviderItemListener extends EventListener {

	void itemSelected(RfProviderItem<?> item);

	void itemDeselected(RfProviderItem<?> item);

}
