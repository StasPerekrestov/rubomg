(ns rubomg.yahoo.ws-handler
  (:require
    [clojure.core.async :refer [put! <! >! chan timeout go close!]]
    [taoensso.sente :as sente]
    [cheshire.core :refer [parse-string]]
    [rubomg.yahoo.rates :refer [usd-eur-rates brent-rates]]))

(let [{:keys [ch-recv send-fn ajax-post-fn ajax-get-or-ws-handshake-fn
              connected-uids]}
      (sente/make-channel-socket! {
                                   :user-id-fn (fn [ring-request]
                                                 (let [{:keys [params]} ring-request]
                                                  ;fake :user-id = :client-id
                                                   (:client-id params)))
                                   })]
  (def ring-ajax-post                ajax-post-fn)
  (def ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
  (def ch-chsk                       ch-recv) ; ChannelSocket's receive channel
  (def chsk-send!                    send-fn) ; ChannelSocket's send API fn
  (def connected-uids                connected-uids) ; Watchable, read-only atom
  )

(def current-rates
  (atom {:usd nil :eur nil :brent nil}))

(defn has-rate-value [rate]
  (let [currency (first (keys rate))
          v (currency rate)]
    (not (nil? v))))

(let [cr (chan)]
  (go
   (loop [{:keys [eur usd]} (<! (usd-eur-rates))]
     (if-not (nil? eur)
       (>! cr {:eur eur}))
     (if-not (nil? usd)
       (>! cr {:usd usd}))
     (<! (timeout 9000))
     (recur (<! (usd-eur-rates)))))
  (go
   (loop [rate (<! (brent-rates))]
     (if (has-rate-value rate)
       (>! cr rate))
     (<! (timeout 9000))
     (recur (<! (brent-rates)))))

  (go
   (loop [rates (<! cr)]
     (swap! current-rates (fn [r] (merge r rates)))
     (println "rates obtained" rates)
     (chsk-send! :sente/all-users-without-uid [:omg/rate rates])
     (recur (<! cr))))
  )

(add-watch connected-uids :watcher
  (fn [key atom old-state new-state]
    (let [old-clients (:any old-state)
          new-clients (:any new-state)
          notif-clients (clojure.set/difference new-clients old-clients)
          rates @current-rates]
    ;Assign UIDs for clients and notify only newly joined clients
      (doseq [uid notif-clients]
       (print "new client notified:" uid)
        (chsk-send! uid [:omg/rate rates]))
    ;(chsk-send! :sente/all-users-without-uid [:omg/rate rates])
      )))

(comment
  @connected-uids
  (chsk-send! :sente/all-users-without-uid [:some/request-id {:name "Rich Hickey" :type "Awesome"}])
  (let [[_ res] [:omg/rate {:usd 62.8832}]]
    res)
  )
