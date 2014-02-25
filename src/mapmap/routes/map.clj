(ns mapmap.routes.map
  (:use compojure.core)
  (:require [mapmap.views.layout :as layout]
            [noir.response :refer [json]]))

(defroutes map-routes
  (GET "/map" request (json [:a "abc" 'd 123 {:d 2}])))
