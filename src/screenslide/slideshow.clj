(ns screenslide.slideshow
  (:use screenslide.stream
        screenslide.util
        screenslide.image-utils)
  (:import (org.eclipse.swt.graphics Image ImageData GC))
  (:require [clj-time.core :as time]
            [clj-time.coerce :as time-coerce]
            [clojure.algo.generic.math-functions :as math]))

(defn epoch []
  (time-coerce/to-long (time/now)))
(def slideshow-stated (epoch))
(defn current-tick [] (- (epoch) slideshow-stated))

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

(def alpha-minimum 20)
(def alpha-maximum 255)

(defn slide-alpha [tick slide-duration]
  (int
    (max
      alpha-minimum
      (min
        alpha-maximum
        (* 700 (math/sin
                 (* (/ Math/PI slide-duration)
                    (rem tick slide-duration ))))))))

(defn draw-slideshow [gc]
  (let [tick (current-tick)]
    (doseq [{image :image x :x y :y alpha :alpha} @current-images]
      (let [alpha (slide-alpha tick 1000)]
        (println "Tick " tick " alpha " alpha)
        (.setAlpha gc ^int alpha)
        (.drawImage gc ^Image image ^int x ^int y)))))
