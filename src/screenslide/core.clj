(ns screenslide.core
  (:use seesaw.core)
  (:gen-class))

(def f (frame :title "Screenslide"))
(defn takeover-screen []
  (native!)
  (-> f pack! show!)
  (config f :title))

(defn -main
  "Given a directory, perform a full screen slide-show of all images contained within."
  [& args]
  (takeover-screen))
