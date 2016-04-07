(ns hello-compojure.view
   (:require [hiccup.core :as hiccup]
             [hiccup.page :as hiccup-page]))

(defn layout [content]
  (hiccup/html [:html 
    [:head 
      [:meta {:charset "utf-8"}]
      [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
      [:title "Hello"]
      (hiccup-page/include-css "http://yui.yahooapis.com/pure/0.6.0/pure-min.css")]
    [:body content]]))

(defn test [params]
  [:h2 (str "wat" params)])  

(defn hello [name]
  [:h1 (str "Helllllo " name "!")])

(defn render [content-fn params]
  (layout (apply content-fn params)))
