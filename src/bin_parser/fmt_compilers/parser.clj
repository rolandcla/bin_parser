(ns bin-parser.fmt-compilers.parser
  (:require [bin-parser.format :as fmt]
            [bin-parser.intermediate-language :as il]
            [bin-parser.fmt-compilers.common :refer [field-layout]]))

(def parser
  (reify fmt/Format
    (init [_ args] {:current-grp [] :prog [] :buf-ix 0})

    (result [_] :prog)

    (bits [_ args]
      (let [{:keys [name n]} args]
        (fn [state]
          (-> state
              (update :prog conj `(il/reset-reg))
              (update :prog into (for [[buf-ix n shifts] (field-layout (:buf-ix state) n)]
                                   `(il/move-buf-reg ~buf-ix ~n ~shifts)))
              (update :prog conj `(il/store-reg :fld ~(conj (:current-grp state) name)))
              (update :buf-ix #(+ % n))))))

    (structure [this flds]
      (fn [state] (reduce (fn [st fld] ((fld this) st)) state flds)))

    (group [this name flds]
      (fn [state]
        (let [saved-grp (:current-grp state)]
          (-> state
              (assoc :current-grp (if name (conj saved-grp name) saved-grp))
              ((fn [sta] (reduce (fn [st fld] ((fld this) st)) sta flds)))
              (assoc :current-grp saved-grp)))))
    ))


