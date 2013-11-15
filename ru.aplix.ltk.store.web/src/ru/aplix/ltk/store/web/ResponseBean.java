package ru.aplix.ltk.store.web;


public class ResponseBean implements ErrorBean {

	private String error;

	@Override
	public String getError() {
		return this.error;
	}

	@Override
	public void setError(String error) {
		this.error = error;
	}

}
