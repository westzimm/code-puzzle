(ns code-puzzle.parse
  (:require [clojure.string :as str]
            [clojure.set :as set]))

(def num-columns [:order-id])
(def amount-columns [:unit-price-cents
                     :merchant-discount-cents
                     :staples-discount-cents
                     :unit-price-dollars
                     :merchant-discount-dollars
                     :staples-discount-dollars])

(defn ->kebab-keyword
  "Provided a string, returns a kebab-case keyword. Does not lowercase string."
  [string]
  (keyword (str/replace string #" " "-")))

(defn split-data [data separator]
  (map #(str/split % separator) data))

(defn parse-data
  "Creates a map from a vector. The 1st string becomes the ks & the rest vals."
  [unparsed-data]
  (let [headers (map ->kebab-keyword (first unparsed-data))
        data (rest unparsed-data)]
    (map #(zipmap headers %1) data)))

(defn conversion
  "For every map, execute the fn on the value of every key (or keys) provided."
  [data ks f]
  (mapv
   (fn [record]
     (reduce (fn [m k]
               (if (get m k)
                 (update-in m [k] f)
                 m))
             record
             ks))
   data))

(defn ->big-decimal [string]
  (bigdec string))

(defn ->integer [string]
  (Integer/valueOf string))

(defn parse-file
  "Takes a filename and a separator and returns it's data as santitized maps."
  [filename separator]
  (-> filename
      slurp
      str/lower-case
      str/split-lines
      (split-data separator)
      parse-data
      (conversion amount-columns ->big-decimal)
      (conversion num-columns ->integer)))

(defn cents->dollars [data cent-ks dollar-ks]
  (let [kmap (zipmap cent-ks dollar-ks)
        divide-by-100 (fn [m k] (update-in m [k] #(/ % 100.)))
        ->dollars (fn [record]
                    (set/rename-keys
                     (reduce
                      divide-by-100
                      record cent-ks) kmap))]
    (map ->dollars data)))

(defn parse-csv [filename separator cents-ks dollars-ks]
  (-> filename
      (parse-file separator)
      (cents->dollars cents-ks dollars-ks)))
