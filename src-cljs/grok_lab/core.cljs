(ns grok_lab.core
  (:require [reagent.core :as r]
            [grok_lab.eval :as eval]))

;; TODO: Make sure we terminate reasonably (handle errors, hangs, etc)
;; TODO: Think about __watch__(uid, code) wrappers

(defonce code (r/atom "println('cats!!'); println(2 + 2);"))
(defonce log (r/atom '()))

(defn textarea [value]
  [:textarea {:value @value
              :on-change #(reset! value (-> % .-target .-value))}])

(defn on-log [value]
  (swap! log conj value))

(defn awesome-ide []
  [:div
    (textarea code)
    [:button {:type "submit" :on-click #(eval/run @code on-log)} "Run"]
    [:pre (clojure.string/join "\n" @log)]])

(defn ^:export main []
  (r/render [awesome-ide]
    (js/document.getElementById "content")))
