(ns rubomg.core
    (:require [compojure.handler :as handler]
              [compojure.route :as route]
              [compojure.core :refer [GET POST defroutes routes]]
              [ring.util.response :as resp]
              [cheshire.core :as json]
              [clojure.java.io :as io]
              [rubomg.yahoo.ws-handler :as ws]))

(defn json-response [data & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/json"}
   :body (json/generate-string data)})

(defroutes all-routes
  (GET "/" [] (resp/redirect "/index.html"))

  (GET "/test" [] (json-response
                   {:message "You made it!"}))
  (POST "/test" req (json-response
                     {:message "Doing something something important..."}))
  (GET  "/ws" req (ws/ring-ajax-get-or-ws-handshake req))
  (POST "/ws" req (ws/ring-ajax-post              req))
  (route/resources "/")
  (route/not-found "404 - Page not found."))

;(def all-routes
;  (routes app-routes ws/ws-routes))
