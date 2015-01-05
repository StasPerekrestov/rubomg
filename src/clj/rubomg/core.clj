(ns rubomg.core
    (:require [compojure.handler :as handler]
              [compojure.route :as route]
              [compojure.core :refer [GET POST defroutes routes]]
              [ring.util.response :as resp]
              [cheshire.core :as json]
              [clojure.java.io :as io]
              [rubomg.yahoo.rates :as yahoo]))

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
  (GET  "/ws" req (yahoo/ring-ajax-get-or-ws-handshake req))
  (POST "/ws" req (yahoo/ring-ajax-post              req))
  (route/resources "/")
  (route/not-found "404 - Page not found."))

;(def all-routes
;  (routes app-routes ws/ws-routes))
