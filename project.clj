(defproject advent-of-code "0.1.0"
  :description "Basis/template for Advent of Code"
  :url "https://github.com/rjray/advent-clojure-basis"
  :license {:name "EPL-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.0"]
                 [org.clojure/math.numeric-tower "0.0.4"]
                 [org.clojure/math.combinatorics "0.1.6"]]
  :plugins [[lein-kibit "0.1.6"]]
  :main advent-of-code.core
  :repl-options {:init-ns advent-of-code.core})
