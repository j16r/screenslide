(ns screenslide.core
  (:use screenslide.util
        screenslide.stream
        screenslide.image-utils)
  (:import (org.eclipse.swt.widgets Display Shell Canvas Listener)
           (org.eclipse.swt.layout FillLayout)
           (org.eclipse.swt.events ShellAdapter))
  (:gen-class))

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
          (draw-slideshow (.gc event)))))))

(defn swt-loop [display shell canvas]
  (loop []
    (if (.isDisposed shell)
      (.dispose display)
      (do
        (if (not (.readAndDispatch display))
          (.sleep display))
        (recur)))))

(def fps 25)
(def frame-delay (/ 1000 fps))
(def change-image-delay 1000)

(defn begin []
  (let [display (Display.)
        shell (Shell. display)
        canvas (Canvas. shell org.eclipse.swt.SWT/NO_BACKGROUND)]
    (create-shell display shell canvas)
    (.setSize shell 700 700)
    (.open shell)

    (interval display change-image-delay
      (advance-slideshow display shell))

    (interval display frame-delay
      (animate-slides)
      (.redraw canvas))

    (swt-loop display shell canvas)))

(defn -main
  "Given a directory, perform a slide-show of all images contained within."
  [& args]
  (if-let [path (first args)]
    (do
      (load-images path)
      (begin))
    (println "Please specify a source directory.")))
