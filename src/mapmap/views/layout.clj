(ns mapmap.views.layout
  (:require [selmer.parser :as parser]
            [ring.util.response :refer [response]])
  (:import compojure.response.Renderable))

(def template-path "mapmap/views/templates/")

(deftype RenderableTemplate [template params]
  Renderable
  (render [this request]
    (->> (assoc params :servlet-context (:context request))
         (parser/render-file (str template-path template))
         response)))

(defn render [template & [params]]
  (RenderableTemplate. template params))

