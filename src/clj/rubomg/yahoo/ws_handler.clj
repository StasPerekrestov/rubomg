(ns rubomg.yahoo.ws-handler
  (:require
    [clojure.core.async :refer [put! <! >! chan timeout go close!]]
    [taoensso.sente :as sente]
    [cheshire.core :refer [parse-string]]
    [rubomg.yahoo.rates :refer [usd-eur-rates brent-rates]]))

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
        (chsk-send! :sente/all-users-without-uid [:omg/rate (dissoc rates :usd)])
        (chsk-send! :sente/all-users-without-uid [:omg/rate (dissoc rates :eur)])
        (<! (timeout 9000))
        (recur (<! (usd-eur-rates)))))
(go
    (loop [rates (<! (brent-rates))]
        (chsk-send! :sente/all-users-without-uid [:omg/rate rates])
        (<! (timeout 9000))
        (recur (<! (brent-rates)))))

(comment
  @connected-uids
  (chsk-send! :sente/all-users-without-uid [:some/request-id {:name "Rich Hickey" :type "Awesome"}])
  (let [[_ res] [:omg/rate {:usd 62.8832}]]
    res)
  )
