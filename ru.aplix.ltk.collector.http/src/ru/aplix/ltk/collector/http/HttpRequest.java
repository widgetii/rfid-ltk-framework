package ru.aplix.ltk.collector.http;


public interface HttpRequest {

	void decode(HttpParams params);

	HttpParams encode();

}
