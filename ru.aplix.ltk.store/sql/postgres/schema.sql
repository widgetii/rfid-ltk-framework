CREATE SCHEMA IF NOT EXISTS rfstore;

CREATE TABLE IF NOT EXISTS rfstore.receiver (
	id serial PRIMARY KEY,
	provider text NOT NULL,
	active boolean NOT NULL,
	settings text NOT NULL
) WITHOUT OIDS;

CREATE TABLE IF NOT EXISTS rfstore.tag_event (
	receiver int NOT NULL,
	id bigint NOT NULL,
	tag text NOT NULL,
	timestamp timestamp with time zone NOT NULL,
	appeared boolean NOT NULL,
	PRIMARY KEY (receiver, id)
) WITHOUT OIDS;

CREATE INDEX tag_event_tag_idx ON rfstore.tag_event (tag);

CREATE INDEX tag_event_timestamp_idx ON rfstore.tag_event (timestamp);
