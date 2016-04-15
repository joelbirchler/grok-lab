(ns grok_lab.editor
  (:require [reagent.core :as r]
            [cljs.pprint :refer [pprint]]))

(enable-console-print!)

(defonce ace-range-constructor
  (.-Range (.require js/ace "ace/range")))

;; TODO: Remove?
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
  (let [session (.getSession (ace-editor))
        doc (.getDocument session)
        start-anchor (.createAnchor doc (marker-range 0) (marker-range 1))
        end-anchor (.createAnchor doc (marker-range 2) (marker-range 3))
        range (ace-range-constructor.)]
    (set! (.-start range) start-anchor)
    (set! (.-end range) end-anchor)
    (.addMarker session range "watch-marker" "text")))

;; This might be helpful: https://github.com/tlatoza/SeeCodeRun/wiki/Ace-code-editor

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
           (.on "change" #(on-change (.getValue (ace-editor))))
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
