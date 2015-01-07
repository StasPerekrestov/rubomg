(ns rubomg.serve
    (:use [org.httpkit.server :only [run-server]])
    (:require [rubomg.core :as core]
              [compojure.core :refer [routes]]
              [ring.middleware.reload :as reload]
              [ring.middleware.defaults :refer [site-defaults wrap-defaults]]))

(defn in-dev? [& _] true) ;; TODO read a config variable from command line, env, or file?

(def my-ring-handler
  (let [ring-defaults-config
        (assoc-in site-defaults [:security :anti-forgery]
          {:read-token (fn [req] (-> req :params :csrf-token))})]

    ;; NB: Sente requires the Ring `wrap-params` + `wrap-keyword-params`
    ;; middleware to work. These are included with
    ;; `ring.middleware.defaults/wrap-defaults` - but you'll need to ensure
    ;; that they're included yourself if you're not using `wrap-defaults`.
    ;;
    (wrap-defaults core/all-routes ring-defaults-config)))

(defn -main [& args] ;; entry point, lein run will pick up and start from here
  (let [handler (if (in-dev? args)
                  (reload/wrap-reload
                     (routes my-ring-handler)) ;; only reload when dev
                  (routes my-ring-handler))
        port 8080]
    (println (str "Starting server on port: " port "..."))
    (run-server handler {:port port})))
