(defproject advent-of-code "0.1.0"
  :description "Advent of Code 2023 Solutions"
  :url "https://github.com/rjray/advent-2023-clojure"
  :license {:name "MIT"
            :url "https://opensource.org/license/mit/"}
  :dependencies [[org.clojure/clojure "1.11.0"]
                 [org.clojure/math.numeric-tower "0.0.4"]
                 [org.clojure/math.combinatorics "0.1.6"]
                 [org.clojure/core.match "1.0.1"]
                 [org.clojure/data.priority-map "1.1.0"]]
  :plugins [[lein-kibit "0.1.6"]]
  :main advent-of-code.core
  :repl-options {:init-ns advent-of-code.core})
