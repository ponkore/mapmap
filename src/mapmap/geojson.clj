(ns mapmap.geojson
  (:require [clojure.string :as str]
            [clojure.data.json :as json])
  (:import (java.io File)))

(defn- calc-bounding-box
  "[[xs1 ys1 xe1 ye1][xs1 ys1 xe1 ye1] ...] の形式の「最大領域」となる矩形領域を算出する。"
  [coll]
  (reduce
   (fn [[min-lon min-lat max-lon max-lat] [lon lat]]
     [(min min-lon lon) (min min-lat lat)
      (max max-lon lon) (max max-lat lat)])
   [999 999 0 0] coll))

(defn- json->station
  "元データ(駅)の１GeoJson を内部形式(駅)に変換する。"
  [station-info]
  (let [info station-info
        geometry (:geometry info)]
    (assoc {}
      :id (:id info)
      :station-name (get-in info '(:properties :N05_011))
      :geometry geometry)))

(defn- json->line
  "元データ(線)の１GeoJson を内部形式(線)に変換する。"
  [line-info]
  (let [info line-info
        geometry (:geometry info)]
    (assoc {}
      :id (:id info)
      :line-name (get-in info '(:properties :N05_002))
      :geometry geometry
      :bounding-box (calc-bounding-box (:coordinates geometry)))))

(def ^{:private true}
  json-root-dir
  "GeoJSON の配置しているディレクトリ" "src/mapmap/model/json/")

(defn- read-all-data
  ""
  [fname transform-fn]
  (let [json-data (-> fname slurp (json/read-str :key-fn keyword))]
    (->> json-data
         :features
         (map transform-fn))))

(defn- read-all-json
  ""
  [file transform-fn]
  (delay (read-all-data (str json-root-dir file) transform-fn)))

(defn- to-station-feature-map
  ""
  [info]
  {:type "Feature"
   :id (:id info)
   :properties {:name (:station-name info)}
   :geometry (:geometry info)})

(defn- to-line-feature-map
  ""
  [info]
  {:type "Feature"
   :id (:id info)
   :properties {:name (:line-name info)}
   :geometry (:geometry info)})

(defn make-feature-collection
  ""
  [coll]
  {:type "FeatureCollection"
   :features coll})

;;(def lines @(read-all-json "JRW-railroad.geojson" json->line))
;;(def satiosns @(read-all-json "JRW-railroad.geojson" json->station))
;;(->> stations (map to-station-feature-map) (take 10))
;;(->> lines (map to-line-feature-map) (take 2))
;;(make-feature-collection (map to-line-feature-map @lines))
;;(clojure.pprint/pprint (->> @stations (map to-station-feature-map) (take 10)))
;;(clojure.pprint/pprint (->> @lines (map to-line-feature-map) (take 1)))
