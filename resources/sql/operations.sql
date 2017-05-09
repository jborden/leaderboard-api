-- name: create-developer<!
INSERT INTO developers (email,key) VALUES (:email,:key);
-- name: get-developer
SELECT email,key,created FROM developers WHERE email = :email;
-- name: create-game<!
INSERT INTO games (developer,name,key) VALUES (:developer,:name,:key);
-- name: get-game
SELECT key,name,created FROM games WHERE name = :name AND developer = :developer;
-- name: get-games
SELECT key,name,created FROM games WHERE developer = :developer;
-- name: create-score<!
INSERT INTO scores (score,name,game_key) VALUES (cast(:score as jsonb),:name,:game_key);
-- name: get-score
SELECT name,game_key,cast(score as varchar),created FROM scores WHERE name = :name;
-- name: get-recent-scores
SELECT score,created,name,game_key,id FROM scores WHERE game_key = :game_key ORDER BY id DESC LIMIT :last;
-- name: get-top-scores
SELECT score,created,name,game_key,id FROM scores WHERE score->>:keyword IS NOT NULL AND game_key=:game_key ORDER by cast(score->>:keyword as integer) DESC,id LIMIT :last;
