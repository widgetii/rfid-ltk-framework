CREATE SCHEMA IF NOT EXISTS rfstore;

CREATE TABLE IF NOT EXISTS rfstore.receiver (
	id serial PRIMARY KEY,
	provider text NOT NULL,
	active boolean NOT NULL,
	settings text NOT NULL
) WITHOUT OIDS;

CREATE TABLE IF NOT EXISTS rfstore.tag_event (
	id bigserial PRIMARY KEY,
	receiver_id int NOT NULL,
	event_id bigint,
	initial_event boolean NOT NULL DEFAULT false,
	tag text NOT NULL,
	timestamp timestamp with time zone NOT NULL,
	appeared boolean NOT NULL
) WITHOUT OIDS;

CREATE INDEX tag_event_tag_idx ON rfstore.tag_event (
	tag,
	timestamp DESC,
	id DESC);

CREATE INDEX tag_event_receiver_tag_idx ON rfstore.tag_event (
	receiver_id,
	tag,
	timestamp DESC,
	id DESC);

CREATE INDEX tag_event_timestamp_idx ON rfstore.tag_event (
	timestamp DESC,
	id DESC);
	
CREATE INDEX tag_event_receiver_timestamp_idx ON rfstore.tag_event (
	receiver_id,
	timestamp DESC,
	id DESC);

CREATE INDEX tag_event_receiver_event_idx ON rfstore.tag_event (
	receiver_id,
	event_id);

CREATE OR REPLACE VIEW rfstore.tag_event_with_next AS
SELECT
	*,
	lag(id) OVER (
		PARTITION BY receiver_id, tag
		ORDER BY id DESC
	) as next_event_id
FROM rfstore.tag_event;

CREATE OR REPLACE VIEW rfstore.visible_tag AS
SELECT * FROM rfstore.tag_event_with_next
WHERE appeared and next_event_id is null;
	
CREATE OR REPLACE FUNCTION rfstore.hide_visible_tags (
	target_receiver_id int,
	initial_event_id bigint,
	initial_event_timestamp timestamp with time zone
) RETURNS integer
LANGUAGE sql AS
$FN$
	INSERT INTO rfstore.tag_event (
		receiver_id,
		tag,
		timestamp,
		appeared
	) SELECT
		target_receiver_id,
		v.tag,
		initial_event_timestamp,
		false
	FROM rfstore.visible_tag v
	WHERE
		v.receiver_id = target_receiver_id
		and v.event_id < initial_event_id;
	SELECT 1;
$FN$;
