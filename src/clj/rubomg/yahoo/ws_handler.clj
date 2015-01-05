(ns rubomg.yahoo.ws-handler
  (:require
    [clojure.core.async :refer [put! <! >! chan timeout go close!]]
    [taoensso.sente :as sente]
    [cheshire.core :refer [parse-string]]
    [rubomg.yahoo.rates :refer [usd-eur-rates]]))

(let [{:keys [ch-recv send-fn ajax-post-fn ajax-get-or-ws-handshake-fn
              connected-uids]}
      (sente/make-channel-socket! {})]
  (def ring-ajax-post                ajax-post-fn)
  (def ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
  (def ch-chsk                       ch-recv) ; ChannelSocket's receive channel
  (def chsk-send!                    send-fn) ; ChannelSocket's send API fn
  (def connected-uids                connected-uids) ; Watchable, read-only atom
  )

(go
    (loop [rates (<! (usd-eur-rates))]
        (println "Crawling response received")
        (doseq [uid (:any @connected-uids)]
          (println "sending to " uid)
          (chsk-send! uid [:my-app/some-req (dissoc rates :usd)]))
         (<! (timeout 9000))
         (recur (<! (usd-eur-rates)))))

(comment
  @connected-uids
  (chsk-send! :sente/all-users-without-uid [:some/request-id {:name "Rich Hickey" :type "Awesome"}])
  )
