(ns code-puzzle.parse-test
  (:require [clojure.string :as str]
            [clojure.test :refer :all]
            [code-puzzle.parse :refer :all]
            [code-puzzle.service :refer :all]))

(def test-csv "resources/test.csv")
(def test-psv "resources/test.psv")

(def test-amt-col [:price-cents :discount-cents :price-dollarss
                   :discount-dollars])
(def test-num-cols [:order-id])

(deftest kebab-keyword-test
  (testing "->kebab-keyword fn converts a string to a kebab-case keyword"
    (let [unparsed-string "lemons lemurs lions"
          converted-string :lemons-lemurs-lions]
      (is (= (->kebab-keyword unparsed-string) converted-string)))))

(deftest parse-csv-test
  (testing "split-data separator"
    (with-redefs [amount-columns test-amt-col
                  num-columns test-num-cols]
      (let [csv->data-interim (-> test-csv
                                  slurp
                                  str/lower-case
                                  str/split-lines
                                  (split-data #",")) ; not testing these clj fns
            parsed-data {:order-id "21133005",
                         :price-cents "11050",
                         :discount-cents "1000",
                         :session-type "control"}
            parsed-file (parse-file test-csv #",")
            converted-to-dollars (cents->dollars
                                  parsed-file
                                  [:price-cents :discount-cents]
                                  [:price-dollars :discount-dollars])]
        (is (= (first (parse-data csv->data-interim)) parsed-data))
        (testing "conversion fn returns the expected class for the values"
          (instance? String (:session-type (first parsed-file)))
          (instance? Integer (:order-id (first parsed-file)))
          (instance? BigDecimal (:price-cents (first parsed-file))))
        (testing "cents keys and values are converted to dollars"
          (is (= 110.5M ((first converted-to-dollars) :price-dollars))))
        (testing "data no longer contains cents keys"
          (is (false? (.contains [:price-cents :discount-cents]
                                 (first converted-to-dollars)))))))))
(deftest parse-psv-test
  (testing "parse-file function can handle psv file as input"
    (with-redefs [amount-columns test-amt-col
                  num-columns test-num-cols]
      (let [psv->data-interim (-> test-psv
                                  slurp
                                  str/lower-case
                                  str/split-lines
                                  (split-data #"\|"))
            parsed-data {:order-id "21133005",
                         :price-dollars "110.0",
                         :discount-dollars "20.5",
                         :session-type "control"}
            parsed-file (parse-file test-psv #"\|")]
        (is (= (first (parse-data psv->data-interim)) parsed-data))
        (testing "conversion fn returns the expected class for the values"
          (instance? String (:session-type (first parsed-file)))
          (instance? Integer (:order-id (first parsed-file)))
          (instance? BigDecimal (:price-cents (first parsed-file))))))))
