DROP TABLE IF EXISTS developers CASCADE;

CREATE TABLE developers (
       email varchar(200) CONSTRAINT must_be_different UNIQUE,
       key varchar(32) PRIMARY KEY,
       created bigint default extract(epoch from now()),
       UNIQUE(key,email)
);

DROP TABLE IF EXISTS games CASCADE;

CREATE TABLE games (
       key varchar(32) PRIMARY KEY,
       developer varchar(32) references developers(key),
       name varchar(200) unique,
       created bigint default extract(epoch from now()),
       UNIQUE(key,developer,name)
);


DROP TABLE IF EXISTS scores CASCADE;

CREATE TABLE scores (
       id serial not null PRIMARY KEY,
       name varchar(200),
       game_key varchar(32) references games(key),
       score jsonb,
       created bigint default extract(epoch from now()),
       UNIQUE(id,game_key)
);
