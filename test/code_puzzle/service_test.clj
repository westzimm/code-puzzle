(ns code-puzzle.service-test
  (:require [clojure.test :refer :all]
            [code-puzzle.service :as service]
            [io.pedestal.http :as bootstrap]
            [io.pedestal.test :refer :all]))

(def service-dev
  (::bootstrap/service-fn (bootstrap/create-servlet service/service)))

(deftest analytics-report-endpoint-test
  (testing "response status and content-type for analytics/report"
    (let [resp (response-for service-dev :get "/analytics/report")
          content-type (get-in resp [:headers "Content-Type"])]
      (is (= 200 (:status resp)))
      (is (= "application/json;charset=UTF-8" content-type)))))
