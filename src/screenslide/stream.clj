(ns screenslide.stream
  (:use screenslide.image-utils)
  (:import (org.eclipse.swt.graphics Image)))

(def images (ref nil))
(def current-images (ref nil))

(defn image-list [path]
  (filter #(re-find #"(?i)^[^.]+\.(jpg|jpeg)$" %)
    (map #(.getPath %) (file-seq (clojure.java.io/file path)))))

(defn load-images [path]
  (dosync (ref-set images (shuffle (image-list path)))))

(defn next-image []
  (let [image (first @images)]
    (dosync (commute images rest))
    image))

(defn advance-slideshow [display]
  (try
    (when-let [image (next-image)]
      (println "Loading image: " image)
      (dosync (commute current-images #(conj (rest %) (Image. display image)))))
    (catch org.eclipse.swt.SWTException e
      (println "EXCEPTION!" e))))

(defn draw-slideshow [shell gc]
  (let [[max-width max-height] (dimensions shell)]
    (when-let [image (first @current-images) ]
      (println "Painting ..." image)
      (let [[width height] (dimensions image)
            [new-width new-height] (fit-image-to-viewport image max-width max-height)
            [x y] (center-to-viewport new-width new-height max-width max-height)]
        (println "Image scaled from " width height " to " new-width new-height)
        (.drawImage gc image 0 0 width height x y new-width new-height)))))
