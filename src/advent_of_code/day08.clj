(ns advent-of-code.day08
  (:require [advent-of-code.utils :as u]
            [clojure.math.numeric-tower :refer [lcm]]))

;; Convert the input to the graph structure. We've used u/to-blocks to split
;; the instructions line from the graph itself. The returned map has a key for
;; the instructions (a list of single-character strings converted to keywords)
;; and one for the graph itself. The graph is directed, and while each node has
;; only two outgoing edges, it may have any number of incoming ones.
(defn- to-graph [[insts lines]]
  {:insts (map keyword (map str (seq insts)))
   :graph (reduce (fn [g [node left right]]
                    (assoc g node {:L left, :R right}))
                  ;; The regexp here includes numbers for the test data
                  {} (map #(re-seq #"[A-Z0-9]{3}" %) (u/to-lines lines)))})

;; Walk the graph given the sequence of instructions. Uses `cycle` to get an
;; infinite stream of instructions.
(defn- walk [{:keys [insts graph]}]
  (loop [node "AAA", [step & steps] (cycle insts), path ()]
    (if (= node "ZZZ")
      path
      (recur (get-in graph [node step]) steps (cons step path)))))

(defn part-1
  "Day 08 Part 1"
  [input]
  (->> input
       u/to-blocks
       to-graph
       walk
       count))

;; This is a modified `walk` that looks for the walk to end in any of a set of
;; nodes.
(defn- walk' [insts graph start ends]
  (loop [node start, [step & steps] (cycle insts), path ()]
    (if (ends node)
      (count path)
      (recur (get-in graph [node step]) steps (cons step path)))))

;; Walk the "ghost" paths: run each of the matching start-nodes until it hits
;; end end-node. Take the collected path lengths and compute their LCM (using
;; the `clojure.math.numeric-tower` package).
(defn- ghost-walk [{:keys [insts graph]}]
  (let [avec        (filter #(= \A (last %)) (keys graph))
        zset        (set (filter #(= \Z (last %)) (keys graph)))]
    (reduce lcm (map #(walk' insts graph % zset) avec))))

(defn part-2
  "Day 08 Part 2"
  [input]
  (->> input
       u/to-blocks
       to-graph
       ghost-walk))
