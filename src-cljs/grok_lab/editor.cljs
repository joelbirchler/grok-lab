(ns grok_lab.editor
  (:require [reagent.core :as r]))

(defn editor [mode content]
  (let [on-change #(reset! content %)
        xon-change #(.log js/console %)]

    (r/create-class
      {:component-did-mount
      (fn [this]
       (let [node (r/dom-node this)
             ace-editor (.edit js/ace node)]

         (doto ace-editor
           (.setTheme "ace/theme/tomorrow_night"))

         (doto (.getSession ace-editor)
           (.on "change" #(on-change (.getValue ace-editor)))
           (.setMode "ace/mode/javascript"))))

      :reagent-render
      (fn [mode content]
        [:div#editor {:style {:font-size "18px !important"}} @content])})))
