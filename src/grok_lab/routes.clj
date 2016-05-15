(ns grok-lab.routes
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [grok-lab.view :as view]))


;; This is just temporary while we figure out the design
(def all-the-data
  {:text
"# Markdown Text Here
We only want educated cats, so we're going to _map_ over all of our cats and
_apply school_ to each one. This will give us a _new cat_, leaving the original
uneducated cat intact and blissfully unaware of science and philosophy.

![Doctor Cat Map](/images/doctor-cat-map.jpg)

Probably want a better description of map and parallelization here."
   :code
"const cats = [\"Babou\",
              \"Gilbert The Destroyer\",
              \"Wittle Whiskers\"];

const school = (str) => `Dr. ${str}`;

cats.map(school).join(\", \");"
  :includes ["https://cdnjs.cloudflare.com/ajax/libs/lodash.js/4.11.1/lodash.min.js"]
  :watch [136 163]
})


(defroutes app-routes
  (GET "/" [] (view/render view/index))
  (GET "/pad/1/slide/1" [] {:status 200 :body all-the-data})
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> app-routes
    (wrap-json-body {:keywords? true})
    (wrap-json-response)
    (wrap-defaults site-defaults)))
