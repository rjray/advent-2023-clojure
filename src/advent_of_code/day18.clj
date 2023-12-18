(ns advent-of-code.day18
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]))

;; Map the four directions to the movement needed.
(def delta {:U [-1 0], :R [0 1], :D [1 0], :L [0 -1]})

;; Parse an instruction line into a tuple of (direction, steps, color).
(defn- parse-inst [line]
  (let [[_ dir len color] (re-matches #"^([URDL]) (\d+) \(#([0-9a-f]{6})\)"
                                      line)]
    (list (keyword dir) (parse-long len) color)))

;; Do the "shoelace" calculation.
(defn- shoelace [[[y1 x1] [y2 x2]]]
  (- (* x1 y2) (* y1 x2)))

;; Do the full calculation. Use overlapping partitioning to iterate calls to
;; `shoelace` over adjacent pairs. Then add in a call between the last and first
;; points.
(defn- calculate [points]
  (+ (shoelace (list (last points) (first points)))
     (reduce + (map shoelace (partition 2 1 points)))))

;; Find the points that describe the polygons, based on the list of instructions
;; given. Once all are found, call `calculate` and apply the last bit of math
;; to get the answer.
(defn- solve [insts]
  (loop [[inst & insts] insts, poly (list [0 0]), p_cnt 0]
    (if (nil? inst)
      (inc (quot (+ (calculate (reverse poly)) p_cnt) 2))
      (let [[dir len] inst
            lp        (first poly)
            newpos    (mapv + lp (mapv * [len len] (dir delta)))]
        (recur insts (cons newpos poly) (+ p_cnt len))))))

(defn part-1
  "Day 18 Part 1"
  [input]
  (->> input
       u/to-lines
       (map parse-inst)
       solve))

;; For part 2, we get direction from the last digit of the "color" value. This
;; will come as a string, so map these to the direction keywords.
(def get-dir {"0" :R, "1" :D, "2" :L, "3" :U})

;; "Fix" an instruction by replacing it with direction and step-value as pulled
;; from the hexadecimal "color" value.
(defn- fix-inst [[_ _ color]]
  (let [[_ val dir] (re-matches #"([0-9a-f]{5})([0-3])" color)]
    (list (get-dir dir) (Integer/parseInt val 16))))

(defn part-2
  "Day 18 Part 2"
  [input]
  (->> input
       u/to-lines
       (map parse-inst)
       (map fix-inst)
       solve))
