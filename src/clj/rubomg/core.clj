(ns rubomg.core
    (:require [compojure.handler :as handler]
              [compojure.route :as route]
              [compojure.core :refer [GET POST defroutes routes]]
              [ring.util.response :as resp]
              [cheshire.core :as json]
              [clojure.java.io :as io]
              [rubomg.yahoo.ws-rates :as ws]))

(defn json-response [data & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/json"}
   :body (json/generate-string data)})

(defroutes app-routes
  (GET "/" [] (resp/redirect "/index.html"))

  (GET "/test" [] (json-response
                   {:message "You made it!"}))
  (POST "/test" req (json-response
                     {:message "Doing something something important..."}))

  (route/resources "/")
  (route/not-found "404 - Page not found."))

(def site
  (-> #'app-routes
      (handler/api)))

(def ws-api
  (-> #'ws/ws-routes
      (handler/api)))

(def app
  (routes ws-api site))
