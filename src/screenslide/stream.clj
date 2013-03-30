(ns screenslide.stream
  (:use screenslide.util
        screenslide.image-utils)
  (:import (org.eclipse.swt.graphics Image ImageData GC)))

(def images (ref nil))
(def current-images (ref nil))

(defn image-list [path]
  (filter #(re-find #"(?i)^[^.]+\.(jpg|jpeg)$" %)
    (map #(.getPath %) (file-seq (clojure.java.io/file path)))))

(defn load-images [path]
  (dosync (ref-set images (shuffle (image-list path)))))

(defn next-image []
  (let [image (first @images)]
    (dosync (alter images rest))
    image))
