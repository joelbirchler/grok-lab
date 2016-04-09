(ns grok_lab.core
  (:require [reagent.core :as r]
            [grok_lab.eval :as eval]))

;; TODO: Term any existing running workers on run, set/reset a terminate timer
;; TODO: Think about __watch__(uid, code) wrappers

(defonce code (r/atom "println('cats!!'); println(2 + 2);"))
(defonce log (r/atom '()))

(defn textarea [value]
  [:textarea {:value @value
              :on-change #(reset! value (-> % .-target .-value))}])

(defn on-log [value]
  (swap! log conj value))

(defn on-error [{message :message line :line col :col}]
  (on-log (str "ERROR! " message " at line " line " column " col)))

(defn awesome-ide []
  [:div
    (textarea code)
    [:button {:type "submit" :on-click #(eval/run @code on-log on-error)} "Run"]
    [:pre (clojure.string/join "\n" @log)]])

(defn ^:export main []
  (r/render [awesome-ide]
    (js/document.getElementById "content")))
