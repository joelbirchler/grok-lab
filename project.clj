(defproject grok-lab "0.1.0-SNAPSHOT"
  :description "A learning platform for live demo and experimentation of code."
  :url "http://www.joelbirchler.com"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.8.40"]
                 [markdown-clj "0.9.87"]
                 [reagent "0.6.0-alpha"]
                 [compojure "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [hiccup "1.0.5"]]
  :plugins [[lein-ring "0.9.7"]
            [lein-cljsbuild "1.1.3"]]
  :ring {:handler grok-lab.routes/app}
  :cljsbuild {
    :builds [{:source-paths ["src-cljs"]
              :compiler {:output-to "resources/public/compiled-js/main.js"
                         :output-dir "resources/public/compiled-js/"
                         :source-map "resources/public/compiled-js/main.js.map"
                         :optimizations :whitespace}}]}
  :profiles {:dev
    {:dependencies [[javax.servlet/servlet-api "2.5"]
                    [ring/ring-mock "0.3.0"]]}})
