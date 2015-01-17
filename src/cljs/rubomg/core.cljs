(ns rubomg.core
    (:require-macros [cljs.core.async.macros :refer [go alt!]])
    (:require [goog.events :as events]
              [cljs.core.async :refer [put! <! >! chan timeout]]
              [om.core :as om :include-macros true]
              [om.dom :as dom :include-macros true]
              [cljs-http.client :as http]
              [rubomg.utils :refer [guid]]
              [rubomg.yahoo.rates :as finance]
              [rubomg.visualiser :as visualiser]
              [figwheel.client :as fw]))

;; Lets you do (prn "stuff") to the console
(enable-console-print!)

(defonce app-state
  (atom {:rate {:usd nil :eur nil :brent nil}}))


(defn rates [data owner]
  (reify
    om/IInitState
    (init-state [_]
      {:rates-ch (finance/rates)})
    om/IWillMount
    (will-mount [_]
      (let [rates-ch (om/get-state owner :rates-ch)]
        (go
          (loop []
            (let [rate (<! rates-ch)]
              (om/transact! data :rates (fn[r] (merge r rate)))
              (recur))))))
    om/IRenderState
    (render-state [_ _]
      (om/build visualiser/rates (get-in data [:rates])))))

(defn rubomg-app [app owner]
  (reify
    om/IRender
    (render [_]
      (dom/div nil
        (om/build rates app)))))

(om/root rubomg-app app-state {:target (.getElementById js/document "app")})

(fw/start {
  ;; configure a websocket url if yor are using your own server
  ;; :websocket-url "ws://localhost:3449/figwheel-ws"

  ;; optional callback
  :on-jsload (fn [] (print "reloaded"))

  ;; The heads up display is enabled by default
  ;; to disable it:
  ;; :heads-up-display false

  ;; when the compiler emits warnings figwheel
  ;; blocks the loading of files.
  ;; To disable this behavior:
  ;; :load-warninged-code true

  ;; if figwheel is watching more than one build
  ;; it can be helpful to specify a build id for
  ;; the client to focus on
  ;; :build-id "example"
})
