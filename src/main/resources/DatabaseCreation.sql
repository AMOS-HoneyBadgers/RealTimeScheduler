-- Table: public.task

-- DROP TABLE public.task;

CREATE TABLE public.task
(
    id bigint NOT NULL,
    priority integer NOT NULL,
    name character varying COLLATE pg_catalog."default",
    submittimestamp timestamp without time zone,
    CONSTRAINT task_pkey PRIMARY KEY (id)
)
    WITH (
        OIDS = FALSE
    )
    TABLESPACE pg_default;

ALTER TABLE public.task
    OWNER to realtimescheduler;


-- Table: public."user"

-- DROP TABLE public."user";

CREATE TABLE public."user"
(
    id character varying COLLATE pg_catalog."default" NOT NULL,
    name character varying COLLATE pg_catalog."default",
    role character varying COLLATE pg_catalog."default",
    age integer,
    CONSTRAINT user_pkey PRIMARY KEY (id)
)
    WITH (
        OIDS = FALSE
    )
    TABLESPACE pg_default;

ALTER TABLE public."user"
    OWNER to realtimescheduler;