(ns leaderboard-api.core
  (:require [clojure.string :as string]))

;; see http://stackoverflow.com/questions/14412132/best-approach-for-generating-api-key
;; see https://docs.oracle.com/javase/6/docs/api/java/util/UUID.html#randomUUID()
;; note that "Static factory to retrieve a type 4 (pseudo randomly generated) UUID. The UUID is generated using a cryptographically strong pseudo random number generator"
;; this is good enough for our purposes now
(defn uuid [] (string/replace (str (java.util.UUID/randomUUID)) #"-" ""))

(defn new-developer
  "Given email, create a developer map"
  [email]
  {:email email
   :key (uuid)})

(defn new-game
  "Given a name and developer key, create a game map"
  [name developer]
  {:name name
   :developer developer
   :key (uuid)})
