(ns bin-parser.fmt-compilers.parser
  (:require [bin-parser.format :as fmt]
            [bin-parser.intermediate-language :as il]
            [bin-parser.fmt-compilers.common :as cmn]))

(def parser
  (reify fmt/Format
    (init [_ args] {:current-grp [] :prog [] :bit-ix 0})

    (result [_] :prog)

    (bits [_ args]
      (let [{:keys [name n]} args]
        (fn [state]
          (-> state
              (update :prog conj `(il/reset-reg))
              (update :prog into (for [[buf-ix r-shifts mask l-shifts] (cmn/field-layout (:bit-ix state) n)]
                                   `(il/move-buf-reg ~buf-ix ~r-shifts ~mask ~l-shifts)))
              (update :prog conj `(il/store-reg :fld ~(conj (:current-grp state) name)))
              (update :bit-ix #(+ % n))))))

    (structure [this flds]
      (cmn/structure this flds))

    (group [this name flds]
      (cmn/group this name flds))
    ))


