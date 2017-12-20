(ns bin-parser.intermediate-language)

(def masks {1 1, 2 3, 3 7, 4 15, 5 31, 6 63, 7 127, 8 255})

(defprotocol IL
  (init [_ args])
  (reset-reg [_])
  (move-buf-reg [_ buf-ix n shifts])
  (store-reg [_ args])
  (load-reg [_ args])
  (move-reg-buf [_ buf-ix n shifts])
  (result [_ args])
  )

(def interpreter
  (reify IL
    (init [_ [action arg1]]
      (case action
        :parse {:buf arg1}
        :construct {:fields arg1 :buf []}))

    (reset-reg [_]
      (fn [state] (assoc state :reg 0)))

    (move-buf-reg [_ buf-ix n shifts]
      (fn [state]
        #_(println (:buf state) buf-ix (quot buf-ix 8))
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

;; IL instructions
;;---------------------------------------------------------------------------------------
(defn reset-reg []
  (fn [executor] (reset-reg executor)))

(defn move-buf-reg [buf-ix n shifts]
  (fn [executor] (move-buf-reg executor buf-ix n shifts)))

(defn store-reg [& args]
  (fn [executor] (store-reg executor args)))

(defn load-reg [& args]
  (fn [executor] (load-reg executor args)))

(defn move-reg-buf [buf-ix n shifts]
  (fn [executor] (move-reg-buf executor buf-ix n shifts)))
;;---------------------------------------------------------------------------------------

(defn exec-prog [executor prog & args]
  (->> (reduce (fn [state f] (println state) ((f executor) state))
              (init executor args)
              prog)
       ((result executor args))))

(def parse-ex1
  [(reset-reg)
   (move-buf-reg 0 5 0)
   (store-reg :fld [:aa])
   (reset-reg)
   (move-buf-reg 5 3 0)
   (store-reg :fld [:bb])
   ])

(def construct-ex1
  [(load-reg :fld [:aa])
   (move-reg-buf 0 5 0)
   (load-reg :fld [:bb])
   (move-reg-buf 5 3 0)
   ])




