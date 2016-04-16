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

(defn ace-editor []
  (.edit js/ace "editor"))

(defn ace-selected-range []
  "Grab the selected range from Ace (note: ace must be rendered)"
  (let [selection-obj (.getSelectionRange (ace-editor))
        start-sel (.-start selection-obj)
        end-sel (.-end selection-obj)]
    [(.-row start-sel) (.-column start-sel) (.-row end-sel) (.-column end-sel)]))

(defn get-watch-marker-ids []
  "Get a list of watch markers from the Ace getMarkers non-Array"
  (let [session (.getSession (ace-editor))
        markers (js->clj (.getMarkers session) :keywordize-keys true)]
    (reduce
      (fn [ids [key marker]]
        (if (= (:clazz marker) "watch-marker")
          (conj ids (:id marker))
          ids))
       [] markers)))

(defn remove-watch-markers []
  "Removes all watch markers from the editor"
  (let [session (.getSession (ace-editor))]
    (doseq [marker-id (get-watch-marker-ids)]
      (.removeMarker session marker-id))))

(defn render-watch-marker [marker-range]
  "Clears and redraws marker (note: ace must be rendered)"
  (let [session (.getSession (ace-editor))
        doc (.getDocument session)
        current-markers (js->clj (.getMarkers session))
        start-anchor (.createAnchor doc (marker-range 0) (marker-range 1))
        end-anchor (.createAnchor doc (marker-range 2) (marker-range 3))
        range (ace-range-constructor.)]
    (remove-watch-markers)
    (set! (.-start range) start-anchor)
    (set! (.-end range) end-anchor)
    (.addMarker session range "watch-marker" "text")))

(defn editor [mode content watch-range]
  "React wrapper for Ace"
  (let [on-change    #(reset! content %)
        on-set-watch #(reset! watch-range %)
        set-watch-on-selection #(on-set-watch (ace-selected-range)) ]

    (r/create-class
      {:component-did-mount
      (fn [this]
       (let [ace (ace-editor)
             session (.getSession ace)
             commands (.-commands ace)]

         (doto ace
           (aset "$blockScrolling" js/Infinity) ; hides deprecation warning
           (.setTheme "ace/theme/tomorrow_night")
           (.setValue @content -1))

         (doto session
           (.on "change" #(on-change (.getValue (ace-editor))))
           (.setMode "ace/mode/javascript"))

         (doto commands
           (.addCommand (clj->js {
             :name "setWatchOnSelection"
             :bindKey {:win "Ctrl-w" :mac "Ctrl-w"}
             :exec set-watch-on-selection})))

         (render-watch-marker @watch-range)))

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
