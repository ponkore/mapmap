(ns mapmap.geojson
  (:require [clojure.string :as str]
            [clojure.data.json :as json])
  (:import (java.io File)))

(defn calc-bounding-box
  [coll]
  (reduce
   (fn [[min-lon min-lat max-lon max-lat] [lon lat]]
     [(min min-lon lon) (min min-lat lat)
      (max max-lon lon) (max max-lat lat)])
   [999 999 0 0] coll))

(defn json->station [station-info]
  (let [info station-info
        geometry (:geometry info)]
    (assoc {}
      :id (:id info)
      :station-name (get-in info '(:properties :N05_011))
      :geometry geometry)))

(defn json->line [line-info]
  (let [info line-info
        geometry (:geometry info)]
    (assoc {}
      :id (:id info)
      :line-name (get-in info '(:properties :N05_002))
      :geometry geometry
      :bounding-box (calc-bounding-box (:coordinates geometry)))))

(defn read-all-data
  [fname transform-fn]
  (let [json-data (-> fname slurp (json/read-str :key-fn keyword))]
    (->> json-data
         :features
         (map transform-fn))))

(defn read-all-lines
  []
  (read-all-data "resources/public/json/JRW-railroad.geojson" json->line))

;;(def lines (read-all-lines))

(defn read-all-stations
  []
  (read-all-data "resources/public/json/JRW-stations.geojson" json->station))

;;(def stations (read-all-stations))

(defn to-station-feature-map
  [info]
  {:type "Feature"
   :id (:id info)
   :properties {:name (:station-name info)}
   :geometry (:geometry info)})

(defn to-line-feature-map
  [info]
  {:type "Feature"
   :id (:id info)
   :properties {:name (:line-name info)}
   :geometry (:geometry info)})

;;; {:type "FeatureCollection" :features []}

(defn make-feature-collection
  [coll]
  {:type "FeatureCollection"
   :features coll})

;;; (make-feature-collection (map to-line-feature-map lines))
