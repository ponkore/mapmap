(ns mapmap.model.geojson-test
  (:require [mapmap.model.geojson :refer :all]
            [midje.sweet :refer :all]))

(fact "(private)calc-bounding-box"
  (fact "collection types: array, list, ..."
    (#'mapmap.model.geojson/calc-bounding-box [[0 1] [2 3]]) => [0 1 2 3]
    (#'mapmap.model.geojson/calc-bounding-box '([0 1] [2 3])) => [0 1 2 3]
    (#'mapmap.model.geojson/calc-bounding-box '((0 1) (2 3))) => [0 1 2 3])
  (fact "many elements of array"
    (#'mapmap.model.geojson/calc-bounding-box [[-1 -1] [20 30] [1 2] [40 5]]) => [-1 -1 40 30])
  (fact "other pattern"
    (#'mapmap.model.geojson/calc-bounding-box [[3 2] [1 0]]) => [1 0 3 2]))

(fact "(private)json->station"
  (fact "argument is nil"
    (#'mapmap.model.geojson/json->station nil) => nil)
  (fact "argument is not Feature"
    (#'mapmap.model.geojson/json->station {:type "foo",
                                     :geometry {:coordinates [1.1 2.2], :type "Point"},
                                     :properties {}}) => nil)
  (fact "argument is not Point"
    (#'mapmap.model.geojson/json->station {:type "Feature",
                                     :geometry {:coordinates [1.1 2.2], :type "Polygon"},
                                     :properties {}}) => nil)
  (fact "normal case 1"
    (#'mapmap.model.geojson/json->station
     {:geometry {:coordinates [135.500035 34.73368], :type "Point"},
      :properties
      {:N05_002 "山陽新幹線",
       :N05_003 "西日本旅客鉄道（旧国鉄）",
       :N05_001 "1",
       :N05_005e "9999",
       :N05_011 "新大阪",
       :N05_005b "1972",
       :N05_008 nil,
       :N05_007 nil,
       :N05_009 nil,
       :N05_006 "EB03_19101001",
       :N05_004 "1972"},
      :type "Feature",
      :id 368}) => {:geometry [135.500035 34.73368], :id 368, :station-name "新大阪"}))

(fact "(private)json->line"
  (fact "argument is nil"
    (#'mapmap.model.geojson/json->line nil) => nil)
  (fact "argument is not Feature"
    (#'mapmap.model.geojson/json->line {:type "foo",
                                  :geometry {:coordinates [[1.1 2.2] [3.3 4.4]], :type "LineString"},
                                  :properties {}}) => nil)
  (fact "argument is not Point"
    (#'mapmap.model.geojson/json->line {:type "Feature",
                                  :geometry {:coordinates [[1.1 2.2]], :type "Polygon"},
                                  :properties {}}) => nil)
  (fact "normal case 1"
    (#'mapmap.model.geojson/json->line
     {:type "Feature",
      :id 16,
      :properties
      {:N05_001 "1",
       :N05_002 "山陽新幹線",
       :N05_003 "西日本旅客鉄道（旧国鉄）",
       :N05_004 "1972",
       :N05_005b "1975",
       :N05_005e "9999",
       :N05_006 "EB02_19101",
       :N05_007 nil,
       :N05_008 nil,
       :N05_009 nil,
       :N05_010 nil },
      :geometry { :type "LineString", :coordinates [[ 130.4, 33.5 ], [ 130.5, 33.6 ]]}}
     ) => {:id 16, :line-name "山陽新幹線",
           :bounding-box [130.4 33.5 130.5 33.6]
           :geometry [[130.4 33.5] [130.5 33.6]]}))

(fact "distance"
  (fact "two points"
    (distance [135.4949770 34.701909] [139.766084 35.681382]) => 405.80781066334544)
  (fact "four numbers"
    (distance 135.4949770 34.701909 139.766084 35.681382) => 405.80781066334544))
