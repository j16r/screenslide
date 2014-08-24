(ns screenslide.core
  (:use screenslide.util
        screenslide.slideshow
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
    (.setFullScreen true)

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
          (draw-slideshow (.gc event) display shell))))))

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
    (.open shell)

    (interval display redraw-interval
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
