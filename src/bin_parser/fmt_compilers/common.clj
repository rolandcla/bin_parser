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

(defn structure [compiler flds]
  (fn [state] (reduce (fn [st fld] ((fld compiler) st)) state flds)))

(defn group [compiler name flds]
  (fn [state]
    (let [saved-grp (:current-grp state)]
      (-> state
          (assoc :current-grp (conj saved-grp name))
          ((structure compiler flds))
          (assoc :current-grp saved-grp)))))

#_(defn group [compiler name flds]
  (fn [state]
    (let [saved-grp (:current-grp state)]
      (-> state
          (assoc :current-grp (if name (conj saved-grp name) saved-grp))
          ((structure compiler flds))
          (assoc :current-grp saved-grp)))))
