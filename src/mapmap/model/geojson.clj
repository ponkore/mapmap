(ns mapmap.model.geojson
  (:require [clojure.data.json :as json]))

(def ^{:private true}
  json-root-dir
  "GeoJSON の配置しているディレクトリ"
  "src/mapmap/model/json/")

(def ^{:private true}
  earth-r
  "地球の半径(km)"
  6378.137)

(defn- calc-bounding-box
  "[[lon1 lat1][lon2 lat2] ...] の形式の「最大領域」となる矩形領域を算出する。"
  [coll]
  (reduce
   (fn [[min-lon min-lat max-lon max-lat] [lon lat]]
     [(min min-lon lon) (min min-lat lat)
      (max max-lon lon) (max max-lat lat)])
   [999 999 0 0] coll))

(defn- json->station
  "GeoJson から読み取った元データの駅の情報を内部形式に変換する。"
  [station-info]
  (if (and (= (:type station-info) "Feature")
           (= (get-in station-info '(:geometry :type)) "Point"))
    (assoc {}
      :id (:id station-info)
      :station-name (get-in station-info '(:properties :N05_011))
      :geometry (get-in station-info '(:geometry :coordinates)))
    nil))

(defn- json->line
  "GeoJson から読み取った元データの路線の情報を内部形式に変換する。"
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

(defn- read-all-data
  ""
  ([fname transform-fn]
     (read-all-data json-root-dir fname transform-fn))
  ([base-dir fname transform-fn]
     (let [fullpath (str base-dir fname)
           json-data (-> fullpath slurp (json/read-str :key-fn keyword))]
       (->> json-data
            :features
            (map transform-fn)))))

(def ^{:private true}
  lines
  "全路線を読み取った Collection (delayed)。"
  (delay (read-all-data "JRW-railroad.geojson" json->line)))

(def ^{:private true}
  stations
  "全駅を読み取った Collection (delayed)。"
  (delay (read-all-data "JRW-stations.geojson" json->station)))

(defn get-lines
  "条件に適合する路線情報を返す。"
  ([] (get-lines identity))
  ([filter-fn]
     (filter filter-fn @lines)))

(defn get-stations
  "条件に適合する駅を返す。"
  ([] (get-stations identity))
  ([filter-fn]
     (filter filter-fn @stations)))

(defn distance
  "２つの点の間の距離(km)を求める。
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

(defn find-mid-point
  "２つの点を結ぶ直線の上の、始点から特定の距離にある位置を求める"
  ([lon1 lat1 lon2 lat2 mid]
     (find-mid-point [lon1 lat1] [lon2 lat2] mid))
  ([[lon1 lat1] [lon2 lat2] mid]
     (let [d (distance lon1 lat1 lon2 lat2)]
       (if (> mid d)
         nil
         (let [r (/ mid d)
               lon-n (+ lon1 (* r (- lon2 lon1)))
               lat-n (+ lat1 (* r (- lat2 lat1)))]
           [lon-n lat-n])))))
