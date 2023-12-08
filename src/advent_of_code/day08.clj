(ns advent-of-code.day08
  (:require [advent-of-code.utils :as u]
            [clojure.math.numeric-tower :refer [lcm]]))

(defn- to-graph [[insts lines]]
  {:insts (map keyword (map str (seq insts)))
   :graph (reduce (fn [g [node left right]]
                    (assoc g node {:L left, :R right}))
                  {} (map #(re-seq #"[A-Z0-9]{3}" %) (u/to-lines lines)))})

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

(defn- walk' [insts graph start ends]
  (loop [node start, [step & steps] (cycle insts), path ()]
    (if (ends node)
      (count path)
      (recur (get-in graph [node step]) steps (cons step path)))))

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
