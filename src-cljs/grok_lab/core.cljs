(ns grok_lab.core
  (:require [reagent.core :as r]
            [grok_lab.editor :as editor]
            [grok_lab.eval :as eval]))


(defonce code (r/atom "println(2 + 2);"))
(defonce log (r/atom '()))

(defn on-log [value]
  (swap! log conj value))

(defn on-error [{message :message line :line col :col}]
  (on-log (str "ERROR! " message " at line " line " column " col)))

(defn grok-pad []
  [:main
    [:section.left-pane
      [editor/editor :javascript code]]
    [:section#slide.right-pane
      [:button {:type "submit" :on-click #(eval/run @code on-log on-error)} "Run"]
      [:pre (clojure.string/join "\n" @log)]]])

(defn ^:export main []
  (r/render [grok-pad]
    (js/document.getElementById "content")))
