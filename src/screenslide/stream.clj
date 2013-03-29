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

(defn create-scaled-image [image-data display width height new-width new-height]
  (Image. display (.scaledTo image-data new-width new-height)))
