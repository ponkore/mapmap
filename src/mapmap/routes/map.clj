(ns mapmap.routes.map
  (:use compojure.core)
  (:require [mapmap.views.layout :as layout]
            [noir.response :refer [json]]))

(defn- map-page
  "returns JSON string"
  [id]
  (slurp (str "src/mapmap/model/json/" id)))

(defn- map-view
  ""
  []
  (layout/render "ng-map.html" {}))

(defroutes map-routes
  (GET "/mapview" [] (map-view))
  (GET "/map/:id" [id] (map-page id)))
