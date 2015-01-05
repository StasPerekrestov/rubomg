(ns rubomg.yahoo.rates
  (:require
    [clojure.core.async :refer [put! <! >! chan timeout go close!]]
    [compojure.core :refer [GET POST defroutes]]
    [taoensso.sente :as sente]
    [org.httpkit.client :as client]
    [cheshire.core :refer [parse-string generate-string]]))

(let [{:keys [ch-recv send-fn ajax-post-fn ajax-get-or-ws-handshake-fn
              connected-uids]}
      (sente/make-channel-socket! {})]
  (def ring-ajax-post                ajax-post-fn)
  (def ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
  (def ch-chsk                       ch-recv) ; ChannelSocket's receive channel
  (def chsk-send!                    send-fn) ; ChannelSocket's send API fn
  (def connected-uids                connected-uids) ; Watchable, read-only atom
  )

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

(go
    (loop [rates (<! (usd-eur-rates))]
        (println "Crawling response received")
        (doseq [uid (:any @connected-uids)]
          (println "sending to " uid)
          (chsk-send! uid (generate-string (dissoc rates :usd))))
         (<! (timeout 9000))
         (recur (<! (usd-eur-rates)))))

;(defroutes ws-routes
;   (GET  "/ws" req (ring-ajax-get-or-ws-handshake req)))
(comment
  @connected-uids
  )
