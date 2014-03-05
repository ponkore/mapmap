(ns mapmap.views.geojson-util
  (:require [clojure.data.json :as json]
            [mapmap.model.geojson :as model]))

(defn to-station-feature-map
  ""
  [info]
  (assoc {}
    :type "Feature"
    :id (:id info)
    :properties {:name (:station-name info)}
    :geometry {:type "Point" :coordinates (:geometry info)}))

(defn to-line-feature-map
  ""
  [info]
  (assoc {}
    :type "Feature"
    :id (:id info)
    :properties {:name (:line-name info)}
    :geometry (:geometry info)))

(defn make-feature-collection
  ""
  [coll]
  (assoc {}
    :type "FeatureCollection"
    :features coll))
