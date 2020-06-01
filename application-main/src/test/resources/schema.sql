
CREATE domain IF NOT EXISTS jsonb AS other;

-- Table: public."group"

-- DROP TABLE public."group";

CREATE TABLE "group"
(
    id character varying(128) NOT NULL,
    parent_id character varying(128),
    priority integer NOT NULL,
    deadline timestamp without time zone,
    active_times jsonb,
    working_days ARRAY,
    type_flag character varying NOT NULL,
    mode character varying NOT NULL,
    paused boolean NOT NULL,
    last_index_number bigint,
    parallelism_degree integer
);

ALTER TABLE "group" ADD PRIMARY KEY (id);
ALTER TABLE "group" ADD FOREIGN KEY (parent_id) REFERENCES "group" (id);


-- Table: public.task

-- DROP TABLE public.task;

CREATE TABLE task
(
    id character varying(36) NOT NULL,
    group_id character varying(128) NOT NULL,
    priority integer NOT NULL,
    deadline timestamp without time zone,
    active_times jsonb,
    working_days ARRAY,
    status character varying NOT NULL,
    type_flag character varying NOT NULL,
    mode character varying NOT NULL,
    retries integer,
    paused boolean NOT NULL,
    force boolean NOT NULL,
    index_number bigint,
    meta_data jsonb
);

ALTER TABLE task ADD PRIMARY KEY (id);
ALTER TABLE task ADD FOREIGN KEY (group_id) REFERENCES "group" (id);