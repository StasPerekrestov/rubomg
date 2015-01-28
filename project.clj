(defproject rubomg "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.reader "0.8.13"]
                 ;; CLJ
                 [ring/ring-core "1.3.2"]
                 [compojure "1.3.1"]
                 [cheshire "5.4.0"]
                 [ring/ring-devel "1.3.2"]
                 [ring/ring-defaults "0.1.3"]
                 [javax.servlet/servlet-api "2.5"]
                 ;; CLJS
                 [org.clojure/clojurescript "0.0-2740"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [cljs-http "0.1.24"]
                 [secretary "1.2.1"]
                 [org.omcljs/om "0.8.7"]
                 [figwheel "0.2.2-SNAPSHOT"]
                 [http-kit "2.1.19"]
                 [com.taoensso/sente "1.3.0"]]

  :plugins [[lein-cljsbuild "1.0.4"]
            [lein-ring "0.9.1"]
            [lein-pdo "0.1.1"]
            [lein-figwheel "0.2.2-SNAPSHOT"]
            [lein-ancient "0.6.1"]]

  :aliases {"dev" ["pdo" "cljsbuild" "auto" "dev," "ring" "server-headless"]}

  :ring {:handler rubomg.core/app
         :init    rubomg.core/init}

  :main rubomg.serve

  :source-paths ["src/clj"]

  :cljsbuild {
              :builds [{:id "dev"
                        :source-paths ["src/cljs"]
                        :compiler {
                                   :output-to "resources/public/js/rubomg.js"
                                   :output-dir "resources/public/js/out"
                                   :optimizations :none
                                   :source-map true
                                   :source-map-timestamp true
                                   :cache-analysis true
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
                                   {:non-standard-jsdoc :off}}}]}

  :figwheel {:http-server-root "public" ;; default and assumes "resources"
             :server-port 3449 ;; default
             :css-dirs ["resources/public/css"] ;; watch and update CSS
             :repl false
             :ring-handler rubomg.serve/dev-handler
             })
