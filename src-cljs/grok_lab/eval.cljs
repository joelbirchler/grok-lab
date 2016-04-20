(ns grok_lab.eval)

(def bootstrap
"const println = postMessage;
const __grok_watch__ = function(result) { println(result); return result; };")

(defn instrument-code [code [watch-start watch-end]]
  (if (= watch-start watch-end)
    (str bootstrap code)
    (let [pre-watched  (.slice code 0 watch-start)
          watched      (.slice code watch-start watch-end)
          post-watched (.slice code watch-end)]
      (str bootstrap pre-watched "__grok_watch__(" watched ")" post-watched))))

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
