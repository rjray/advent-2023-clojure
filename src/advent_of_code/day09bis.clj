(ns advent-of-code.day09bis
  (:require [advent-of-code.utils :as u]))

;; From Raghav on Clojurians slack:
(defn- infinite-diffs [coll]
  (iterate #(map - (next %) %) coll))

;; And with that, this is all that is needed:
(defn- extrapolate [sequence]
  (reduce + (map last (take-while #(not (every? zero? %))
                                  (infinite-diffs sequence)))))

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
