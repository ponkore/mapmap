(ns mapmap.routes.map
  (:use compojure.core)
  (:require [mapmap.views.layout :as layout]
            [mapmap.views.geojson-util :as gj-util]
            [mapmap.model.geojson :as model]
            [noir.response :refer [json]]))

(defn- map-page
  "returns JSON string"
  [id]
  (slurp (str "src/mapmap/model/json/" id)))

(defn- stations-json
  ""
  [params]
  (let [query-params (:query-params params)
        left (-> (get query-params "left") Double/valueOf)
        top (-> (get query-params "top") Double/valueOf)
        right (-> (get query-params "right") Double/valueOf)
        bottom (-> (get query-params "bottom") Double/valueOf)
        filter-fn (fn [info]
                    (when-let [[lon lat] (:geometry info)]
                      (and (<= left lon right) (<= bottom lat top))))]
    (->> (model/get-stations filter-fn)
         (map gj-util/to-station-feature-map)
         (gj-util/make-feature-collection)
         (json))))

(defn- map-view
  ""
  []
  (layout/render "ng-map.html" {}))

(defroutes map-routes
  (GET "/mapview" [] (map-view))
  (GET "/map/stations" params (stations-json params)))
