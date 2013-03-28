(ns screenslide.core
  (:use screenslide.util
        screenslide.stream)
  (:import (org.eclipse.swt.widgets Display Shell Canvas Listener)
           (org.eclipse.swt.graphics Image GC)
           (org.eclipse.swt.layout FillLayout)
           (org.eclipse.swt.events ShellAdapter))
  (:gen-class))

(defn dimensions [image]
  (let [rect (.getBounds image)]
    [(.width rect) (.height rect)]))

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

(defn create-shell [display shell canvas]
  (doto shell
    (.setText "Screenslide")
    (.setLayout (FillLayout.))
    ; Exit the app when the shell is closed
    (.addShellListener
      (proxy [ShellAdapter][]
        (shellClosed [evt]
          (System/exit 0)))))
  (doto canvas
    ; Close the app on keypress
    (.addListener
      org.eclipse.swt.SWT/KeyUp
      (proxy [Listener][]
        (handleEvent [event]
          (.close shell))))
    ; Paint the active images
    (.addListener
      org.eclipse.swt.SWT/Paint
      (proxy [Listener][]
        (handleEvent [event]
          (when-let [image @current-image]
            (println "Painting ..." image)
            (let [[width height] (dimensions image)
                  [new-width new-height] (fit-image-to-viewport image 1440 900)
                  [x y] (center-to-viewport new-width new-height 1440 900)]
              (println "Image scaled from " width height " to " new-width new-height)
              (.drawImage (.gc event) image 0 0 width height x y new-width new-height))))))))

(defn swt-loop [display shell canvas]
  (loop []
    (if (.isDisposed shell)
      (.dispose display)
      (do
        (if (not (.readAndDispatch display))
          (.sleep display))
        (recur)))))

(defn begin []
  (let [display (Display.)
        shell (Shell. display)
        canvas (Canvas. shell org.eclipse.swt.SWT/NO_BACKGROUND)]
    (create-shell display shell canvas)
    (.setSize shell 700 700)
    (.open shell)

    (interval display 1000
      (println "Timer!")
      (.redraw canvas)
      (try
        (when-let [image (next-image)]
          (println "Loading image: " image)
          (dosync (ref-set current-image (Image. display image))))
        (catch org.eclipse.swt.SWTException e
          (println "EXCEPTION!" e))))

    (swt-loop display shell canvas)))

(defn -main
  "Given a directory, perform a slide-show of all images contained within."
  [& args]
  (if-let [path (first args)]
    (do
      (load-images path)
      (begin))
    (println "Please specify a source directory.")))
