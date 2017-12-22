(ns bin-parser.stuff
  (:require [bin-parser.format :as fmt]
            [bin-parser.fmt-compilers.parser :refer [parser]]
            [bin-parser.fmt-compilers.constructor :refer [constructor]]
            [bin-parser.intermediate-language :as il]
            [bin-parser.il-executors.interpreter :refer [interpreter]]))

(def fmt-ex1
  (fmt/structure
   (fmt/bits :name :aa :n 5)
   (fmt/bits :name :bb :n 3)))

(defn parse [fmt buf]
  (let [il-src (fmt/compile parser fmt)
        prog   (map eval il-src)]
    (il/execute interpreter prog :parse buf)))

(defn construct [fmt fields]
  (let [il-src (fmt/compile constructor fmt)
        prog   (map eval il-src)]
    (il/execute interpreter prog :construct fields)))

(def fmt-ex2
  (fmt/structure
   (fmt/group :g1 (fmt/bits :name :aa :n 5))
   (fmt/bits :name :bb :n 3)))

