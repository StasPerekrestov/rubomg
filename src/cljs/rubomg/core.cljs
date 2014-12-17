(ns rubomg.core
    (:require-macros [cljs.core.async.macros :refer [go alt!]])
    (:require [goog.events :as events]
              [cljs.core.async :refer [put! <! >! chan timeout]]
              [om.core :as om :include-macros true]
              [om.dom :as dom :include-macros true]
              [cljs-http.client :as http]
              [rubomg.utils :refer [guid]]))

;; Lets you do (prn "stuff") to the console
(enable-console-print!)

(def app-state
  (atom {:things []}))

(defn rubomg-app [app owner]
  (reify
    om/IRender
    (render [_]
      (dom/div nil
        (dom/h1 nil "rubomg is working!")))))

(om/root rubomg-app app-state {:target (.getElementById js/document "app")})
