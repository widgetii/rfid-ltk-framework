package ru.aplix.ltk.collector.http.server;

import java.util.UUID;

import javax.servlet.ServletContext;

import org.osgi.framework.BundleContext;

import ru.aplix.ltk.collector.http.CollectorClientRequest;


public class ClrClients {

	public ClrClients(
			BundleContext bundleContext,
			ServletContext servletContext) {
	}

	public ClrClient create(CollectorClientRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	public ClrClient update(UUID id, CollectorClientRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	public void delete(UUID id) {
		// TODO Auto-generated method stub
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

}
