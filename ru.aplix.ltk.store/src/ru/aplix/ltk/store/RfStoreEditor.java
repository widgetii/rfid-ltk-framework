package ru.aplix.ltk.store;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;


public interface RfStoreEditor<S extends RfSettings> {

	RfProvider<S> getRfProvider();

	S getRfSettings();

	void setRfSettings(S settings);

	boolean isActive();

	void setActive(boolean active);

	RfStore<S> save();

}
