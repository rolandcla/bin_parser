(ns bin-parser.il-executors.interpreter
  (:require [bin-parser.intermediate-language :as il]
            [bin-parser.config :as config]))

(def pow-of-2
  (->> (iterate #(*' 2 %) 1)
       (take config/FIELD-MAX-LEN)
       vec))

(def interpreter
  (reify il/IL
    (init [_ [action arg1]]
      (case action
        :parse {:buf arg1}
        :construct {:fields arg1 :buf []}))

    (reset-reg [_]
      (fn [state] (assoc state :reg 0)))

    (move-buf-reg [_ buf-ix r-shifts mask l-shifts]
      (fn [state]
        (let [v (-> (get-in state [:buf buf-ix])
                    (bit-shift-right r-shifts)
                    (bit-and mask)
                    ((fn [x] (*' x (nth pow-of-2 l-shifts)))))]
          (update state :reg
                  (fn [reg] (+' reg v))))))

    (store-reg [_ {:keys [fld]}]
      (fn [state] (update state :fields assoc-in fld (:reg state))))

    (load-reg [_ {:keys [fld]}]
      (fn [state] (assoc state :reg (get-in (:fields state) fld))))

    (move-reg-buf [_ buf-ix r-shifts mask l-shifts]
      (fn [state]
        (let [b (-> (:reg state)
                    ((fn [x] (quot x (nth pow-of-2 l-shifts))))
                    (bit-and mask)
                    (bit-shift-left r-shifts)
                    )]
          (update-in state [:buf buf-ix]
                     (fn [v] (bit-or (or v 0) b))))))

    (result [_ [action]]
      (fn [state] (case action
                    :parse (:fields state)
                    :construct (:buf state))))
    ))
