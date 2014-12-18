(ns rubomg.core
    (:require-macros [cljs.core.async.macros :refer [go alt!]])
    (:require [goog.events :as events]
              [cljs.core.async :refer [put! <! >! chan timeout]]
              [om.core :as om :include-macros true]
              [om.dom :as dom :include-macros true]
              [cljs-http.client :as http]
              [rubomg.utils :refer [guid]]
              [rubomg.yahoo.rates :as finance]
              [figwheel.client :as fw]))

;; Lets you do (prn "stuff") to the console
(enable-console-print!)

(def app-state
  (atom {:things []}))


(defn rates [data owner]
  (reify
    om/IInitState
    (init-state [_]
      {:rates-ch (finance/quote)})
    om/IWillMount
    (will-mount [_]
      (let [rates-ch (om/get-state owner :rates-ch)]
        (go
          (loop []
            (let [rates (<! rates-ch)]
              (print "response: " rates)
              (recur))))))
    om/IRenderState
    (render-state [this {:keys [rates]}]
                  (dom/div nil "Test"))))

(defn rubomg-app [app owner]
  (reify
    om/IRender
    (render [_]
      (dom/div nil
        (dom/h1 nil "rubomg is working!")
        (om/build rates nil)
               ))))

(om/root rubomg-app app-state {:target (.getElementById js/document "app")})

(fw/watch-and-reload
 :jsload-callback (fn []
                    ;; (stop-and-start-my app)
                    ))
