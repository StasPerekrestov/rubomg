(ns rubomg.ws-rates
  (:require
    [compojure.core :refer [GET POST defroutes]]
    [taoensso.sente :as sente]))

;;; Add this: --->
(let [{:keys [ch-recv send-fn ajax-post-fn ajax-get-or-ws-handshake-fn
              connected-uids]}
      (sente/make-channel-socket! {})]
  (def ring-ajax-post                ajax-post-fn)
  (def ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
  (def ch-chsk                       ch-recv) ; ChannelSocket's receive channel
  (def chsk-send!                    send-fn) ; ChannelSocket's send API fn
  (def connected-uids                connected-uids) ; Watchable, read-only atom
  )

(defroutes ws-routes
   (GET  "/chsk" req (ring-ajax-get-or-ws-handshake req))
   (POST "/chsk" req (ring-ajax-post               req)))
