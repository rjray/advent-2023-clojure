(ns advent-of-code.day06
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]))

(defn- calculate [len time]
  (let [travel (- len time)]
    (* travel time)))

(defn- find-options [[len record]]
  (count (filter #(> % record) (map #(calculate len %) (range 1 len)))))

(defn part-1
  "Day 06 Part 1"
  [input]
  (->> input
       u/to-lines
       (map u/parse-out-longs)
       (apply (partial map list))
       (map find-options)
       (reduce *)))

(defn part-2
  "Day 06 Part 2"
  [input]
  (->> input
       u/to-lines
       (map #(str/replace % #"[^\d]" ""))
       (map parse-long)
       find-options))
