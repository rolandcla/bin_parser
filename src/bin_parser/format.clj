(ns bin-parser.format
  (:refer-clojure :exclude [compile]))

(defprotocol Format
  (init [_ args])
  (result [_])
  (bits [_ args])
  (structure [_ args]))


(defn bits [& args]
  (fn [compiler] (bits compiler args)))

(defn structure [& flds]
  (fn [compiler] (structure compiler flds)))

(defn compile [compiler format & args]
  (->> ((format compiler) (init compiler args))
       ((result compiler))))
