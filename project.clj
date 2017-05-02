(defproject leaderboard-api "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]
                 [compojure "1.5.2"]
                 [ring/ring-defaults "0.2.1"]
                 [yesql "0.5.3"]
                 [buddy/buddy-core "1.2.0"]]
  :plugins [[lein-ring "0.11.0"]]
  :ring {:handler leaderboard-api.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}})
