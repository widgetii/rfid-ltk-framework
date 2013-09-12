package ru.aplix.ltk.store;

import ru.aplix.ltk.core.RfProvider;
import ru.aplix.ltk.core.RfSettings;


public interface RfStore<S extends RfSettings> {

	int getId();

	RfProvider<S> getRfProvider();

	S getRfSettings();

	void setRfSettings(S settings);

	void save();

	void delete();

}
