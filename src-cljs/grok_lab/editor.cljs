(ns grok_lab.editor
  (:require [reagent.core :as r]
            [cljs.pprint :refer [pprint]]))

;;
;; The Ace Editor wasn't designed with functional purity in mind :)... so wrapping
;; Reagent around it in Clojure is a bit of a trick. There are some unpure functions
;; in here.
;;

(enable-console-print!)

(def ace-editor
  (memoize
    #(.edit js/ace "editor")))

(def ace-session
  (memoize
    #(.getSession (ace-editor))))

(def ace-document
  (memoize
    #(.getDocument (.getSession (ace-editor)))))

(defonce ace-range-constructor
  (.-Range (.require js/ace "ace/range")))

(defn index->js-position [index]
  "Converts a string index to an ace {row, column} js object"
  (.indexToPosition (ace-document) index))

(defn index->vec-position [index]
  (let [js-position (index->js-position index)]
    [(.-row js-position) (.-column js-position)]))

(defn js-position->index [js-position]
  "Converts an ace {row, column} js object to a string index??"
  (.positionToIndex (ace-document) js-position))

(defn ace-selected-index-range []
  "Grab the selected range in terms of [start-index end-index] (note: ace must be rendered)"
  (let [selection-obj (.getSelectionRange (ace-editor))]
    (map js-position->index [(.-start selection-obj) (.-end selection-obj)])))

(defn get-markers []
  "Get a list of markers from Ace (which sends it as an Array-like object)"
  (map
    (fn [[key marker]] marker)
    (js->clj (.getMarkers (ace-session)) :keywordize-keys true)))

(defn get-watch-marker []
  (first (filter
    #(= "watch-marker" (:clazz %))
    (get-markers))))

(defn get-watch-index-range []
  (let [range (:range (get-watch-marker))]
    (map js-position->index [(.-start range) (.-end range)])))

(defn create-anchor [row column]
  (.createAnchor (ace-document) row column))

(defn anchors->ace-range [start-anchor end-anchor]
  (let [range (ace-range-constructor.)]
    (set! (.-start range) start-anchor)
    (set! (.-end range) end-anchor)
    range))

(defn render-watch-marker [[start-index end-index]]
  "Clears and redraws marker (note: ace must be rendered)"
  (let [start-anchor (apply create-anchor (index->vec-position start-index))
        end-anchor (apply create-anchor (index->vec-position end-index))
        range (anchors->ace-range start-anchor end-anchor)]
    (.removeMarker (ace-session) (:id (get-watch-marker)))
    (.addMarker (ace-session) range "watch-marker" "text")))

(defn editor [mode content watch-range]
  "React wrapper for Ace"
  (let [on-change (fn [updated-content]
          (reset! watch-range (get-watch-index-range))
          (reset! content updated-content))
        set-watch-on-selection #(reset! watch-range (ace-selected-index-range)) ]

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
