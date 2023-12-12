(ns advent-of-code.day11bis
  (:require [advent-of-code.utils :as u]
            [clojure.math.combinatorics :as comb]))

;; Calculate the Manhattan Distance between two points.
(defn- manhattan-dist [p1 p2]
  (+ (abs (- (first p1) (first p2)))
     (abs (- (last  p1) (last  p2)))))

;; Find the distances between every pair of galaxies in the data.
(defn- find-distances [galaxies]
  (let [pairs (comb/combinations galaxies 2)]
    (for [pair pairs]
      (apply manhattan-dist pair))))

;; Predicate to determine if a row is clear of `#` chars.
(defn- row-clear? [mat row]
  (every? #(= \. %) (mat row)))

;; Predicate to determine if a column is clear of `#` chars. A little trickier
;; than the row-predicate.
(defn- col-clear? [mat col]
  (every? #(= \. %) (map #(% col) mat)))

;; Do the expansion in the X axis while also finding galaxies. Iterates over
;; the characters in `row` while maintaining an `x` value that is adjusted by
;; `gap` when necessary. The `y` value is constant for the row.
(defn- expand-and-find-x [row y empty-x gap]
  (loop [[ch & chs] row, x 0, found ()]
    (if (nil? ch)
      found
      (if (empty-x x)
        (recur chs (+ x gap) found)
        (recur chs (inc x) (if (= ch \#)
                             (cons [y x] found)
                             found))))))

;; Find all the galaxies in the field, while handling expansion in both axes.
;; The determination of `empty-y` and `empty-x` are adjusted for the `gap`
;; value. Each row is handled via the previous defn, and the Y values are
;; adjusted as part of the `loop`.
(defn- find-galaxies-with-expansion [gap mat]
  (let [;; For the empty-* lists, we get the actual empty slots, then augment
        ;; each one by increasing multiples of `(dec gap)`.
        empty-y (set (map #(+ %1 (* %2 (dec gap)))
                          (filter (partial row-clear? mat) (range (count mat)))
                          (iterate inc 0)))
        empty-x (set (map #(+ %1 (* %2 (dec gap)))
                          (filter (partial col-clear? mat)
                                  (range (count (first mat))))
                          (iterate inc 0)))]
    (loop [[row & rows] mat, y 0, found ()]
      (if (nil? row)
        found
        (if (empty-y y)
          (recur rows (+ y gap) found)
          (recur rows (inc y)
                 (concat found
                         (expand-and-find-x row y empty-x gap))))))))

(defn part-1
  "Day 11 Part 1"
  [input]
  (->> input
       u/to-matrix
       (find-galaxies-with-expansion 2)
       find-distances
       (reduce +)))

(defn part-2
  "Day 11 Part 2"
  [input]
  (->> input
       u/to-matrix
       (find-galaxies-with-expansion 1000000)
       find-distances
       (reduce +)))
