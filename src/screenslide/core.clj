(ns screenslide.core
  (:use seesaw.core)
  (:gen-class))

(defn present? [val] (not (nil? val)))

(def f (frame :title "Screenslide"))
(defn takeover-screen []
  (native!)
  (-> f pack! show!)
  (config f :title))

(defn- wildcard-filter
  "Given a regex, return a FilenameFilter that matches."
  [regex]
  (reify java.io.FilenameFilter
    (accept [_ dir name] (present? (re-find regex name)))))

(defn- directory-list
  "Given a directory and a regex, return a sorted seq of matching filenames."
  [dir regex]
  (sort (.list (clojure.java.io/file dir) (wildcard-filter regex))))

(def regular-file-regex #"[^.]+[.][^.]+")

(defn load-files [path]
  (directory-list path regular-file-regex))

(defn -main
  "Given a directory, perform a full screen slide-show of all images contained within."
  [& args]
  (println (load-files (first args)))
  (takeover-screen))
