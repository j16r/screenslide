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

(defn create-slide [image-path display shell]
  (let [image-data (ImageData. image-path)
        [width height] (dimensions image-data)
        [max-width max-height] (dimensions shell)
        [new-width new-height] (fit-to-viewport width height max-width max-height)
        [x y] (center-to-viewport new-width new-height max-width max-height)]
    (println "Creating slide " image-path)
    {:image (create-scaled-image image-data display width height new-width new-height)
     :x x
     :y y
     :width new-width
     :height new-height
     :alpha 0}))

(defn advance-slideshow [display shell]
  (try
    (when-let [image (next-image)]
      (println "Loading image: " image)
      (dosync
        (alter current-images #(conj (rest %) (create-slide image display shell)))))
    (catch org.eclipse.swt.SWTException e
      (println "EXCEPTION!" e))))

(defn animate-slides []
  (dosync
    (alter
      current-images
      #(map (fn [image] (assoc image :alpha (min 255 (+ (:alpha image) 20)))) %))))

(defn draw-slideshow [gc]
  (doseq [{image :image x :x y :y alpha :alpha} @current-images]
    (.setAlpha gc ^int alpha)
    (.drawImage gc ^Image image ^int x ^int y)))
