(ns leaderboard-api.handler
  (:require [clojure.data.json :as json]
            [clojure.string :as string]
            [com.walmartlabs.lacinia :refer [execute]]
            [leaderboard-api.schema :refer [leaderboard-schema]]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-params wrap-json-body]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.util.request :refer [body-string]]))

;;  this code is based on the example provided at
;;  https://github.com/hlship/boardgamegeek-graphql-proxy/blob/master/src/bgg_graphql_proxy/server.clj

(defn variable-map
  "Reads the `variables` query parameter, which contains a JSON string
  for any and all GraphQL variables to be associated with this request.
  Returns a map of the variables (using keyword keys)."
  [request]
  (let [variables (condp = (:request-method request)
                    ;; We do a little bit more error handling here in the case
                    ;; where the client gives us non-valid json. We still haven't
                    ;; handed over the values of the request object to lacinia
                    ;; GraphQL so we are still responsibile for minimal error
                    ;; handling
                    :get (try (-> request
                                  (get-in [:query-params "variables"])
                                  (json/read-str :key-fn keyword))
                              (catch Exception e nil))
                    :post (try (-> request
                                   :body
                                   (json/read-str :key-fn keyword)
                                   :variables)
                               (catch Exception e nil)))]
    (if-not (empty? variables)
      variables
      {})))

(defn extract-query
  "Reads the `query` query parameters, which contains a JSON string
  for the GraphQL query associated with this request. Returns a
  string.  Note that this differs from the PersistentArrayMap returned
  by variable-map. e.g. The variable map is a hashmap whereas the
  query is still a plain string."
  [request]
  (case (:request-method request)
    :get  (get-in request [:query-params "query"])
    ;; Additional error handling because the clojure ring server still
    ;; hasn't handed over the values of the request to lacinia GraphQL
    :post (try (-> request
                   :body
                   (json/read-str :key-fn keyword)
                   :query)
               (catch Exception e ""))
    :else ""))

(defn extract-authorization-key
  "Extract the authorization key from the request header. The
  authorization header is of the form: Authorization: bearer <key>"
  [request]
  (if-let [auth-header (-> request
                           :headers
                           (get "authorization"))]
    (-> auth-header
        (string/split #"\s")
        last)
    nil))

(defn ^:private graphql-handler
  "Accepts a GraphQL query via GET or POST, and executes the query.
  Returns the result as text/json."
  [compiled-schema]
  (let [context {:cache (atom {})}]
    (fn [request]
      ;; include authorization key in context
      (swap! (:cache context) assoc :authorization
             (extract-authorization-key request))
      (let [vars (variable-map request)
            query (extract-query request)
            result (execute compiled-schema query vars context)
            status (if (-> result :errors seq)
                     400
                     200)]
        {:status status
         :headers {"Content-Type" "application/json"}
         :body (json/write-str result)}))))

(defn handler [request]
  (let [uri (:uri request)]
    (if (= uri "/graphql")
      ;; hits the proper uri, process request
      ((graphql-handler (leaderboard-schema)) request)
      ;; not serving any other requests
      {:status 404
       :headers {"Content-Type" "text/html"}
       :body (str "only requests to /graphql are accepted on this server")})))

;; from: http://stackoverflow.com/questions/37397531/ring-read-body-of-a-http-request-as-string
;; this is needed to deal with the fact that body-string can only be called once because it consumes
;; the stream object representing the body. e.g. the body of a request makes the request data structure
;; mutable!
(defn wrap-body-string [handler]
  (fn [request]
    (let [body-str (body-string request)]
      (handler (assoc request :body body-str)))))

(def app
  (-> handler
      wrap-params
      (wrap-cors :access-control-allow-origin [#".*"]
                 :access-control-allow-methods [:get :post])
      wrap-body-string))
