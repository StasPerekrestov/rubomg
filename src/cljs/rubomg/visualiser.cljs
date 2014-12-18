(ns rubomg.visualiser
    (:require-macros [cljs.core.async.macros :refer [go alt!]])
    (:require [goog.events :as events]
              [cljs.core.async :refer [put! <! >! chan timeout]]
              [om.core :as om :include-macros true]
              [om.dom :as dom :include-macros true]
              ))

(defn section [data owner]
  (reify
    om/IRender
    (render [_]
        (dom/div #js {:className (:style data)
                      :style #js {:width "33%"}}
                 (dom/div #js {:className "value"
                               :style #js {:font-size "136.129032258065px"}} (:rate data))
                 (dom/div #js {:className "note"
                               :style #js {:font-size "136.129032258065px"}} (:note data))))))

(defn rates [rate owner]
  (reify
    om/IRender
    (render [_]
      (dom/div #js {:id "container"}
        (dom/div #js {:className "datetime"}
            (dom/div #js {:className "time"} "20:46")
            (dom/div #js {:className "date"} ""))
        (dom/div #js
               {:className "quotes"
                :style #js {:margin-top "-86px"}}
          (om/build section {:rate (.toFixed (js/parseFloat (:Rate rate)) 2) :note "Ru" :style "item usd minus"})
          (om/build section {:rate "72.1" :note "EUR" :style "item eur"})
          (om/build section {:rate "61.1" :note "Brent" :style "item brent plus"}))))))
