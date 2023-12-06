(ns advent-of-code.day06
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]))

;; Simple calculation to see how far the boat will go if the button is held for
;; `time` ms and travels for the remainder.
(defn- calculate [len time]
  (let [travel (- len time)]
    (* travel time)))

;; Find all the values in the range (1..`len`) for which the distance traveled
;; is greater than `record`.
(defn- find-options [[len record]]
  (count (filter #(> % record) (map #(calculate len %) (range 1 len)))))

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
       ;; Map `find-options` over each pair
       (map find-options)
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
       ;; We only have to `find-options` on one pair, now.
       find-options))
