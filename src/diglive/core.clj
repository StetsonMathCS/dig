(ns diglive.core
  (:require [ring.util.response :as response])
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route])
  (:require [hiccup.core :refer :all]))

(defroutes app-routes
  (GET "/" [] "Hello, world!"))

(def handler
  (handler/site app-routes))

