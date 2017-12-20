(ns bin-parser.stuff-test
  (:require [bin-parser.stuff :as bp]
            [clojure.test :refer :all]))

(def fmt1
  (bp/format
   (bp/bits :name :aa :n 5)
   (bp/bits :name :bb :n 3)))

(deftest fmt1-test
  (testing "Parse 8 bits"
    (is (= (bp/parse fmt1 (.getBytes "a")) {:aa 12 :bb 1}))
    (is (= (bp/parse fmt1 [255]) {:aa 31 :bb 7})))
  (testing "Construct 8 bits"
    (is (= (bp/construct fmt1 {:aa 31 :bb 7}) [255]))))

(def fmt2
  (bp/format
   (bp/bits :name :aa :n 5)
   (bp/bits :name :bb :n 13)
   (bp/bits :name :cc :n 2)
   (bp/bits :name :dd :n 12)))

(deftest fmt2-test
  (testing "Parse 32 bits"
    (is (= (bp/parse fmt2 [255 255 255 255]) {:aa 31 :bb 8191 :cc 3 :dd 4095}))
    (is (= (bp/parse fmt2 [128 129 255 0]) {:aa 16 :bb 519 :cc 3 :dd 3840})))
  (testing "Construct 8 bits"
    (is (= (bp/construct fmt2 {:aa 31 :bb 8191 :cc 3 :dd 4095}) [255 255 255 255]))
    (is (= (bp/construct fmt2 {:aa 16 :bb 519 :cc 3 :dd 3840}) [128 129 255 0])))
  )

(def fmt3
  (bp/format
   (bp/group :gg
          (bp/bits :name :aa :n 5)
          (bp/bits :name :bb :n 13))
   (bp/bits :name :cc :n 2)
   (bp/bits :name :dd :n 12)))

(deftest fmt3-test
  (testing "Parse 32 bits with group"
    (is (= (bp/parse fmt3 [255 255 255 255]) {:gg {:aa 31 :bb 8191} :cc 3 :dd 4095}))
    (is (= (bp/parse fmt3 [128 129 255 0]) {:gg {:aa 16 :bb 519} :cc 3 :dd 3840}))))


