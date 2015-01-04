(ns rubomg.core
    (:require [compojure.route :as route]
              [compojure.core :refer [GET POST defroutes routes]]
              [ring.util.response :as resp]
              [cheshire.core :as json]
              [rubomg.yahoo.rates :as rates]))

(defn json-response [data & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/json"}
   :body (json/generate-string data)})

(defroutes all-routes
  (GET "/" [] (resp/redirect "/index.html"))

  (GET "/test" [] (json-response
                   {:message "You made it!"}))

  (GET "/ws" req (rates/ws-handler req))

  (POST "/test" req (json-response
                     {:message "Doing something something important..."}))

  (route/resources "/")
  (route/not-found "Page not found"))



