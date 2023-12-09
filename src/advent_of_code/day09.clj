(ns advent-of-code.day09
  (:require [advent-of-code.utils :as u]))

(defn- extrapolate [sequence]
  (loop [sequence sequence, next-term 0]
    (if (every? zero? sequence)
      next-term
      (recur (map #(apply - (reverse %)) (partition 2 1 sequence))
             (+ next-term (last sequence))))))

(defn part-1
  "Day 09 Part 1"
  [input]
  (->> input
       u/to-lines
       (map u/parse-out-longs)
       (map extrapolate)
       (reduce +)))

(defn part-2
  "Day 09 Part 2"
  [input]
  (->> input
       u/to-lines
       (map u/parse-out-longs)
       (map reverse)
       (map extrapolate)
       (reduce +)))
