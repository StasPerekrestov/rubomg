(defproject rubomg "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.reader "0.8.13"]
                 ;; CLJ
                 [ring/ring-core "1.3.2"]
                 [compojure "1.3.1"]
                 [cheshire "5.4.0"]
                 ;; CLJS
                 [org.clojure/clojurescript "0.0-2657"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [cljs-http "0.1.24"]
                 [secretary "1.2.1"]
                 [om "0.8.0-rc1"]
                 [figwheel "0.2.0-SNAPSHOT"]

  :plugins [[lein-cljsbuild "1.0.4-SNAPSHOT"]
            [lein-ring "0.8.13"]
            [lein-pdo "0.1.1"]
            [lein-figwheel "0.2.0-SNAPSHOT"]]

  :aliases {"dev" ["pdo" "cljsbuild" "auto" "dev," "ring" "server-headless"]}

  :ring {:handler rubomg.core/app
         :init    rubomg.core/init}

  :source-paths ["src/clj"]

  :cljsbuild {
              :builds [{:id "dev"
                        :source-paths ["src/cljs"]
                        :compiler {
                                   :output-to "resources/public/js/rubomg.js"
                                   :output-dir "resources/public/js/out"
                                   :optimizations :none
                                   :source-map true
                                   :externs ["react/externs/react.js"]}}
                       {:id "release"
                        :source-paths ["src/cljs"]
                        :compiler {
                                   :output-to "resources/public/js/rubomg.js"
                                   :source-map "resources/public/js/rubomg.js.map"
                                   :optimizations :advanced
                                   :pretty-print false
                                   :output-wrapper false
                                   :preamble ["react/react.min.js"]
                                   :externs ["react/externs/react.js"]
                                   :closure-warnings
                                   {:non-standard-jsdoc :off}}}]
              :figwheel
                {:http-server-root "public" ;; default and assumes "resources"
                 :server-port 3449 ;; default
                 :css-dirs ["resources/public/css"] ;; watch and update CSS
                 ;; :ring-handler hello-world.server/handler
                 }})
