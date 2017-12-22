(ns bin-parser.intermediate-language)

(defprotocol IL
  (init [_ args])
  (reset-reg [_])
  (move-buf-reg [_ buf-ix r-shifts mask l-shifts])
  (store-reg [_ args])
  (load-reg [_ args])
  (move-reg-buf [_ buf-ix r-shifts mask l-shifts])
  (result [_ args])
  )

(defn reset-reg []
  (fn [executor] (reset-reg executor)))

(defn move-buf-reg [buf-ix r-shifts mask l-shifts]
  (fn [executor] (move-buf-reg executor buf-ix r-shifts mask l-shifts)))

(defn store-reg [& args]
  (fn [executor] (store-reg executor args)))

(defn load-reg [& args]
  (fn [executor] (load-reg executor args)))

(defn move-reg-buf [buf-ix r-shifts mask l-shifts]
  (fn [executor] (move-reg-buf executor buf-ix r-shifts mask l-shifts)))

(defn execute [executor prog & args]
  (->> (reduce (fn [state f] ((f executor) state))
              (init executor args)
              prog)
       ((result executor args))))

;;-------------------------------------------------------------------------

(def parse-ex1
  [(reset-reg)
   (move-buf-reg 0 3 0x1f 0)
   (store-reg :fld [:aa])
   (reset-reg)
   (move-buf-reg 0 0 0x07 0)
   (store-reg :fld [:bb])
   ])

(def construct-ex1
  [(load-reg :fld [:aa])
   (move-reg-buf 0 3 0x1f 0)
   (load-reg :fld [:bb])
   (move-reg-buf 0 0 0x07 0)
   ])




