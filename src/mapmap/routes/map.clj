(ns mapmap.routes.map
  (:use compojure.core)
  (:require [mapmap.views.layout :as layout]
            [noir.response :refer [json]]))

(defn map-page
  "returns JSON string"
  [params]
  (slurp (str "src/mapmap/model/json/" (get-in params [:params :id]))))

(defroutes map-routes
  (GET "/map/:id" params (map-page params)))
