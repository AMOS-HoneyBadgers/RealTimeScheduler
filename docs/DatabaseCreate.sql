-- Table: public.groups

-- DROP TABLE public.groups;

CREATE TABLE public.groups
(
    id character varying COLLATE pg_catalog."default" NOT NULL,
    "parent_id" character varying COLLATE pg_catalog."default",
    priority integer NOT NULL,
    "type_flag" character varying COLLATE pg_catalog."default" NOT NULL,
    "max_failures" character varying COLLATE pg_catalog."default" NOT NULL,
    mode character varying COLLATE pg_catalog."default" NOT NULL,
    paused boolean,
    CONSTRAINT groups_pkey PRIMARY KEY (id),
    CONSTRAINT parent FOREIGN KEY ("parent_id")
        REFERENCES public.groups (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
    WITH (
        OIDS = FALSE
    )
    TABLESPACE pg_default;

ALTER TABLE public.groups
    OWNER to realtimescheduler;


-- Table: public.tasks

-- DROP TABLE public.tasks;

CREATE TABLE public.tasks
(
    id character varying COLLATE pg_catalog."default" NOT NULL,
    "group_id" character varying COLLATE pg_catalog."default" NOT NULL,
    priority integer NOT NULL,
    "earliest_start" timestamp without time zone NOT NULL,
    "latest_start" timestamp without time zone NOT NULL,
    "working_days" integer NOT NULL,
    "type_flag" character varying COLLATE pg_catalog."default" NOT NULL,
    "max_failures" integer NOT NULL,
    mode character varying COLLATE pg_catalog."default" NOT NULL,
    "index_number" bigint,
    force boolean,
    "parallelism_degree" integer,
    "meta_data" jsonb,
    CONSTRAINT tasks_pkey PRIMARY KEY (id),
    CONSTRAINT group_fk FOREIGN KEY ("group_id")
        REFERENCES public.groups (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
    WITH (
        OIDS = FALSE
    )
    TABLESPACE pg_default;

ALTER TABLE public.tasks
    OWNER to realtimescheduler;