(ns advent-of-code.day01
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]))

(defn- parse-digits [line]
  (map #(Integer/parseInt %) (re-seq #"\d" line)))

(defn- get-val [pair] (+ (* 10 (first pair)) (last pair)))

(defn part-1
  "Day 01 Part 1"
  [input]
  (->> input
       u/to-lines
       (map parse-digits)
       (map get-val)
       (apply +)))

(def ^:private t2d-map {"zero" 0,
                        "0" 0,
                        "one" 1,
                        "1" 1,
                        "two" 2,
                        "2" 2,
                        "three" 3,
                        "3" 3,
                        "four" 4,
                        "4" 4,
                        "five" 5,
                        "5" 5,
                        "six" 6,
                        "6", 6,
                        "seven" 7,
                        "7" 7,
                        "eight" 8,
                        "8" 8,
                        "nine" 9,
                        "9" 9})

(defn- txt2digit [line]
  (map (comp t2d-map last)
       (re-seq #"(?=([0-9]|one|two|three|four|five|six|seven|eight|nine))"
               line)))

(defn part-2
  "Day 01 Part 2"
  [input]
  (->> input
       u/to-lines
       (map txt2digit)
       (map get-val)
       (apply +)))
