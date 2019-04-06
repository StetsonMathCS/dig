(ns diglive.core
  (:require [ring.util.response :as response]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.content-type :refer [wrap-content-type]])
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route])
  (:require [hiccup.core :refer :all]))

(def ideas (atom {})) ; keys are ids, values are ideas
(def votes (atom {})) ; keys are ids, values are vote counts

(defn add-idea
  [prefix suffix]
  (let [id (hash [prefix suffix])
        idea {:id id :prefix prefix :suffix suffix}]
    (swap! ideas assoc id idea)
    (swap! votes assoc id 0)
    idea))

(defn process-vote
  [id dumb?]
  (swap! votes update-in [id] (if dumb? inc dec)))

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

(defn show-top-5
  []
  [:div
   (for [id (map first (take 5 (reverse (sort-by second (seq @votes)))))
         :let [idea (get @ideas id)]]
     [:div.row
      [:div.col-12
       [:h2.display-2.text-right (format "%s %s." (:prefix idea) (:suffix idea))]]])
   [:div.fixed-bottom
    [:div.col-12
     [:p.text-center
      [:a.btn.btn-primary.btn-lg
       {:href "/" :role "button"} "That's dumb."]]]]])

(defroutes app-routes
  (GET "/" []
       (process-vote (:id (add-idea "foo" "bar")) true)
       (prn "Ideas:" @ideas)
       (prn "Votes:" @votes)
       (render (heading "Hello, world!")))
  (GET "/top" []
       (render (show-top-5)))
  (route/not-found (render (heading "404"))))

(def handler
  (-> app-routes
      (wrap-resource "public")
      (wrap-content-type)
      (wrap-params)))
