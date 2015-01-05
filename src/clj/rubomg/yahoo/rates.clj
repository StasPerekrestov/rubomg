(ns rubomg.yahoo.rates
  (:require [clojure.core.async :refer [put! <! >! <!! chan timeout go close!]]
            [org.httpkit.client :as client]
            [org.httpkit.server :as server]
            [cheshire.core :refer [parse-string generate-string]]))

(defn get-url [url]
  (let [c (chan)]
    (client/get url {}
                (fn [{:keys [status headers body error]}] ;; asynchronous response handling
                  (go
                    (if error
                      (println "status-code" status "error" error)
                      (do
                        (>! c (parse-string body true))
                        (close! c))))))
    c))

(defn usd-eur-rates []
  (go
    (let [{{{rates :rate} :results} :query}  (<! (get-url "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20(%22USDRUB%22,%22EURRUB%22)&format=json&diagnostics=false&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys"))
          [{usd :Rate} {eur :Rate}] rates]
        {:usd usd :eur eur})))

(defn brent-rates []
  (go
    (let [{{{{brent :BidRealtime} :quote} :results} :query}  (<! (get-url "http://query.yahooapis.com/v1/public/yql?q=select%20BidRealtime%20from%20yahoo.finance.quotes%20where%20symbol%20in%20(%22BZG15.NYM%22)&env=store://datatables.org/alltableswithkeys&format=json&diagnostics=false"))
          ]
        {:brent brent})))

(defn ws-handler [request]
  (server/with-channel request channel
    (go
       (loop [rates (<! (usd-eur-rates))]
         (server/send! channel (generate-string (dissoc rates :usd)))
         (server/send! channel (generate-string (dissoc rates :eur)))
         (<! (timeout 5000))
         (recur (<! (usd-eur-rates)))))
    (go
       (loop [brent (<! (brent-rates))]
         (server/send! channel (generate-string brent))
         (<! (timeout 5000))
         (recur (<! (brent-rates)))))
    (server/on-close channel (fn [status] (println "channel closed: " status)))
    (server/on-receive channel (fn [data] ;; echo it back
                          (server/send! channel data)))))

(comment

  (go
   (let [res (<! (usd-eur-rates))]
     (println "Resp: " res)

   ))
)
