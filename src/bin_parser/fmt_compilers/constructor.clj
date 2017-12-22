(ns bin-parser.fmt-compilers.constructor
  (:require [bin-parser.format :as fmt]
            [bin-parser.intermediate-language :as il]
            [bin-parser.fmt-compilers.common :as cmn]))

(def constructor
  (reify fmt/Format
    (init [_ args] {:current-grp [] :prog [] :buf-ix 0})

    (result [_] :prog)

    (bits [_ args]
      (let [{:keys [name n]} args]
        (fn [state]
          (-> state
              (update :prog conj `(il/load-reg :fld ~(conj (:current-grp state) name)))
              (update :prog into (for [[buf-ix n shifts] (cmn/field-layout (:buf-ix state) n)]
                                   `(il/move-reg-buf ~buf-ix ~n ~shifts)))
              (update :buf-ix #(+ % n))))))

    (structure [this flds]
      (cmn/structure this flds))

    (group [this name flds]
      (cmn/group this name flds))
    ))


