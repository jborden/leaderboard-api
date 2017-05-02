DROP TABLE IF EXISTS developers CASCADE;

CREATE TABLE developers (
       email varchar(200) CONSTRAINT must_be_different UNIQUE,
       api_key varchar(32) PRIMARY KEY,
       date_added bigint default extract(epoch from now()),
       UNIQUE(api_key,email)
);

DROP TABLE IF EXISTS games CASCADE;

CREATE TABLE games (
       game_key varchar(32) PRIMARY KEY,
       developer varchar(32) references developers(api_key),
       name varchar(200),
       date_added bigint default extract(epoch from now()),
       UNIQUE(game_key,developer)
);


DROP TABLE IF EXISTS scores CASCADE;

CREATE TABLE scores (
       id serial not null PRIMARY KEY,
       game varchar(32) references games(game_key),
       name varchar(200),
       game_key varchar(32) references games(game_key),
       score jsonb,
       date_added bigint default extract(epoch from now()),
       UNIQUE(id,game_key)
);
