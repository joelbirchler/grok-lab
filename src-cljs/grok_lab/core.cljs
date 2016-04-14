(ns grok_lab.core
  (:require [reagent.core :as r]
            [grok_lab.editor :as editor :refer [editor]]
            [grok_lab.slide :refer [slide]]
            [grok_lab.eval :as eval]))


;; TODO: We're going to move this data to a db. Ignore the mess for now.
(defonce code (r/atom
  "const cats = [\"Babou\",
              \"Gilbert The Destroyer\",
              \"Wittle Whiskers\"];

const school = (str) => `Dr. ${str}`;

const graduates = cats.map(school);

println(graduates.join(\", \"));
"))

(defonce watch-range (r/atom [8 8 8 28]))

(defonce slide-md (r/atom "
# Markdown Text Here
We only want educated cats, so we're going to _map_ over all of our cats and
_apply school_ to each one. This will give us a _new cat_, leaving the original
uneducated cat intact and blissfully unaware of science and philosophy.

![Doctor Cat Map](/images/doctor-cat-map.jpg)

Probably want a better description of map and parallelization here.
"))

(defonce log (r/atom '()))

(defn on-log [value]
  (swap! log conj value))

(defn on-error [{message :message line :line col :col}]
  (on-log (str "ERROR! " message " at line " line " column " col)))

(defn run-code []
  (eval/run @code on-log on-error))

(defn grok-pad []
  [:main
    [:div.left-pane
      [slide @slide-md]]

    [:div.right-pane
      [editor :javascript code watch-range]
      [:div#console.stack-1-3
        [:button {:type "submit" :on-click run-code} "Run"]
        [:pre (clojure.string/join "\n" @log)]]]])

(defn ^:export main []
  (r/render [grok-pad]
    (js/document.getElementById "content"))
  (run-code))
