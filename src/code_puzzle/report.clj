(ns code-puzzle.report
  (:require [clojure.string :as str]
            [code-puzzle.parse :as p]))

(def csv "resources/staples-data.csv")
(def psv "resources/external-data.psv")

;; separators
(def csv-sep #",")
(def psv-sep #"\|")

(def session-type-desc [:session-type])
(def order-id-asc [:order-id])
(def unit-price-dollars-asc [:unit-price-dollars])

(def dollars-ks [:unit-price-dollars
                 :merchant-discount-dollars
                 :staples-discount-dollars])
(def cents-ks [:unit-price-cents
               :merchant-discount-cents
               :staples-discount-cents])

(def desc #(compare %2 %1))
(def asc compare)

(defn summary
  "Returns a single map where the values are the sum of all the values with the
  same keys."
  [data ks]
  (let [key-total (fn [k]
                    (reduce + (map k data)))
        key-totals (map key-total ks)]
    (zipmap ks key-totals)))

(defn grouped-summaries [csv psv cents-ks dollars-ks]
  {:staples-data (summary
                  (p/parse-csv csv csv-sep cents-ks dollars-ks)
                  dollars-ks)
   :merchant-data (summary
                   (p/parse-file psv psv-sep) dollars-ks)})

(defn sorted-orders [csv psv order-key order-by]
  (map (fn [s m] {:staples-data s :merchant-data m})
       (sort-by order-key order-by (p/parse-csv csv csv-sep cents-ks
                                                dollars-ks))
       (sort-by order-key order-by (p/parse-file psv psv-sep))))

(defmulti create-report identity)

(defmethod create-report :session-type-desc [_]
  {:summaries (grouped-summaries csv psv cents-ks dollars-ks)
   :orders (sorted-orders csv psv :session-type desc)})

(defmethod create-report :order-id-asc [_]
  {:summaries (grouped-summaries csv psv cents-ks dollars-ks)
   :orders (sorted-orders csv psv :order-id asc)})

(defmethod create-report :unit-price-dollars-asc [_]
  {:summaries (grouped-summaries csv psv cents-ks dollars-ks)
   :orders (sorted-orders csv psv :unit-price-dollars asc)})

(defmethod create-report :default [_]
  {:summaries (grouped-summaries csv psv cents-ks dollars-ks)})
