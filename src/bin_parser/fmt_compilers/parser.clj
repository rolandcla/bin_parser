(ns bin-parser.fmt-compilers.parser
  (:require [bin-parser.format :as fmt]
            [bin-parser.intermediate-language :as il]))

(def parser-compiler
  (reify fmt/Format
    (init [_ args] {:current-grp [] :prog [] :buf-ix 0})

    (result [_] :prog)

    (bits [_ args]
      (let [{:keys [name n]} args]
        (fn [state]
          (-> state
              (update :prog
                      (fn [prog]
                        (loop [prog (conj prog `(il/reset-reg))
                               buf-ix (:buf-ix state)
                               n n]
                          (if (> n 0)
                            (let [m (min n (- 8 (mod buf-ix 8)))]
                              (recur (conj prog `(il/move-buf-reg ~buf-ix ~m ~(- n m)))
                                     (+ buf-ix m)
                                     (- n m)))
                            (conj prog `(il/store-reg :fld ~(conj (:current-grp state) name)))))))
              (update :buf-ix #(+ % n))))))

    (structure [this flds]
      (fn [state] (reduce (fn [st fld] ((fld this) st)) state flds)))
    ))


