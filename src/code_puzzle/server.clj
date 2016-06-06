(ns code-puzzle.server
  (:require [code-puzzle.service :as service]
            [io.pedestal.http :as bootstrap]))

(defn -main [& args]
  (-> service/service
      bootstrap/create-server
      bootstrap/start))
