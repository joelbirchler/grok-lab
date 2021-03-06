(defproject grok-lab "0.1.0-SNAPSHOT"
  :description "A learning platform for live demo and experimentation of code."
  :url "http://www.joelbirchler.com"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [markdown-clj "0.9.91"]
                 [compojure "1.5.1"]
                 [ring/ring-defaults "0.2.1"]
                 [ring/ring-json "0.4.0"]
                 [hiccup "1.0.5"]]
  :plugins [[lein-ring "0.10.0"]]
  :ring {:handler grok-lab.routes/app}
  :profiles {:dev
    {:dependencies [[javax.servlet/servlet-api "2.5"]
                    [ring/ring-mock "0.3.0"]]}})
