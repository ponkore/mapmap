(ns mapmap.routes.map
  (:use compojure.core)
  (:require [mapmap.views.layout :as layout]
            [noir.response :refer [json]]))

(defn map-page
  [request]
  (layout/render "jrw-lines-example.html"))

(defroutes map-routes
  (GET "/map" request (map-page request)))
