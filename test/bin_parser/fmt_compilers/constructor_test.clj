(ns bin-parser.fmt-compilers.constructor-test
  (:require [bin-parser.fmt-compilers.constructor :refer [constructor]]
            [bin-parser.format :as fmt]
            [bin-parser.intermediate-language :as il]
            [clojure.test :refer :all]))

(def fmt-ex1
  (fmt/structure
   (fmt/bits :name :aa :n 5)
   (fmt/bits :name :bb :n 3)))

(deftest fmt-ex1-test
  (testing "Construct 8 bits"
    (is (= (fmt/compile constructor fmt-ex1)
           `[(il/load-reg :fld [:aa])
             (il/move-reg-buf 0 3 31 0)
             (il/load-reg :fld [:bb])
             (il/move-reg-buf 0 0 7 0)]))))


