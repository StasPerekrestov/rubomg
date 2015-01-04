(ns rubomg.yahoo.rates
  (:use org.httpkit.server))

(defn ws-handler [request]
  (with-channel request channel
    (on-close channel (fn [status] (println "channel closed: " status)))
    (on-receive channel (fn [data] ;; echo it back
                          (send! channel data)))))
