(ns rubomg.yahoo.rates
    (:require-macros [cljs.core.async.macros :refer [go alt!]])
    (:require [cljs.core.async :refer [put! <! >! chan timeout]]
              [cljs-http.client :as http]
              [taoensso.sente  :as sente :refer (cb-success?)]))


(let [{:keys [chsk ch-recv send-fn state]}
      (sente/make-channel-socket! "/ws" ; Note the same path as before
       {:type :auto ; e/o #{:auto :ajax :ws}
       })]
  (def chsk       chsk)
  (def ch-chsk    ch-recv) ; ChannelSocket's receive channel
  (def chsk-send! send-fn) ; ChannelSocket's send API fn
  (def chsk-state state)   ; Watchable, read-only atom
  )

(defn map-function-on-map-vals [m f]
        (apply merge
               (map (fn [[k v]] {k (f v)})
                    m)))

(defn rates []
  (let [c (chan)]
    (go
     ;(loop [{res :?data} (<! ch-chsk)]
     (loop [{res :?data [event-code _] :event} (<! ch-chsk)]
       (if (= :chsk/recv event-code)
         (let [[_ rate] res]
           (>! c (map-function-on-map-vals rate js/parseFloat))))
       (recur (<! ch-chsk)))) c))
