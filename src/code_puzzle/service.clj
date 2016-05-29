(ns code-puzzle.service
  (:require [io.pedestal.http :as bootstrap]
            [io.pedestal.http.route :as route :refer [router]]
            [io.pedestal.http.route.definition :refer [defroutes]]
            [ns-tracker.core :refer [ns-tracker]]
            [ring.util.response :as ring-resp]))

(defn hello-girl
  "server is up / home page check"
  [req]
  (ring-resp/response "hello girl"))

(defroutes routes
  [[["/" ^:interceptors [bootstrap/html-body]
     ["/hello-girl"
      {:get hello-girl}]]]])

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
