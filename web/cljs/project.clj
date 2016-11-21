(defproject grok-lab "0.1.0-SNAPSHOT"
  :description "A learning platform for live demo and experimentation of code."
  :url "http://www.joelbirchler.com"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.8.40"]
                 [markdown-clj "0.9.87"]
                 [reagent "0.6.0-alpha"]]
  :plugins [[lein-cljsbuild "1.1.3"]]
  :ring {:handler grok-lab.routes/app}
  :cljsbuild {
    :builds [{:source-paths ["src"]
              :compiler {:output-to "compiled-js/main.js"
                         :output-dir "compiled-js/"
                         :source-map "compiled-js/main.js.map"
                         :optimizations :whitespace}}]})
