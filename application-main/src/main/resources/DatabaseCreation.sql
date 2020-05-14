
-- Role: realtimescheduler
-- DROP ROLE realtimescheduler;

CREATE ROLE realtimescheduler WITH
    LOGIN
    NOSUPERUSER
    INHERIT
    CREATEDB
    NOCREATEROLE
    NOREPLICATION;


-- Database: AMOS

-- DROP DATABASE "AMOS";

CREATE DATABASE "AMOS"
    WITH
    OWNER = realtimescheduler
    ENCODING = 'UTF8'
    LC_COLLATE = 'German_Germany.1252'
    LC_CTYPE = 'German_Germany.1252'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;





-- ################################# TABLES


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



-- Table: public.task

-- DROP TABLE public.task;

CREATE TABLE public.task
(
    id bigint NOT NULL,
    priority integer NOT NULL,
    name character varying COLLATE pg_catalog."default",
    submittimestamp timestamp without time zone,
    userid character varying COLLATE pg_catalog."default",
    CONSTRAINT task_pkey PRIMARY KEY (id),
    CONSTRAINT fk_user FOREIGN KEY (userid)
        REFERENCES public."user" (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
    WITH (
        OIDS = FALSE
    )
    TABLESPACE pg_default;

ALTER TABLE public.task
    OWNER to realtimescheduler;



-- Table: public.role

-- DROP TABLE public.role;

CREATE TABLE public.role
(
    id character varying COLLATE pg_catalog."default" NOT NULL,
    name character varying COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT role_pkey PRIMARY KEY (id)
)
    WITH (
        OIDS = FALSE
    )
    TABLESPACE pg_default;

ALTER TABLE public.role
    OWNER to realtimescheduler;



-- Table: public.rt_task_role

-- DROP TABLE public.rt_task_role;

CREATE TABLE public.rt_task_role
(
    taskid bigint NOT NULL,
    roleid character varying COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT rt_task_role_pkey PRIMARY KEY (taskid, roleid),
    CONSTRAINT fk_role FOREIGN KEY (roleid)
        REFERENCES public.role (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_task FOREIGN KEY (taskid)
        REFERENCES public.task (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
    WITH (
        OIDS = FALSE
    )
    TABLESPACE pg_default;

ALTER TABLE public.rt_task_role
    OWNER to realtimescheduler;
