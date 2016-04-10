(ns grok_lab.eval)


(defn bootstrapped-code [code]
  (str
    "const println = postMessage;"
    code))

(defn create-eval-worker [code]
  (let [blob (js/Blob. (array (bootstrapped-code code)) {:type "application/javascript"})
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
