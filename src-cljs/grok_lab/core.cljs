(ns grok_lab.core
  (:require [reagent.core :as r]))

;; TODO: Get this to work with println(str)
;; TODO: Make sure we terminate reasonably (handle errors, hangs, etc)
;; TODO: Think about __watch__(uid, code) wrappers

(defn bootstrapped-code [code]
  (str
    "const println = (str) => { postMessage({'event': 'print', 'value': str}); };"
    code
    "; postMessage({'event': 'complete'})"))

(defn run [code]
  (let [blob (js/Blob. (array (bootstrapped-code code)) {:type "application/javascript"})
        obj-url (.createObjectURL js/URL blob)
        worker (js/Worker. obj-url)]
    (set! (.-onmessage worker) #(.log js/console (.-data %)))))

(defn awesome-ide []
  (let [code (atom "console.log('cats!'); 2 + 2;")]
    [:div
      [:textarea {:value @code}]
      [:button {:type "submit" :onClick #(run @code)} "Run"]
      [:pre "—"]]))

(defn ^:export main []
  (r/render [awesome-ide]
    (js/document.getElementById "content")))
