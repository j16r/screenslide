(ns screenslide.core-test
  (:use clojure.test
        screenslide.core))

(deftest test-fit-to-viewport-small
  (testing "Supplied image is uniformly smaller than viewport"
    (is (= [100 100] (fit-to-viewport 10 10 100 100)))))

(deftest test-fit-to-viewport-short
  (testing "Supplied image is smaller than viewport with 2:1 aspect ratio"
    (is (= [200 100] (fit-to-viewport 100 50 100 100)))))

(deftest test-fit-to-viewport-skinny
  (testing "Supplied image is smaller than viewport with 2:1 aspect ratio"
    (is (= [100 200] (fit-to-viewport 50 100 100 100)))))


(deftest test-fit-to-viewport-large
  (testing "Supplied image is uniformly larger than viewport"
    (is (= [100 100] (fit-to-viewport 200 200 100 100)))))

(deftest test-fit-to-viewport-wide
  (testing "Supplied image is larger and wider than the viewport"
    (is (= [200 100] (fit-to-viewport 400 200 100 100)))))

(deftest test-fit-to-viewport-tall
  (testing "Supplied image is larger and taller than the viewport"
    (is (= [100 200] (fit-to-viewport 200 400 100 100)))))


(deftest test-fit-to-viewport-large-wide
  (testing "Supplied image is larger than viewport with smaller aspect ratio"
    (is (= [1440 9603/10] (fit-to-viewport 1600 1067 1440 900)))))


(deftest test-center-to-viewport
  (testing "Testing center to viewport")
    (is (= [5 5] (center-to-viewport 10 10 20 20))))
