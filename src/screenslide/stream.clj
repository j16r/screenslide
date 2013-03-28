(ns screenslide.stream)

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
