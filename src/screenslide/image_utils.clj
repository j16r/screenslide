(ns screenslide.image-utils)

(defmulti dimensions class)
(defmethod dimensions org.eclipse.swt.graphics.Image [image]
  (let [rect (.getBounds image)]
    [(.width rect) (.height rect)]))
(defmethod dimensions org.eclipse.swt.widgets.Shell [image]
  (let [rect (.getBounds image)]
    [(.width rect) (.height rect)]))
(defmethod dimensions org.eclipse.swt.graphics.ImageData [image-data]
  [(.width image-data) (.height image-data)])

(defn fit-to-viewport [width height max-width max-height]
  "Scale rectangle to fill the target rectangle without leaving any space"
  (let [image-ratio (/ width height)
        screen-ratio (/ max-width max-height)]
    (if (< screen-ratio image-ratio)
      [(* width (/ max-height height)) max-height]
      [max-width (* height (/ max-width width))])))

(defn fit-image-to-viewport [image max-width max-height]
  (let [[width height] (dimensions image)]
    (fit-to-viewport width height max-width max-height)))

(defn scale-to-viewport [width height max-width max-height]
  "Scale rectangle to fit within a target rectangle"
  (let [image-ratio (/ width height)
        screen-ratio (/ max-width max-height)]
    (if (> screen-ratio image-ratio)
      [(* width (/ max-height height)) max-height]
      [max-width (* height (/ max-width width))])))

(defn scale-image-to-viewport [image max-width max-height]
    (let [[width height] (dimensions image)]
      (scale-to-viewport width height max-width max-height)))

(defn center-to-viewport [width height max-width max-height]
  [(/ (- max-width width) 2) (/ (- max-height height) 2)])
