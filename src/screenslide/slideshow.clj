(ns screenslide.slideshow
  (:use screenslide.stream
        screenslide.util
        screenslide.image-utils)
  (:import (org.eclipse.swt.graphics Image ImageData GC))
  (:require [clojure.algo.generic.math-functions :as math]))

(defn create-scaled-image [image-data display width height new-width new-height]
  (Image. display (.scaledTo image-data new-width new-height)))

(defn create-slide [image-path display shell]
  (let [image-data (ImageData. image-path)
        [width height] (dimensions image-data)
        [max-width max-height] (dimensions shell)
        [new-width new-height] (fit-to-viewport width height max-width max-height)
        [x y] (center-to-viewport new-width new-height max-width max-height)]
    (create-scaled-image image-data display width height new-width new-height)))
(def get-slide
  (memoize create-slide))

(def alpha-minimum 0)
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
(defn following-slide-alpha [tick slide-duration]
  (- alpha-maximum (slide-alpha tick slide-duration)))

(def transition-delay 1000)
(def current-tick (atom 0))
(defn next-tick []
  (swap! current-tick inc))

(defn image-at-frame [images tick]
  (nth images (int (/ tick 25))))

(defn arrange-frames [& frames]
  (sort-by :alpha frames))

(defn create-frame [tick]
  (let [alpha-one (slide-alpha tick 25)
        alpha-two (- alpha-maximum alpha-one)
        slide-one (image-at-frame @images tick)
        slide-two (image-at-frame @images (+ 1 tick))]
    {:tick tick
     :slides (arrange-frames
               {:alpha alpha-one :image slide-one}
               {:alpha alpha-two :image slide-two})}))
(defn frame-sequence [ticks]
  (lazy-seq
    (cons
      (create-frame (first ticks))
      (frame-sequence (rest ticks)))))
(def frames (frame-sequence (iterate inc 0)))
(defn frame [tick] (nth frames tick))

(defn draw-slideshow [gc display shell]
  (let [tick (next-tick)
        current-frame (frame tick)]
    (println "Tick " tick " slides " (:slides current-frame))
    (doseq [{image :image alpha :alpha} (:slides current-frame)]
      (.setAlpha gc ^int alpha)
      (.drawImage gc ^Image (get-slide image display shell) 0 0))))
