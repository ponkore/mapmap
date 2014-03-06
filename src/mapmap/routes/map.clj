(ns mapmap.routes.map
  (:use compojure.core)
  (:require [mapmap.views.layout :as layout]
            [mapmap.views.geojson-util :as gj-util]
            [mapmap.model.geojson :as model]
            [noir.response :refer [json]]))

(defn- find-stations
  ""
  [params]
  (let [query-params (:query-params params)
        get-double-fn (fn [key param] (-> (get param key) Double/valueOf))
        left   (get-double-fn "left" query-params)
        top    (get-double-fn "top" query-params)
        right  (get-double-fn "right" query-params)
        bottom (get-double-fn "bottom" query-params)
        filter-fn (fn [info]
                    (when-let [[lon lat] (:geometry info)]
                      (and (<= left lon right) (<= bottom lat top))))]
    (->> (model/get-stations filter-fn)
         (map gj-util/to-station-feature-map)
         (gj-util/make-feature-collection))))

(defroutes map-routes
  ;; main view page
  (GET "/mapview" [] (layout/render "ng-map.html" {}))
  ;; station list json generator
  (GET "/map/stations" params (-> params find-stations json)))
