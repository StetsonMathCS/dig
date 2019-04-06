(ns diglive.core
  (:require [ring.util.response :as response]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.content-type :refer [wrap-content-type]])
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route])
  (:require [hiccup.core :refer :all]))

(defn render
  [& content]
  (html
    [:head
     [:meta {:charset "utf-8"}]
     [:meta {:name "viewport" :content "width=device-width, initial-scale=1, shrink-to-fit=no"}]
     [:title "Dumb Idea Generator"]
     [:link {:rel "stylesheet" :href "https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css"}]]
    [:body
     [:div.container
      content]
     [:script {:src "https://code.jquery.com/jquery-3.2.1.slim.min.js"}]
     [:script {:src "https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js"}]
     [:script {:src "https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"}]]))

(defn heading
  [txt]
  [:div.row.mt-5.mb-5 [:div.col [:h1.display-1.text-center txt]]])

(defroutes app-routes
  (GET "/" [] (render (heading "Hello, world!")))
  (route/not-found (render (heading "404"))))

(def handler
  (-> app-routes
      (wrap-resource "public")
      (wrap-content-type)
      (wrap-params)))
