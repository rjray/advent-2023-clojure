(ns advent-of-code.day11
  (:require [advent-of-code.utils :as u]
            [clojure.math.combinatorics :as comb]))

;; Calculate the Manhattan Distance between two points.
(defn- manhattan-dist [p1 p2]
  (+ (abs (- (first p1) (first p2)))
     (abs (- (last  p1) (last  p2)))))

;; Predicate to determine if a row is clear of `#` chars.
(defn- row-clear? [mat row]
  (every? #(= \. %) (mat row)))

;; Predicate to determine if a column is clear of `#` chars. A little trickier
;; than the row-predicate.
(defn- col-clear? [mat col]
  (every? #(= \. %) (map #(% col) mat)))

;; Create a new row that is expanded for each "empty" space.
(defn- expand-x [row empty-x]
  (loop [[x & xs] (range (count row)), newrow []]
    (if (nil? x)
      newrow
      (if (empty-x x)
        (recur xs (conj newrow (row x) \.))
        (recur xs (conj newrow (row x)))))))

;; Expand the full matrix in both dimensions. X expansion is handled with the
;; previous defn, and Y expansion is handled by just inserting a whole new
;; blank row.
(defn- expand [mat]
  (let [max-y   (count mat)
        max-x   (count (first mat))
        empty-y (set (filter (partial row-clear? mat) (range max-y)))
        empty-x (set (filter (partial col-clear? mat) (range max-x)))
        blank   (vec (repeat (+ max-x (count empty-x)) \.))]
    (loop [[y & ys] (range max-y), newmat []]
      (if (nil? y)
        newmat
        (if (empty-y y)
          (recur ys (conj newmat (expand-x (mat y) empty-x) blank))
          (recur ys (conj newmat (expand-x (mat y) empty-x))))))))

;; Find all the galaxies in the matrix-field.
(defn- find-galaxies [mat]
  (let [max-y (count mat)
        max-x (count (first mat))]
    {:matrix mat
     :galaxies (for [y (range max-y), x (range max-x)
                     :when (= \# (get-in mat [y x]))]
                 [y x])}))

;; Find the distances between every pair of galaxies in the data.
(defn- find-distances [data]
  (let [pairs (comb/combinations (:galaxies data) 2)]
    (for [pair pairs]
      (apply manhattan-dist pair))))

(defn part-1
  "Day 11 Part 1"
  [input]
  (->> input
       u/to-matrix
       expand
       find-galaxies
       find-distances
       (reduce +)))

;; Part 2 required expanding each empty space by 1000000 instead of by 2. This
;; made most of the above code inapplicable to the new constraints.

;; Do the expansion in the X axis while also finding galaxies. Iterates over
;; the characters in `row` while maintaining an `x` value that is adjusted by
;; `gap` when necessary. The `y` value is constant for this row.
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
;; Here, the determination of `empty-y` and `empty-x` have to be adjusted for
;; the `gap` value. Each row is handled via the previous defn, and the Y values
;; are adjusted as part of the `loop`.
(defn- find-galaxies-with-expand [gap mat]
  (let [max-y   (count mat)
        max-x   (count (first mat))
        empty-y (filter (partial row-clear? mat) (range max-y))
        empty-y (set (map #(+ %1 (* %2 (dec gap))) empty-y (iterate inc 0)))
        empty-x (filter (partial col-clear? mat) (range max-x))
        empty-x (set (map #(+ %1 (* %2 (dec gap))) empty-x (iterate inc 0)))]
    (loop [[row & rows] mat, y 0, found ()]
      (if (nil? row)
        {:galaxies found}
        (if (empty-y y)
          (recur rows (+ y gap) found)
          (recur rows (inc y)
                 (concat found
                         (expand-and-find-x row y empty-x gap))))))))

(defn part-2
  "Day 11 Part 2"
  [input]
  (->> input
       u/to-matrix
       (find-galaxies-with-expand 1000000)
       find-distances
       (reduce +)))
