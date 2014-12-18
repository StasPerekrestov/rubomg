(ns rubomg.yahoo.rates
    (:require-macros [cljs.core.async.macros :refer [go alt!]])
    (:require [cljs.core.async :refer [put! <! >! chan timeout]]
              [cljs-http.client :as http]))

(defn quote []
  (print "initializing")
  (let [ch (chan 1)]
    (go
     (loop []
       (print "requesting")
       (let [response (<! (http/get "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20(%22USDRUB%22)&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=" {:with-credentials? false}))]
         ;pose for some time
         (<! (timeout 8000))
         ;(>! ch (get-in response [:body :results :rate]))
         )
             (recur))
     )
     ch))
