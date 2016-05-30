(ns voting-app.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [clojure.java.jdbc :as sql]
            [hiccup.core :as hiccup]))

(def ^:constant db "postgresql://vagrant:foobar@localhost:5432/fcc_provision")

(defn render-poll [id authenticated?]
  (let [data-qus (sql/query db ["SELECT * FROM polls WHERE ID = ?" id])
        data-opt (sql/query db ["SELECT * FROM options WHERE ID = ?" id])]
    (hiccup/html
     [:h1 (:question (first data-qus))]
     (for [opt data-opt]
       [:div
        [:h2 (:option opt)]
        [:h3 (str (:votes opt) " people have voted for this.")]]))))

(defn add-layout [html]
  (hiccup/html
   [:html
    [:head
     [:link {:rel "stylesheet", :type "text/css", :href "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css"}]
     [:link {:rel "stylesheet", :type "text/css", :href "/style.css"}]]
    [:body
     [:nav.navbar.navbar-inverse.navbar-fixed-top
      [:a.navbar-brand {:href "/"} "Voting App"]
      [:a#twitter.btn.btn-primary "Log in with Twitter"]]
     [:div.container
      [:div.row
       [:div#central.col-md-10-col-md-offset-1
        html]]]]]))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (GET ["/:id" :id #"[a-z0-9]{6}"] [id] (add-layout (render-poll id)))
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (wrap-defaults app-routes site-defaults))
