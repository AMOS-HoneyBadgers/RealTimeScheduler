-- Table: public."group"

-- DROP TABLE public."group";

CREATE TABLE test_group
(
    id character varying(128) NOT NULL,
    parent_id character varying(128),
    priority integer NOT NULL,
    deadline timestamp without time zone,
    active_times text,
    working_days integer[],
    type_flag character varying NOT NULL,
    mode character varying NOT NULL,
    paused boolean NOT NULL,
    last_index_number bigint,
    parallelism_degree integer,
    CONSTRAINT group_pkey PRIMARY KEY (id),
    CONSTRAINT parent_fk FOREIGN KEY (parent_id)
        REFERENCES test_group (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);



-- Table: public.task

-- DROP TABLE public.task;

CREATE TABLE task
(
    id character varying(36) NOT NULL,
    group_id character varying(128) NOT NULL,
    priority integer NOT NULL,
    deadline timestamp without time zone,
    active_times text,
    working_days integer[],
    status character varying NOT NULL,
    type_flag character varying NOT NULL,
    mode character varying NOT NULL,
    retries integer,
    paused boolean NOT NULL,
    force boolean NOT NULL,
    index_number bigint,
    meta_data text,
    CONSTRAINT task_pkey PRIMARY KEY (id),
    CONSTRAINT group_fk FOREIGN KEY (group_id)
        REFERENCES test_group (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);