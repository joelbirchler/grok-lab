(ns grok_lab.slide
  (:require [reagent.core :as r]
            [markdown.core :as markdown]))

(defn slide [slide-md]
  [:div#slide.fill
    {:dangerouslySetInnerHTML
      {:__html (markdown/md->html slide-md)}}])
