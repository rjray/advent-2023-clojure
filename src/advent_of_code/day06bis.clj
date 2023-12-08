(ns advent-of-code.day06bis
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]))

;; Quadratic formula. Taken from a comment on reddit.
;; https://www.reddit.com/r/Clojure/comments/18cen0z/advent_of_code_day_6/kca5xk5/
(defn- calculate [[t d]]
  (->> [(int (Math/ceil (/ (- t (Math/sqrt (- (* t t) (* 4 d)))) 2)))
        (int (Math/ceil (/ (+ t (Math/sqrt (- (* t t) (* 4 d)))) 2)))]
       sort
       reverse
       (apply -)))

;; Don't usually have so much of the logic here in the runner, so I'll comment
;; this as well (inline).
(defn part-1
  "Day 06 Part 1"
  [input]
  (->> input
       u/to-lines
       (map u/parse-out-longs)
       ;; Take the two seq's of numbers from the previous step and create a seq
       ;; of pairs from them.
       (apply (partial map list))
       ;; Map `calculate` over each pair
       (map calculate)
       ;; Multiply the results
       (reduce *)))

;; Same here. Worth noting what's going on inline.
(defn part-2
  "Day 06 Part 2"
  [input]
  (->> input
       u/to-lines
       ;; Each line has all non-digit characters removed:
       (map #(str/replace % #"[^\d]" ""))
       ;; The resulting two elements are converted to numbers:
       (map parse-long)
       ;; We only have to `calculate` on one pair, now.
       calculate))
