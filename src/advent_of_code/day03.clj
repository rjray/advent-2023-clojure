(ns advent-of-code.day03
  (:require [advent-of-code.utils :as u]))

(defn- create-board [input]
  (let [lines (u/to-lines input)
        mat   (u/to-matrix input)]
    {:mat mat,
     :nums (mapv #(u/re-pos #"\d+" %) lines)}))

(def ^:private not-sym #{\0 \1 \2 \3 \4 \5 \6 \7 \8 \9 \.})
(defn- is-sym? [ch] (not (not-sym ch)))

(defn- valid [y x max-y max-x]
  (and (>= x 0)
       (>= y 0)
       (< x max-x)
       (< y max-y)))

(defn- is-part-no? [mat row col max-y max-x num]
  (let [len (count num)]
    (some identity (for [y (range (dec row) (+ row 2))
                         x (range (dec col) (inc (+ col len)))]
                     (and (valid y x max-y max-x)
                          (is-sym? (get-in mat [y x])))))))

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

(defn- get-gears [mat row col max-y max-x num]
  (let [len (count num)
        n'  (parse-long num)]
    (for [y (range (dec row) (+ row 2))
          x (range (dec col) (inc (+ col len)))
          :when (and (valid y x max-y max-x)
                     (= (get-in mat [y x]) \*))]
      (list n' [y x]))))

(defn- find-gears [{mat :mat, num-rows :nums}]
  (let [max-y (count mat)
        max-x (count (first mat))]
    (group-by last
              (map first
                   (filter seq (for [row (range (count num-rows))
                                     [col num] (num-rows row)]
                                 (get-gears mat row col max-y max-x num)))))))

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
