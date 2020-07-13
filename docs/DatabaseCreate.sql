
-----------------------------------------------------
-- Please replace all occurrences of <OWNER> with the user you want to be the owner of the tables
-----------------------------------------------------


-- Functions

CREATE FUNCTION public.reduce_dim(anyarray)
    RETURNS SETOF anyarray AS
$function$
DECLARE
    s $1%type;
BEGIN
    FOREACH s SLICE 1  IN ARRAY $1 LOOP
            RETURN NEXT s;
        END LOOP;
    RETURN;
END;
$function$
    LANGUAGE plpgsql IMMUTABLE;


-- Table: public."group"

-- DROP TABLE public."group";

CREATE TABLE public."group"
(
    id character varying(128) COLLATE pg_catalog."default" NOT NULL,
    parent_id character varying(128) COLLATE pg_catalog."default",
    priority integer NOT NULL,
    deadline timestamp without time zone,
    active_times jsonb,
    working_days integer[],
    type_flag character varying COLLATE pg_catalog."default" NOT NULL,
    mode character varying COLLATE pg_catalog."default" NOT NULL,
    last_index_number bigint,
    parallelism_degree integer,
    current_parallelism_degree integer NOT NULL DEFAULT 0,
    CONSTRAINT group_pkey PRIMARY KEY (id),
    CONSTRAINT parent_fk FOREIGN KEY (parent_id)
        REFERENCES public."group" (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
    WITH (
        OIDS = FALSE
    )
    TABLESPACE pg_default;

ALTER TABLE public."group"
    OWNER to <OWNER>;



-- Table: public.task

-- DROP TABLE public.task;

CREATE TABLE public.task
(
    id character varying(36) COLLATE pg_catalog."default" NOT NULL,
    group_id character varying(128) COLLATE pg_catalog."default" NOT NULL,
    priority integer NOT NULL,
    deadline timestamp without time zone,
    active_times jsonb,
    working_days integer[],
    status character varying COLLATE pg_catalog."default" NOT NULL,
    type_flag character varying COLLATE pg_catalog."default" NOT NULL,
    mode character varying COLLATE pg_catalog."default" NOT NULL,
    retries integer,
    force boolean NOT NULL,
    index_number bigint,
    meta_data jsonb,
    total_priority bigint,
    history jsonb,
    CONSTRAINT task_pkey PRIMARY KEY (id),
    CONSTRAINT group_fk FOREIGN KEY (group_id)
        REFERENCES public."group" (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
    WITH (
        OIDS = FALSE
    )
    TABLESPACE pg_default;

ALTER TABLE public.task
    OWNER to <OWNER>;



-- Table: public.paused

-- DROP TABLE public.paused;

CREATE TABLE public.paused
(
    id character varying(256) COLLATE pg_catalog."default" NOT NULL,
    resume_date timestamp without time zone,
    CONSTRAINT paused_pkey PRIMARY KEY (id)
)
    WITH (
        OIDS = FALSE
    )
    TABLESPACE pg_default;

ALTER TABLE public.paused
    OWNER to <OWNER>;


-- TODO for later the lock table
-- Table: public.lock

-- DROP TABLE public.lock;
-- For now no foreign key, but: https://stackoverflow.com/questions/47550419/locks-on-updating-rows-with-foreign-key-constraint
CREATE TABLE public.lock
(
    id character varying(256) COLLATE pg_catalog."default" NOT NULL,
    is_dispatched boolean NOT NULL DEFAULT false,
    CONSTRAINT lock_pkey PRIMARY KEY (id)
)
    WITH (
        OIDS = FALSE
    )
    TABLESPACE pg_default;

ALTER TABLE public.lock
    OWNER to <OWNER>;


-- Insert Default group
INSERT INTO public."group" (id, priority, type_flag, mode, parallelism_degree) VALUES ('DEFAULT_GROUP', 1, 'Batch', 'Parallel', 100);