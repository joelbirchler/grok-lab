(ns hello-compojure.view
   (:require [hiccup.core :as hiccup]))

(defn layout [content]
  (hiccup/html [:html [:body content]]))

(defn test [params]
  [:p (str "wat" params)])  

(defn hello [name]
  [:h1 (str "Helllllo " name "!")])

(defn render [content-fn params]
  (layout (apply content-fn params)))
