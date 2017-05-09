(ns leaderboard-api.db
  (:require [clojure.data.json :as json]
            [environ.core :refer [env]]
            [leaderboard-api.core :as core]
            [yesql.core :refer [defquery defqueries]]))

;; still need to put a password in for this
;; need to be sure the database is password protected!
(def db-spec {:classname "org.postgresql.Driver"
              :subprotocol "postgresql"
              :subname (str "//"
                            (or (:db-host env)
                                (System/getProperty "OPENSHIFT_POSTGRESQL_DB_HOST"))
                            ":"
                            (or (:db-port env)
                                (System/getProperty "OPENSHIFT_POSTGRESQL_DB_PORT"))
                            "/"
                            "leaderboard")
              :user (or (:db-username env)
                        (System/getProperty "OPENSHIFT_POSTGRESQL_DB_USERNAME"))
              :password (or (:db-password env)
                            (System/getProperty "OPENSHIFT_POSTGRESQL_DB_PASSWORD"))})

(defqueries "sql/operations.sql"
  {:connection db-spec})

;; see: https://gist.github.com/alexpw/2166820
(defmacro check-error
  "Usage: (check-error (create-developer! (core/new-developer \"jmborden@gmail.com\")))"
  [body]
  `(try ~body (catch Exception e# (throw (Exception.(:cause (Throwable->map (.getNextException e#))))))))

(defn resolve-game
  [context args _value]
  (let [developer (:authorization @(:cache context))]
    (first
     (check-error (get-game (assoc args :developer developer))))))

(defn resolve-games
  [context args _value]
  (let [developer (:authorization @(:cache context))]
    (check-error (get-games {:developer developer}))))

(defn resolve-create-game!
  [context args _value]
  (let [developer (:authorization @(:cache context))]
    (dissoc (create-game<! (core/new-game (:name args) developer)) :developer)))

;;https://blog.codeship.com/unleash-the-power-of-storing-json-in-postgres/
(defn resolve-create-score!
  [context args _value]
  (create-score<! args))

(defn resolve-recent-scores
  [context {:keys [game_key last]} _value]
  (let [last (if (> last 100)
               100
               last)]
    (get-recent-scores {:game_key game_key
                        :last last})))

(defn resolve-top-scores
  [context {:keys [game_key last keyword]} _value]
  (let [last (if (> last 100)
               100
               last)]
    (get-top-scores {:game_key game_key
                     :last last
                     :keyword keyword})))
