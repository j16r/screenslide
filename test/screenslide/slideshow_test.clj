(ns screenslide.slideshow-test
  (:use clojure.test
        screenslide.slideshow))

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
