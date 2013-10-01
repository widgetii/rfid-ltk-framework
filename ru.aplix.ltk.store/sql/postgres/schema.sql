CREATE SCHEMA IF NOT EXISTS rfstore;

CREATE TABLE IF NOT EXISTS rfstore.receiver (
	id serial PRIMARY KEY,
	provider text NOT NULL,
	active boolean NOT NULL,
	settings text NOT NULL
) WITHOUT OIDS;
