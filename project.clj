(defproject screenslide "0.1.0-SNAPSHOT"
  :description "Quick and dirty image slideshow"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/algo.generic "0.1.1"]
                 [clj-time "0.5.0"]
                 [quil "1.6.0"]
                 [org.eclipse.swt/org.eclipse.swt.cocoa.macosx.x86_64 "3.8"]]
  :repositories [["swt-repo" "https://swt-repo.googlecode.com/svn/repo/"]]
  :jvm-opts ["-XstartOnFirstThread" "-Xmx512M"]
  :main screenslide.core)
