(ns advent-of-code.day09
  (:require [advent-of-code.utils :as u]))

;; This is all the logic needed for both parts 1 and 2. Start with the input
;; sequence and do a step-wise reduction until we reach a sequence of all 0.
;; Along the way, sum all of the end (`last`) numbers.
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

;; For part 2, we just have to reverse all of the lines and `extrapolate` will
;; still work.
(defn part-2
  "Day 09 Part 2"
  [input]
  (->> input
       u/to-lines
       (map u/parse-out-longs)
       (map reverse)
       (map extrapolate)
       (reduce +)))
