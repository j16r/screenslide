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

(deftest test-slide-alpha
  (testing "Testing slide alpha tapers off towards end"
    (let [slide-duration 1000]
      (is (= 20 (slide-alpha 0 slide-duration)))
      (is (= 216 (slide-alpha 100 slide-duration)))
      (is (= 255 (slide-alpha 200 slide-duration)))
      (is (= 255 (slide-alpha 500 slide-duration)))
      (is (= 255 (slide-alpha 700 slide-duration)))
      (is (= 216 (slide-alpha 900 slide-duration)))
      (is (= 20 (slide-alpha 1000 slide-duration))))))
