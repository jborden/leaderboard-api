(ns leaderboard-api.database
  (:require [yesql.core :refer [defquery defqueries]]
            [leaderboard-api.core :as core]))

(def db-spec {:classname "org.postgresql.Driver"
              :subprotocol "postgresql"
              :subname "//localhost:5432/leaderboard"
              :user "leaderboard"})

(defqueries "sql/operations.sql"
  {:connection db-spec})

;;java.sql.BatchUpdateException

;; see: https://gist.github.com/alexpw/2166820
(defmacro check-error
  "Usage: (check-error (create-developer! (core/new-developer \"jmborden@gmail.com\")))"
  [body]
  `(try ~body (catch Exception e# (:cause (Throwable->map (.getNextException e#))))))
