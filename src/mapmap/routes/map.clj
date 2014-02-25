(ns mapmap.routes.map
  (:use compojure.core)
  (:require [mapmap.views.layout :as layout]
            [noir.response :refer [json]]))

(defn map-page
  [request]
  (layout/render "jrw-lines-example.html"))

;;(def js-path "src/mapmap/controller/")

(defroutes map-routes
  (GET "/map" request (map-page request))
  ;;(GET "/controller/:id" [id] (slurp (str js-path id)))
  )
