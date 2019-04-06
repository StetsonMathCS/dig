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

(defn get-random-idea
  []
  (let [[idea1 idea2] (take 2 (shuffle (vals @ideas)))]
    (if (or (< 0.25 (rand)) (nil? idea2))
      idea1
      (add-idea (:prefix idea1) (:suffix idea2)))))

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

(defn ask-prefix-suffix
  []
  [:div
   (heading "Dumb Idea Generator")
   [:div.jumbotron
    [:h1.display-3 "What's a dumb idea?"]
    [:p.lead
     [:form.form-inline {:action "/new" :method "post"}
      [:div.form-group
       [:input.form-control.form-control-lg
        {:type "text" :name "prefix" :placeholder "Shave all the"}]]
      [:div.form-group.mx-3
       [:input.form-control.form-control-lg
        {:type "text" :name "suffix" :placeholder "cats"}]]
      [:div.form-group.mx
       [:button.btn-primary.btn.btn-lg
        {:type "submit"} "This is dumb."]]]]]])

(defn ask-if-dumb-idea
  [idea]
  [:div.jumbotron
   [:h1.display-3 (format "%s %s." (:prefix idea) (:suffix idea))]
   [:p.lead "Is this a dumb idea?"]
   [:p.lead
    [:form.form-inline {:action "/vote" :method "post"}
     [:input {:type "hidden" :name "id" :value (:id idea)}]
     [:div.form-group
      [:button.btn-primary.btn.btn-lg
       {:type "submit" :name "answer" :value "dumb"} "Dumb"]]
     [:div.form-group.mx-3
      [:button.btn-secondary.btn.btn-lg
       {:type "submit" :name "answer" :value "notdumb"} "Not dumb"]]]]])

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
  (GET "/new" []
       (render (ask-prefix-suffix)))
  (POST "/new" [prefix suffix]
        (add-idea prefix suffix)
        (response/redirect "/"))
  (GET "/top" []
       (render (show-top-5)))
  (GET "/vote" []
       (render (let [idea (get-random-idea)]
                 (ask-if-dumb-idea idea))))
  (POST "/vote" [id answer]
        (try
          (process-vote (Long/parseLong id) (= answer "dumb"))
          (catch Exception _))
        (response/redirect "/top"))
  (route/not-found (render (heading "404"))))

(def handler
  (-> app-routes
      (wrap-resource "public")
      (wrap-content-type)
      (wrap-params)))
