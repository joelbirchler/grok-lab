(ns grok_lab.editor
  (:require [reagent.core :as r]
            [cljs.pprint :refer [pprint]]))

 (enable-console-print!)

(defonce ace-range-constructor
  (.-Range (.require js/ace "ace/range")))

(defn ace-range [row-start col-start row-end col-end]
  "Creates a new Ace range."
  (ace-range-constructor. row-start col-start row-end col-end))

(defn ace-editor []
  (.edit js/ace "editor"))

(defn ace-selected-range []
  "Grab the selected range from Ace (note: ace must be rendered)"
  (let [selection-obj (.getSelectionRange (ace-editor))
        start-sel (.-start selection-obj)
        end-sel (.-end selection-obj)]
    [(.-row start-sel) (.-column start-sel) (.-row end-sel) (.-column end-sel)]))

(defn render-ace-markers [marker-range]
  "Clears and redraws markers (note: ace must be rendered)"
  (let [session (.getSession (ace-editor))]
    (pprint "hot")
    (.addMarker session
      (apply ace-range marker-range) "watch-marker" "text")))

;;
;; Ace notes for the horribly undocumented road ahead:
;;   editor.getSelectionRange() for the current selected range
;;   editor.session.getTextRange(range) should give us the text in the range (for watch)
;;



(defn editor [mode content watch-range]
  "React wrapper for Ace"
  (let [on-change    #(reset! content %)
        on-set-watch #(reset! watch-range %)]

    (r/create-class
      {:component-did-mount
      (fn [this]
       (let [ace (ace-editor)
             session (.getSession ace)]

         (doto ace
           (aset "$blockScrolling" js/Infinity) ; hides deprecation warning
           (.setTheme "ace/theme/tomorrow_night")
           (.setValue @content -1))

         (doto session
           (.on "change" #(on-change (.getValue ace-editor)))
           (.setMode "ace/mode/javascript"))

         (render-ace-markers @watch-range)))

      :component-did-update
      (fn [this old-props old-children]
        (render-ace-markers @watch-range))

      :reagent-render
      (fn [mode content watch-range]
        (do
          (deref watch-range) ; hacking non-React into React -- deref forces rerender on change
          [:div.stack-2-3
            [:button.watch-button {:type "submit" :on-click #(on-set-watch (ace-selected-range))} "Watch"]
            [:div#editor]]))})))
