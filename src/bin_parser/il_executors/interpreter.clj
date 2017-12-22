(ns bin-parser.il-executors.interpreter
  (:require [bin-parser.intermediate-language :as il]))

(def masks {1 1, 2 3, 3 7, 4 15, 5 31, 6 63, 7 127, 8 255})

(def interpreter
  (reify il/IL
    (init [_ [action arg1]]
      (case action
        :parse {:buf arg1}
        :construct {:fields arg1 :buf []}))

    (reset-reg [_]
      (fn [state] (assoc state :reg 0)))

    (move-buf-reg [_ buf-ix n shifts]
      (fn [state]
        (let [v (-> (get-in state [:buf (quot buf-ix 8)])
                    (bit-shift-right (- 8 n (mod buf-ix 8)))
                    (bit-and (masks n))
                    ((fn [x] (*' x (bit-shift-left 1 shifts)))))]
          (update state :reg
                  (fn [reg] (+' reg v))))))

    (store-reg [_ {:keys [fld]}]
      (fn [state] (update state :fields assoc-in fld (:reg state))))

    (load-reg [_ {:keys [fld]}]
      (fn [state] (assoc state :reg (get-in (:fields state) fld))))

    (move-reg-buf [_ buf-ix n shifts]
      (fn [state]
        (let [b (-> (:reg state)
                    ((fn [x] (quot x (bit-shift-left 1 shifts))))
                    (bit-and (masks n))
                    (bit-shift-left (- 8 n (mod buf-ix 8)))
                    )]
          (update-in state [:buf (quot buf-ix 8)]
                     (fn [v] (bit-or (or v 0) b))))))

    (result [_ [action]]
      (fn [state] (case action
                    :parse (:fields state)
                    :construct (:buf state))))
    ))
