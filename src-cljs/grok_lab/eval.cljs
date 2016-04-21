(ns grok_lab.eval
  (:require [clojure.string :refer [trim replace]]))

(def bootstrap
  "const __grok_watch__ = function(result) { postMessage(JSON.stringify(result)); return result; };\n")

(defn- groom-watched [code]
  "Language-dependent removes whitespace and weird endings"
  (replace (trim code) #";$" ""))

(defn- instrument-code [code [watch-start watch-end]]
  (if (= watch-start watch-end)
    code
    (let [pre-watched  (.slice code 0 watch-start)
          watched      (groom-watched (.slice code watch-start watch-end))
          post-watched (.slice code watch-end)]
      (str bootstrap pre-watched "__grok_watch__(" watched ")" post-watched "; close();"))))

(defn- create-eval-worker [code]
  (let [blob (js/Blob. (array code) {:type "application/javascript"})
        obj-url (.createObjectURL js/URL blob)]
    (js/Worker. obj-url)))

(defn run [code log-handler error-handler]
  (let [worker (create-eval-worker code)]
    (js/setTimeout #(.terminate worker) 2000)
    (set! (.-onmessage worker)
      #(log-handler (.-data %)))
    (set! (.-onerror worker)
      (fn [event]
        (.preventDefault event)
        (error-handler {:message (.-message event) :line (dec (.-lineno event)) :col (.-colno event)})))))
