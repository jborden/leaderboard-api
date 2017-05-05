(ns leaderboard-api.schema
  (:require
   [leaderboard-api.db :as database]
   [cheshire.core :refer [generate-string parse-string]]
   [clojure.java.io :as io]
   [clojure.edn :as edn]
   [com.walmartlabs.lacinia.schema :as schema]
   [com.walmartlabs.lacinia.util :refer [attach-resolvers]]
   [com.walmartlabs.lacinia :refer [execute]]
   ))

(defn leaderboard-schema
  []
  (-> (io/resource "edn/leaderboard-schema.edn")
      slurp
      edn/read-string
      (attach-resolvers {;;:resolve-developer resolve-developer
                         ;;:resolve-user resolve-user
                         :resolve-game database/resolve-game
                         :resolve-games database/resolve-games
                         :resolve-create-game! database/resolve-create-game!
                         :resolve-create-score! database/resolve-create-score!
                         })
      schema/compile))
