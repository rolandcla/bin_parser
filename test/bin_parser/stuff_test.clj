(ns bin-parser.stuff-test
  (:require [bin-parser.stuff :as bp]
            [clojure.test :refer :all]))

(deftest fmt-ex1-test
  (testing "Parse 8 bits"
    (is (= (bp/parse bp/fmt-ex1 [255])
           {:aa 31 :bb 7}))
    (is (= (bp/parse bp/fmt-ex1 [0])
           {:aa 0 :bb 0}))
    (is (= (bp/parse bp/fmt-ex1 [42])
           {:aa 5 :bb 2}))
    (is (= (bp/parse bp/fmt-ex1 [0x55])
           {:aa 10 :bb 5}))))
