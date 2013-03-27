(ns screenslide.core
  (:use quil.core)
  (:gen-class))

(defn present? [val] (not (nil? val)))

(defn- wildcard-filter
  "Given a regex, return a FilenameFilter that matches."
  [regex]
  (reify java.io.FilenameFilter
    (accept [_ dir name] (present? (re-find regex name)))))

(defn- directory-list
  "Given a directory and a regex, return a seq of matching filenames."
  [dir regex]
  (map
    #(str dir "/" %)
    (sort
      (.list (clojure.java.io/file dir) (wildcard-filter regex)))))

(def regular-file-regex #"[^.]+[.][^.]+")

(defn load-files [path]
  (directory-list path regular-file-regex))

(defn setup []
  (frame-rate 1)
  (background 200))

(def images (ref ()))

(defn draw []
  (println (count @images))
  (let [image-to-show (first @images)]
    (if (present? image-to-show)
      (do
        (image (load-image image-to-show) 0 0)
        (dosync (alter images rest))))))

(defsketch screenslide 
  :title "Screenslide"
  :setup setup
  :draw draw
  :size [1920 1080])

(defn -main
  "Given a directory, perform a full screen slide-show of all images contained within."
  [& args]
  (dosync 
    (ref-set images (load-files (first args)))))
