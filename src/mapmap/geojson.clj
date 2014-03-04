(ns mapmap.geojson
  (:require [clojure.string :as str]
            [clojure.data.json :as json])
  (:import (java.io File)))

(defn- calc-bounding-box
  "[[lon1 lat1][lon2 lat2] ...] の形式の「最大領域」となる矩形領域を算出する。"
  [coll]
  (reduce
   (fn [[min-lon min-lat max-lon max-lat] [lon lat]]
     [(min min-lon lon) (min min-lat lat)
      (max max-lon lon) (max max-lat lat)])
   [999 999 0 0] coll))

(defn- json->station
  "元データ(駅)の１GeoJson を内部形式(駅)に変換する。"
  [station-info]
  (if (and (= (:type station-info) "Feature")
           (= (get-in station-info '(:geometry :type)) "Point"))
    (assoc {}
      :id (:id station-info)
      :station-name (get-in station-info '(:properties :N05_011))
      :geometry (get-in station-info '(:geometry :coordinates)))
    nil))

(defn- json->line
  "元データ(線)の１GeoJson を内部形式(線)に変換する。"
  [line-info]
  (if (and (= (:type line-info) "Feature")
           (= (get-in line-info '(:geometry :type)) "LineString"))
    (let [geometry (get-in line-info '(:geometry :coordinates))]
      (assoc {}
        :id (:id line-info)
        :line-name (get-in line-info '(:properties :N05_002))
        :geometry geometry
        :bounding-box (calc-bounding-box geometry)))
    nil))

(def ^{:private true}
  json-root-dir
  "GeoJSON の配置しているディレクトリ" "src/mapmap/model/json/")

(defn- read-all-data
  ""
  ([fname transform-fn]
     (read-all-data json-root-dir fname transform-fn))
  ([root-dir fname transform-fn]
     (let [fullpath (str root-dir fname)
           json-data (-> fullpath slurp (json/read-str :key-fn keyword))]
       (->> json-data
            :features
            (map transform-fn)))))

(defn- read-all-json
  ""
  [file transform-fn]
  (delay (read-all-data (str json-root-dir file) transform-fn)))

(defn- to-station-feature-map
  ""
  [info]
  (assoc {}
    :type "Feature"
    :id (:id info)
    :properties {:name (:station-name info)}
    :geometry (:geometry info)))

(defn- to-line-feature-map
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

(def ^{:private true} earth-r 6378.137) ;; 地球の半径 (km)

(defn distance
  "return two points of distance as 'km'.
see http://www.kiteretsu-so.com/archives/1183 "
  ([lon1 lat1 lon2 lat2]
     (distance [lon1 lat1] [lon2 lat2]))
  ([[lon1 lat1] [lon2 lat2]]
     (let [[lon1 lon2] (if (< lon1 lon2) [lon1 lon2] [lon2 lon1])
           [lat1 lat2] (if (< lat1 lat2) [lat1 lat2] [lat2 lat1])
           lat-rad (-> (- lat2 lat1) Math/toRadians)
           lon-rad (-> (- lon2 lon1) Math/toRadians)
           y-diff (* earth-r lat-rad)
           x-diff (* (Math/cos (Math/toRadians lat1)) earth-r lon-rad)]
       (Math/sqrt (+ (* x-diff x-diff) (* y-diff y-diff))))))

;;(def lines (read-all-json "JRW-railroad.geojson" json->line))
;;(->> @lines (map #(dissoc % :geometry :bounding-box)))
;;(->> @lines (map to-line-feature-map) (take 2))
;;(def stations (read-all-json "JRW-stations.geojson" json->station))
;;(->> @stations (map to-station-feature-map) (take 10))
;;(->> @stations (group-by :station-name) (vals) (filter #(>= (count %) 2)))
;;(def h (->> @stations (group-by :station-name) (vals) (filter #(>= (count %) 2))))
;;(def c (get-in (->> @lines (map to-line-feature-map) (first)) '(:geometry :coordinates)))
;; (partition 2 1 c)
;; (take 4 (map (fn [[p1 p2]] (distance p1 p2)) (partition 2 1 c)))
