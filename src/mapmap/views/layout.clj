(ns mapmap.views.layout
  (:require [selmer.parser :as parser]
            [clojure.string :as s]
            [ring.util.response :refer [content-type response]]
            [compojure.response :refer [Renderable]]))

(def template-path "mapmap/views/templates/")

(defn- render-file'
  [template params]
  (parser/render-file
          (str template-path template)
          params
          {:tag-open \[ :tag-close \]}))

(deftype RenderableTemplate [template params]
  Renderable
  (render [this request]
    (content-type
      (->> (assoc params
                  (keyword (s/replace template #".html" "-selected")) "active"
                  :servlet-context (:context request))
        (render-file' template)
        response)
      "text/html; charset=utf-8")))

(defn render [template & [params]]
  (RenderableTemplate. template params))

