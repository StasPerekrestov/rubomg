(ns rubomg.serve
    (:use [org.httpkit.server :only [run-server]])
    (:require [rubomg.core :as core]
              [compojure.core :refer [routes]]
              [ring.middleware.reload :as reload]))

(defn in-dev? [& _] true) ;; TODO read a config variable from command line, env, or file?

(defn -main [& args] ;; entry point, lein run will pick up and start from here
  (let [handler (if (in-dev? args)
                  (reload/wrap-reload
                     (routes core/all-routes)) ;; only reload when dev
                  (routes core/all-routes))
        port 8080]
    (println (str "Starting server on port: " port "..."))
    (run-server handler {:port port})))
