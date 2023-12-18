(ns advent-of-code.day18
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]))

(def delta {:U [-1 0], :R [0 1], :D [1 0], :L [0 -1]})

(defn- parse-inst [line]
  (let [[_ dir len color] (re-matches #"^([URDL]) (\d+) \(#([0-9a-f]{6})\)"
                                      line)]
    (list (keyword dir) (parse-long len) color)))

(defn- get-info [[[y1 x1] [y2 x2]]]
  (- (* x1 y2) (* y1 x2)))

(defn- solve [points]
  (+ (get-info (list (last points) (first points)))
     (reduce + (map get-info (partition 2 1 points)))))

(defn- find-polys [insts]
  (loop [[inst & insts] insts, poly (list [0 0]), p_cnt 0]
    (if (nil? inst)
      (inc (quot (+ (solve (reverse poly)) p_cnt) 2))
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
       find-polys))

(def get-dir {"0" :R, "1" :D, "2" :L, "3" :U})

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
       find-polys))
