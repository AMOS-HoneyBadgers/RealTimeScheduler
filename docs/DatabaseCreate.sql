-- Table: public.groups

-- DROP TABLE public.groups;

CREATE TABLE public.groups
(
    id character varying COLLATE pg_catalog."default" NOT NULL,
    "parentId" character varying COLLATE pg_catalog."default",
    priority integer NOT NULL,
    "typeFlag" character varying COLLATE pg_catalog."default" NOT NULL,
    "maxFailures" character varying COLLATE pg_catalog."default" NOT NULL,
    mode character varying COLLATE pg_catalog."default" NOT NULL,
    paused boolean,
    CONSTRAINT groups_pkey PRIMARY KEY (id),
    CONSTRAINT parent FOREIGN KEY ("parentId")
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
    "groupId" character varying COLLATE pg_catalog."default" NOT NULL,
    priority integer NOT NULL,
    "earliestStart" timestamp without time zone NOT NULL,
    "latestStart" timestamp without time zone NOT NULL,
    "workingDays" integer NOT NULL,
    "typeFlag" character varying COLLATE pg_catalog."default" NOT NULL,
    "maxFailures" integer NOT NULL,
    mode character varying COLLATE pg_catalog."default" NOT NULL,
    "indexNumber" bigint,
    force boolean,
    "parallelismDegree" integer,
    "metaData" jsonb,
    CONSTRAINT tasks_pkey PRIMARY KEY (id),
    CONSTRAINT group_fk FOREIGN KEY ("groupId")
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