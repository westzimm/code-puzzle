(ns code-puzzle.report-test
  (:require [clojure.test :refer :all]
            [code-puzzle.parse :as p]
            [code-puzzle.report :refer :all]))

(def test-csv "resources/test.csv")
(def test-psv "resources/test.psv")

(def cent-cols [:price-cents :discount-cents])
(def dollar-cols [:price-dollars :discount-dollars])

(def test-amt-cols (concat cent-cols dollar-cols))
(def test-num-cols [:order-id])

(deftest reports-test
  (testing "summaries fn returns the sum of the values for matching keys"
    (with-redefs [p/amount-columns test-amt-cols
                  p/num-columns test-num-cols]
      (let [parsed-csv (p/parse-csv test-csv #"," cent-cols dollar-cols)
            parsed-psv (p/parse-file test-psv #"\|")
            csv-summary (summary parsed-psv dollar-cols)
            psv-summary (summary parsed-csv dollar-cols)]
        (is (= 65.3M (:discount-dollars csv-summary)))
        (is (= 360.5M (:price-dollars psv-summary)))))))
