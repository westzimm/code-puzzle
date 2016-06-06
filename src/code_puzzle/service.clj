(ns code-puzzle.service
  (:require [cheshire.core :as json]
            [clojure.string :as str]
            [code-puzzle.report :as report]
            [io.pedestal.http :as bootstrap]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.route :as route :refer [router]]
            [io.pedestal.http.route.definition :refer [defroutes]]
            [ns-tracker.core :refer [ns-tracker]]
            [ring.util.response :as ring-resp]))

(defn get-report
  "Create a report based on request/param value"
  [req]
  (let [order-by (keyword (get-in req [:query-params :order-by]))]
    (bootstrap/json-response (report/create-report order-by))))

(defroutes routes
  [[["/" ^:interceptors [(body-params/body-params) bootstrap/html-body]
     ["/analytics/report"
      {:get get-report}]]]])

(def modified-namespaces
  (ns-tracker ["src" "test"]))

(def service {:env :prod
              ::bootstrap/interceptors [bootstrap/log-request
                                        bootstrap/not-found
                                        route/query-params
                                        (router
                                         (fn []
                                           (doseq [ns-sym (modified-namespaces)]
                                             (require ns-sym :reload))
                                           routes))]
              ::bootstrap/type :jetty
              ::bootstrap/port 8088})
