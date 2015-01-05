(ns rubomg.yahoo.rates
    (:require-macros [cljs.core.async.macros :refer [go alt!]])
    (:require [cljs.core.async :refer [put! <! >! chan timeout]]))

(defn ws-socket [ws-url]
  (new js/WebSocket ws-url))

(defn ws-event[]
  (let [ws (ws-socket "ws://localhost:8080/ws")
        c (chan)]
  (set! (.-onmessage ws)
        (fn [msg]
          (put! c
                (js->clj
                  (-> msg
                      (.-data)
                      (JSON/parse)) :keywordize-keys true)))) c))

(defn map-function-on-map-vals [m f]
        (apply merge
               (map (fn [[k v]] {k (f v)})
                    m)))
(defn rates []
  (let [ch (chan 1)
        ws-ch (ws-event)]
    (go
       (loop [rate (<! ws-ch)]
         (let [rc (map-function-on-map-vals rate js/parseFloat)]
           (>! ch rc))
         (recur (<! ws-ch))))
    ch))
