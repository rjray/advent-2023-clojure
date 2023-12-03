(ns advent-of-code.day03
  (:require [advent-of-code.utils :as u]))

;; For this pair of puzzles, we need to both the line-wise version of the input
;; and the matrix version. The line-wise will be used to extract the numbers
;; from each line while also recording their start-position.
(defn- create-board [input]
  (let [lines (u/to-lines input)
        mat   (u/to-matrix input)]
    {:mat mat,
     :nums (mapv #(u/re-pos #"\d+" %) lines)}))

;; This is a fairly-crude way of testing whether a point has a symbol or not.
(def ^:private not-sym #{\0 \1 \2 \3 \4 \5 \6 \7 \8 \9 \.})
(defn- is-sym? [ch] (not (not-sym ch)))

;; This tests whether a point [y x] is valid within the [max-y max-x] range.
(defn- valid [y x max-y max-x]
  (and (>= x 0)
       (>= y 0)
       (< x max-x)
       (< y max-y)))

;; For part 1, test if this is a "valid" part number by looking at all points
;; around the digits and flagging anything that is a "symbol" (not a digit or
;; a `.`).
(defn- is-part-no? [mat row col max-y max-x num]
  (let [len (count num)]
    (some identity (for [y (range (dec row) (+ row 2))
                         x (range (dec col) (inc (+ col len)))]
                     (and (valid y x max-y max-x)
                          (is-sym? (get-in mat [y x])))))))

;; Find the subset of numbers in the grid that are considered valid part
;; numbers.
(defn- find-part-nos [{mat :mat, num-rows :nums}]
  (let [max-y (count mat)
        max-x (count (first mat))]
    (map parse-long
         (for [row (range (count num-rows)), [col num] (num-rows row)
               :when (is-part-no? mat row col max-y max-x num)]
           num))))

(defn part-1
  "Day 03 Part 1"
  [input]
  (->> input
       create-board
       find-part-nos
       (reduce +)))

;; Find all "gears" (`*`) around the specified number. For all that are found,
;; return a list consisting of the number (converted to an int) and the [y x]
;; coordinates of the gear.
(defn- get-gears [mat row col max-y max-x num]
  (let [len (count num)
        n'  (parse-long num)]
    (for [y (range (dec row) (+ row 2))
          x (range (dec col) (inc (+ col len)))
          :when (and (valid y x max-y max-x)
                     (= (get-in mat [y x]) \*))]
      (list n' [y x]))))

;; Find all "gears" over all the grid. This (along with the logic of
;; `get-gears`, above) was meant to catch cases where one part number might be
;; adjacent to multiple gears, but I think I got that wrong. Luckily, it didn't
;; affect the answer.
(defn- find-gears [{mat :mat, num-rows :nums}]
  (let [max-y (count mat)
        max-x (count (first mat))]
    (group-by last
              (map first
                   (filter seq (for [row (range (count num-rows))
                                     [col num] (num-rows row)]
                                 (get-gears mat row col max-y max-x num)))))))

;; Take the gears data, filter out all gears that have exactly two adjacent
;; part numbers, and create a sequence of those products.
(defn- get-gear-ratios [gears]
  (map #(* (ffirst %) (first (second %)))
       (filter #(= 2 (count %)) (vals gears))))

(defn part-2
  "Day 03 Part 2"
  [input]
  (->> input
       create-board
       find-gears
       get-gear-ratios
       (reduce +)))
