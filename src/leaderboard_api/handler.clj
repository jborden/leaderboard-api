(ns leaderboard-api.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defroutes app-routes
  (POST "/score" "New Score")
  (POST "/game" "New Game")
  (GET "/game/key" "Game Key")
  (GET "/game/name/scores" "Name Scores")
  (GET "/game/highscores" "Game highscore")
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
