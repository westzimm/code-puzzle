(ns code-puzzle.service-test
  (:require [clojure.test :refer :all]
            [code-puzzle.service :as service]
            [io.pedestal.http :as bootstrap]
            [io.pedestal.test :refer :all]))

(def service-dev
  (::bootstrap/service-fn (bootstrap/create-servlet service/service)))

(deftest hello-girl-endpoint
  (testing "hello-girl response status and content-type"
    (let [resp (response-for service-dev :get "/hello-girl")
          content-type (get-in resp [:headers "Content-Type"])]
      (is (= 200 (:status resp)))
      (is (= "text/html;charset=UTF-8" content-type)))))
