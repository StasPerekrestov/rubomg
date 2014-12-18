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

(defonce app-state
  (atom {:rate {}}))


(defn rates [data owner]
  (reify
    om/IInitState
    (init-state [_]
      {:rates-ch (finance/quote)})
    om/IWillMount
    (will-mount [_]
      (print "will mount")
      (let [rates-ch (om/get-state owner :rates-ch)]
        (go
          (loop []
            (let [rates (<! rates-ch)]
              (om/update! data [:rate] rates)
              (print rates)
              (recur))))))
    om/IRenderState
    (render-state [this {:keys [rates]}]
                  (dom/div nil (get-in data [:rate :Rate])))))

(defn rubomg-app [app owner]
  (reify
    om/IRender
    (render [_]
      (dom/div nil
        (dom/h1 nil "Currency Rates")
        (om/build rates app)
               ))))

(om/root rubomg-app app-state {:target (.getElementById js/document "app")})

(fw/watch-and-reload
 :jsload-callback (fn []
                    ;; (stop-and-start-my app)
                    ))
