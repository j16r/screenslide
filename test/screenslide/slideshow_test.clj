(ns screenslide.slideshow-test
  (:use clojure.test
        screenslide.slideshow))

(def images [:first-image :second-image :last-image])
(def screen {:width 100 height: 100})

(deftest images-at-frame-0
  (testing "Testing the set of images to draw at frame 0"
    (is (= [{:image :first-image
             :width 0
             :height
             :alpha 0}]
           (frame 0)))))

(deftest images-at-frame-25
  (testing "Testing the set of images to draw after the transition has finished"
    (is (= [{:image :first-image
             :width 0
             :height
             :alpha 255}]
           (frame 25)))))

(deftest images-at-frame-50
  (testing "Testing the set of images to draw after the transition has finished"
    (is (= [{:image :first-image
             :width 0
             :height
             :alpha 255}
            {:image :second-image
             :width 0
             :height 0
             :alpha 0}]
           (frame 50)))))

(deftest images-at-frame-75
  (testing "Testing the set of images to draw after the transition has finished"
    (is (= [{:image :first-image
             :width 0
             :height
             :alpha 0}
            {:image :second-image
             :width 0
             :height 0
             :alpha 255}]
           (frame 50)))))
