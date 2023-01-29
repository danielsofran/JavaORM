-- wbssb
CREATE TABLE public."TestTable1"
(
    "id" serial,
    "nume" varchar,
    "Moment" timestamp without time zone,
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS public."TestTable1"
    OWNER to postgres;

-- fk
create table "TestTable2"
(
    id        serial
        primary key,
    table1_id int
        constraint fk_testtable1
            references "TestTable1"(id)
);

-- fk2
CREATE TABLE contacts(
     contact_id INT GENERATED ALWAYS AS IDENTITY,
     customer_id INT,
     contact_name VARCHAR(255) NOT NULL,
     phone VARCHAR(15),
     email VARCHAR(100),
     PRIMARY KEY(contact_id),
     CONSTRAINT fk_customer
         FOREIGN KEY(customer_id)
             REFERENCES customers(customer_id),
    );
CREATE TABLE "Angajat"(
                          "salariu" DECIMAL,
                          "sef" INT,
                          "id" INT GENERATED ALWAYS AS IDENTITY,
                          "data" INT,
                          "nume" VARCHAR,

                          PRIMARY KEY("id"),
                          CONSTRAINT fk_Angajat_Persoana
                              FOREIGN KEY("sef")
                                  REFERENCES "Persoana"("id"),
                          CONSTRAINT fk_Angajat_MData
                              FOREIGN KEY("data")
                                  REFERENCES "MData"("id")
                          ON DELETE CASCADE
                              ON UPDATE CASCADE
);