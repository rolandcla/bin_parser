(ns bin-parser.format
  (:refer-clojure :exclude [compile]))

(defprotocol Format
  (init [_ args])
  (result [_])
  (bits [_ args])
  (structure [_ args])
  (group [_ name flds]))


(defn bits [& args]
  (fn [compiler] (bits compiler args)))

(defn structure [& flds]
  (fn [compiler] (structure compiler flds)))

(defn group [name & flds]
  (fn [compiler] (group compiler name flds)))

(defn compile [compiler format & args]
  (->> ((format compiler) (init compiler args))
       ((result compiler))))
