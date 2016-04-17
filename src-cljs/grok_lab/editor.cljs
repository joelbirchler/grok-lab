(ns grok_lab.editor
  (:require [reagent.core :as r]
            [cljs.pprint :refer [pprint]]))

;;
;; The Ace Editor wasn't designed with functional purity in mind :)... so wrapping
;; Reagent around it in Clojure is a bit of a trick. There are some unpure functions
;; in here.
;;

(enable-console-print!)

(defonce ace-range-constructor
  (.-Range (.require js/ace "ace/range")))

(def ace-editor
  (memoize
    #(.edit js/ace "editor")))

(def ace-session
  (memoize
    #(.getSession (ace-editor))))

(def ace-document
  (memoize
    #(.getDocument (.getSession (ace-editor)))))

(defn ace-selected-range []
  "Grab the selected range from Ace (note: ace must be rendered)"
  (let [selection-obj (.getSelectionRange (ace-editor))
        start-sel (.-start selection-obj)
        end-sel (.-end selection-obj)]
    [(.-row start-sel) (.-column start-sel) (.-row end-sel) (.-column end-sel)]))

(defn get-watch-marker-ids []
  "Get a list of watch markers from the Ace getMarkers non-Array"
  (let [markers (js->clj (.getMarkers (ace-session)) :keywordize-keys true)]
    (reduce
      (fn [ids [key marker]]
        (if (= (:clazz marker) "watch-marker")
          (conj ids (:id marker))
          ids))
       [] markers)))

(defn remove-watch-markers []
  "Removes all watch markers from the editor"
  (doseq [marker-id (get-watch-marker-ids)]
    (.removeMarker (ace-session) marker-id)))

(defn create-anchor [row column]
  (.createAnchor (ace-document) row column))

(defn anchors->range [start-anchor end-anchor]
  (let [range (ace-range-constructor.)]
    (set! (.-start range) start-anchor)
    (set! (.-end range) end-anchor)
    range))

(defn render-watch-marker [[start-row start-col end-row end-col]]
  "Clears and redraws marker (note: ace must be rendered)"
  (let [start-anchor (create-anchor start-row start-col)
        end-anchor (create-anchor end-row end-col)
        range (anchors->range start-anchor end-anchor)]
    (remove-watch-markers)
    (.addMarker (ace-session) range "watch-marker" "text")
    (pprint
      (.positionToIndex (ace-document)
        (clj->js {:row start-row :column start-col})))))

;;
;; TODO: Code stuff should probably be elsewhere with eval in the code module?
;;
;; HA! Use ed.sessions.doc.positionToIndex({row, column})
;; If this works, we may want to store the watch marker this way and translate back and forth.
;; So core will have the text-based knowledge and the editor will have the marker and position nonsense.




(defn editor [mode content watch-range]
  "React wrapper for Ace"
  (let [on-change    #(reset! content %)
        on-set-watch #(reset! watch-range %)
        set-watch-on-selection #(on-set-watch (ace-selected-range)) ]

    (r/create-class
      {:component-did-mount
      (fn [this]
       (doto (ace-editor)
         (aset "$blockScrolling" js/Infinity) ; hides deprecation warning
         (.setTheme "ace/theme/tomorrow_night")
         (.setValue @content -1))

       (doto (ace-session)
         (.on "change" #(on-change (.getValue (ace-editor))))
         (.setMode "ace/mode/javascript"))

       (doto (.-commands (ace-editor))
         (.addCommand (clj->js {
           :name "setWatchOnSelection"
           :bindKey {:win "Ctrl-w" :mac "Ctrl-w"}
           :exec set-watch-on-selection})))

       (render-watch-marker @watch-range))

      :component-did-update
      (fn [this old-props old-children]
        (render-watch-marker @watch-range))

      :reagent-render
      (fn [mode content watch-range]
        (do
          (deref watch-range) ; hacking non-React into React -- deref forces rerender on change
          [:div.stack-2-3
            [:button.watch-button {:type "submit" :on-click set-watch-on-selection} "Watch"]
            [:div#editor]]))})))
