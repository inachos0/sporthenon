-- Table: "YEAR"

-- DROP TABLE "YEAR";

CREATE TABLE "YEAR"
(
  id integer NOT NULL,
  label character varying(4) NOT NULL,
  id_member integer NOT NULL,
  last_update timestamp without time zone NOT NULL DEFAULT now(),
  first_update timestamp without time zone NOT NULL DEFAULT now(),
  ref smallint,
  CONSTRAINT "YEAR_pkey" PRIMARY KEY (id),
  CONSTRAINT "YEAR_id_member_fkey" FOREIGN KEY (id_member)
      REFERENCES "~MEMBER" (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE SET NULL,
  CONSTRAINT "YEAR_label_key" UNIQUE (label)
)
WITH (
  OIDS=FALSE
);