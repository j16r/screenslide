(ns screenslide.core
  (:use quil.core)
  (:import (org.eclipse.swt.widgets Display Shell Canvas Listener)
           (org.eclipse.swt.graphics Image GC)
           (org.eclipse.swt.layout FillLayout)
           (org.eclipse.swt.events ShellAdapter))
  (:gen-class))

(defn present? [val] (not (nil? val)))

(def images (ref nil))
(def current-image (ref nil))

(defn load-images [path]
  (shuffle
    (filter #(re-find #"^[^.]+\.(jpg|jpeg)$" %)
      (map #(.getPath %) (file-seq (clojure.java.io/file path))))))

(defn next-image []
  (let [image (first @images)]
    (dosync (commute images rest))
    image))

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
            (.drawImage (.gc event) image 0 0)))))))

(defn swt-loop [display shell canvas]
  (loop []
    (if (.isDisposed shell)
      (.dispose display)
      (do
        (if (not (.readAndDispatch display))
          (.sleep display))
        (recur)))))

(defmacro interval [display timeout & body]
  `(.timerExec ~display ~timeout
    (proxy [Runnable][]
      (run []
        (do
          ~@body
          (.timerExec ~display ~timeout ~'this))))))

(defn begin []
  (let [display (Display.)
        shell (Shell. display)
        canvas (Canvas. shell org.eclipse.swt.SWT/NO_BACKGROUND)]
    (create-shell display shell canvas)
    (.setSize shell 700 700)
    (.open shell)

    (interval display 3000
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
  (dosync (ref-set images (load-images (first args)))
  (begin)))
