(ns hello-compojure.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [hello-compojure.view :as view]))

(defroutes app-routes
  (GET "/" [] "Hello Worlds")
  (GET "/hello/:name" [name] (view/render view/hello [name]))
  (GET "/:view-name" [view-name :as {params :params}] 
    (view/render 
      (ns-resolve 'hello-compojure.view (symbol view-name)) [params]))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
