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
      (let [{:keys [style rate note]} data]
        (dom/div #js {:className style
                      :style #js {:width "33%"}}
                 (dom/div #js {:className "value"
                               :style #js {:font-size "136.129032258065px"}} rate)
                 (dom/div #js {:className "note"
                               :style #js {:font-size "136.129032258065px"}} note))))))

(defn rates [rates owner]
  (reify
    om/IRender
    (render [_]
      (dom/div #js {:id "container"}
        (dom/div #js {:className "datetime"}
            (dom/div #js {:className "time"} "20:46")
            (dom/div #js {:className "date"} ""))
          (apply dom/div #js {:className "quotes"
                              :style #js {:margin-top "-86px"}}
            (let [{:keys [usd eur]} rates]
              [(om/build section {:rate (if (nil? usd) "" (.toFixed usd 2))
                                 :note "Ru"
                                 :style "item usd minus"})
              (om/build section {:rate (if (nil? eur) "" (.toFixed eur 2))
                                 :note "Eur"
                                 :style "item eur"})
              (om/build section {:rate "61.1"
                                 :note "Brent"
                                 :style "item brent plus"})]))))))
