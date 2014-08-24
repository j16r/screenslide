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

(def ^:dynamic frames-per-second 25)
(def ^:dynamic slide-duration 1)
(def slide-ticks (* slide-duration frames-per-second))
(def redraw-interval (/ 1000 frames-per-second))

(defn constrain [value lower-bound upper-bound]
  (->
    value
    (min upper-bound)
    (max lower-bound value)
    (int)))
(defn alpha-constrain [value]
  (constrain value alpha-minimum alpha-maximum))
(defn round-to-multiple [value multiple]
  (- value (mod value multiple)))
(defn slide-alpha [tick]
  (let [offset (- tick (round-to-multiple tick frames-per-second))]
    (alpha-constrain
      (+ alpha-maximum
         (* -1 (+ offset (math/pow (- offset slide-duration) 2)))))))

(defn frame-index [tick]
  (int (/ tick slide-ticks)))

(defn frame [tick]
  (let [alpha-one (slide-alpha tick)
        alpha-two (- alpha-maximum alpha-one)
        image-index (frame-index tick)]
    {:tick tick
     :slides [{:alpha alpha-one
               :image (nth @images image-index)}
              {:alpha alpha-two
               :image (nth @images (+ 1 image-index))}]}))

(def current-tick (atom 0))
(defn next-tick []
  (swap! current-tick inc))

(defn slide-to-str [slide]
  (let [image (:image slide)]
    (str (:alpha slide)
         ":"
         (last (clojure.string/split image #"\/")))))

(defn draw-slideshow [gc display shell]
  (let [tick (next-tick)
        current-frame (frame tick)
        slides (:slides current-frame)]
    
    (println
      "Tick " tick
      " slides " (clojure.string/join ", " (map slide-to-str slides)))

    (let [sorted-slides (sort-by :alpha slides)]
      (doseq [{image :image alpha :alpha} sorted-slides]
        (.setAlpha gc ^int alpha)
        (.drawImage gc ^Image (get-slide image display shell) 0 0)))))
