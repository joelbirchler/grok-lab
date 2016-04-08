(ns grok-lab.view
   (:require [hiccup.core :as hiccup]
             [hiccup.page :as hiccup-page :refer [include-js include-css]]))

(defn layout [content]
  (hiccup/html [:html
    [:head
      [:meta {:charset "utf-8"}]
      [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
      [:title "Hello"]
      (include-js "/js/main.js")
      (include-css "http://yui.yahooapis.com/pure/0.6.0/pure-min.css")]
    [:body content]]))

(defn index []
  [:main
    [:div#content]
    [:script "grok_lab.core.main()"]])

(defn render
  ([content-fn params]
    (layout (apply content-fn params)))
  ([content-fn]
    (layout (content-fn))))
