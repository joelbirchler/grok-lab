(ns grok_lab.eval)

(def bootstrap
"const println = postMessage;
const __grok_watch__ = function(result) { println(result); return result; };")

(defn instrument-code [code [watch-start watch-end]]
  (str
    bootstrap
    (.slice code 0 watch-start)
    "__grok_watch__("
    (.slice code watch-start watch-end)
    ")"
    (.slice code watch-end)))

(defn create-eval-worker [code]
  (let [blob (js/Blob. (array code) {:type "application/javascript"})
        obj-url (.createObjectURL js/URL blob)]
    (js/Worker. obj-url)))

(defn run [code log-handler error-handler]
  (let [worker (create-eval-worker code)]
    (set! (.-onmessage worker)
      #(log-handler (.-data %)))
    (set! (.-onerror worker)
      (fn [event]
        (.preventDefault event)
        (error-handler {:message (.-message event) :line (.-lineno event) :col (.-colno event)})))))
