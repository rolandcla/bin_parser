(ns bin-parser.stuff)

(defn big-endian-uint [buf beg-ix n]
  (let [end-ix (+ beg-ix n -1)
        end-b-ix (quot end-ix 8)]
    (loop [b-ix (quot beg-ix 8)
           v    (bit-and (nth buf b-ix)
                         (bit-shift-right 0xff (mod beg-ix 8)))]
      (if (== b-ix end-b-ix)
        (quot v (bit-shift-left 1 (- 7 (mod end-ix 8))))
        (recur (inc b-ix)
               (+' (*' 256 v) (nth buf (inc b-ix))))))))

(defn big-endian-bytes [v beg-ix n]
  (let [r-bits (mod (- 8 beg-ix n) 8)]
    (loop [v (*' v (bit-shift-left 1 r-bits))
           bytes '()
           n (+ n r-bits)]
      (cond (> n 0) (recur (quot v 256)
                           (conj bytes (mod v 256))
                           (- n 8))
            :else bytes))))

(defn bits [& args]
  (let [{:keys [name n]} args]
    (fn [action state]
      (case action
        :parse (-> state
                   (assoc-in (conj (:current-grp state) name)
                             (big-endian-uint (:buf state) (:buf-ix state) n))
                   (update-in [:buf-ix] #(+ % n)))
        :construct (let [v (name (:fld-values state))
                         buf-ix (:buf-ix state)
                         new-bytes (big-endian-bytes v buf-ix n)]
                     (-> state
                         (update-in [:buf]
                                    (fn [buf]
                                      (vec (if (zero? (mod buf-ix 8))
                                             (concat buf new-bytes)
                                             (concat (pop buf)
                                                     [(bit-or (peek buf) (first new-bytes))]
                                                     (rest new-bytes))))))
                         (update-in [:buf-ix] #(+ % n))))))))

(defn group [name & flds]
  (fn [action state]
    (case action
      :parse (let [saved-group (:current-grp state)]
               (-> (reduce (fn [state fld-fn] (fld-fn :parse state))
                           (update state :current-grp conj name)
                           flds)
                   (assoc :current-grp saved-group))))))

(defn format [& flds]
  (fn [action & args]
    (case action
      :parse (let [buf (first args)]
               (-> (reduce (fn [state fld-fn] (fld-fn :parse state))
                           {:buf buf :buf-ix 0 :current-grp [:result]}
                           flds)
                   :result))
      :construct (-> (reduce (fn [state fld-fn] (fld-fn :construct state))
                             {:fld-values (first args) :buf [] :buf-ix 0 :current-grp []}
                             flds)
                     :buf))))

(defn parse [fmt buf]
  (fmt :parse buf))

(defn construct [fmt fields]
  (fmt :construct fields))
