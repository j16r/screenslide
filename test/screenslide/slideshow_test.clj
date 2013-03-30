(ns screenslide.slideshow-test
  (:use clojure.test
        screenslide.slideshow))

(def images [:first-image :second-image :last-image])

(deftest test-keyframe-first-frame
  (testing "Testing first keyframe includes first image"
    (is (= [:first-image :second-image] (keyframe images 0)))))

(deftest test-keyframe-second-frame
  (testing "Testing second keyframe includes first and second image"
    (is (= [:second-image :last-image] (keyframe images 25)))))

(deftest test-keyframe-third-frame
  (testing "Testing third keyframe includes second and finale image"
    (is (= [:last-image] (keyframe images 50)))))
