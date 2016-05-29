(defproject code-puzzle "0.1.0-SNAPSHOT"
  :description "staples-sparx/Code-Puzzle"
  :url "https://github.com/staples-sparx/Code-Puzzle/blob/master/README.md"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [io.pedestal/pedestal.jetty "0.4.1"]
                 [io.pedestal/pedestal.service "0.4.1"]
                 [io.pedestal/pedestal.service-tools "0.4.1"]
                 [ns-tracker "0.3.0"]]
  :main code-puzzle.server)
