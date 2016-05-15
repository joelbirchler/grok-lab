(ns grok-lab.view
   (:require [hiccup.core :as hiccup]
             [hiccup.page :as hiccup-page :refer [include-js include-css]]))

(defn layout [content]
  (hiccup/html [:html
                 [:head
                   [:meta {:charset "utf-8"}]
                   [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
                   [:title "Hello"]
                   (include-js "https://cdnjs.cloudflare.com/ajax/libs/ace/1.2.3/ace.js")
                   (include-js "/compiled-js/main.js")
                   (include-css "https://cdnjs.cloudflare.com/ajax/libs/normalize/4.0.0/normalize.min.css")
                   (include-css "/css/style.css")]
                 content]))

(defn index []
  [:body
    [:div#content]
    [:script "grok_lab.core.main()"]])

(defn render
  ([content-fn params]
    (layout (apply content-fn params)))
  ([content-fn]
    (layout (content-fn))))
