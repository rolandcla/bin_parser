(ns bin-parser.fmt-compilers.common)

(def masks [0 1 3 7 15 31 63 127 255])

(defn field-layout [bit-ix n]
  (loop [moves []
         bit-ix bit-ix
         n n]
    (if (> n 0)
      (let [m        (min n (- 8 (mod bit-ix 8)))
            buf-ix   (quot bit-ix 8)
            r-shifts (- 8 m (mod bit-ix 8))
            mask     (nth masks m)
            l-shifts (- n m)]
        (recur (conj moves [buf-ix r-shifts mask l-shifts])
               (+ bit-ix m)
               (- n m)))
      moves)))

(defn structure [compiler flds]
  (fn [state] (reduce (fn [st fld] ((fld compiler) st)) state flds)))

(defn group [compiler name flds]
  (fn [state]
    (let [saved-grp (:current-grp state)]
      (-> state
          (assoc :current-grp (conj saved-grp name))
          ((structure compiler flds))
          (assoc :current-grp saved-grp)))))

