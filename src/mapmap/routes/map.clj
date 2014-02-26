(ns mapmap.routes.map
  (:use compojure.core)
  (:require [mapmap.views.layout :as layout]
            [noir.response :refer [json]]))

(defn map-page
  "returns JSON string"
  [id]
  (slurp (str "src/mapmap/model/json/" id)))

(defroutes map-routes
  (GET "/map/:id" id (map-page id)))
