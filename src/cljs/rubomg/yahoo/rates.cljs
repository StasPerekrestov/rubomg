(ns rubomg.yahoo.rates
    (:require-macros [cljs.core.async.macros :refer [go alt!]])
    (:require [cljs.core.async :refer [put! <! >! chan timeout]]
              [cljs-http.client :as http]))

(defn quote []
  (let [ch (chan 1)]
    (go
     (loop []
       (let [response (<! (http/get "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20(%22USDRUB%22)&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=" {:with-credentials? false}))]
         ;pause for some time
         (>! ch   (get-in response [:body :query :results :rate])))
         (<! (timeout 60000))
         (recur))
     )
     ch))
