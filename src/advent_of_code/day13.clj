(ns advent-of-code.day13
  (:require [advent-of-code.utils :as u]))

;; https://stackoverflow.com/questions/10347315/matrix-transposition-in-clojure
(defn- transpose [m]
  (apply mapv vector m))

;; Check for a horizontal reflection. Use `eq` for the equality test, so that
;; it can be overridden for part 2.
(defn- horiz-reflection [m eq]
  (let [all (filter pos? (for [l (range 1 (count m))
                               :let [[top btm] (split-at l m)]]
                           (if (eq (take (count btm) (reverse top))
                                   (take (count top) btm))
                             l 0)))]
    (if (seq all) (first all) 0)))

;; Find the reflection point for `m`. Try a horizontal reflection on the input
;; value first, if that doesn't find one then try it on a transpose of `m`.
(defn- find-reflection-point [m]
  (let [refl (horiz-reflection m =)]
    (if (pos? refl)
      (* 100 refl)
      (horiz-reflection (transpose m) =))))

(defn part-1
  "Day 13 Part 1"
  [input]
  (->> input
       u/to-blocks
       (map u/to-matrix)
       (map find-reflection-point)
       (reduce +)))

;; Taken from https://github.com/erdos/advent-of-code/blob/master/2023/day13.clj
(defn- mirror= [a b]
  (= 1 (reduce + (mapcat (partial map #(if (= %1 %2) 0 1)) a b))))

;; Just like `find-reflection-point`, but passes `mirror=` to the reflection
;; calls.
(defn- find-smudge-point [m]
  (let [refl (horiz-reflection m mirror=)]
    (if (pos? refl)
      (* 100 refl)
      (horiz-reflection (transpose m) mirror=))))

(defn part-2
  "Day 13 Part 2"
  [input]
  (->> input
       u/to-blocks
       (map u/to-matrix)
       (map find-smudge-point)
       (reduce +)))
