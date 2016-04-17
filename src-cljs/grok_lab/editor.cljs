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
    (remove-watch-markers)
    (.addMarker (ace-session) range "watch-marker" "text")))

(defn editor [mode content watch-range]
  "React wrapper for Ace"
  (let [on-change #(reset! content %)
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
