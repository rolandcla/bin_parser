(ns bin-parser.fmt-compilers.parser-test
  (:require [bin-parser.fmt-compilers.parser :refer [parser-compiler]]
            [bin-parser.format :as fmt]
            [bin-parser.intermediate-language :as il]
            [clojure.test :refer :all]))


(def fmt-ex1
  (fmt/structure
   (fmt/bits :name :aa :n 5)
   (fmt/bits :name :bb :n 3)))

(deftest fmt-ex1-test
  (testing "Parse 8 bits"
    (is (= (fmt/compile parser-compiler fmt-ex1)
           `[(il/reset-reg)
             (il/move-buf-reg 0 5 0)
             (il/store-reg :fld [:aa])
             (il/reset-reg)
             (il/move-buf-reg 5 3 0)
             (il/store-reg :fld [:bb])]))))
