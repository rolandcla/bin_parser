(ns bin-parser.fmt-compilers.common)

(defn field-layout [buf-ix n]
  (loop [moves []
         buf-ix buf-ix
         n n]
    (if (> n 0)
      (let [m (min n (- 8 (mod buf-ix 8)))]
        (recur (conj moves [buf-ix m (- n m)])
               (+ buf-ix m)
               (- n m)))
      moves)))
