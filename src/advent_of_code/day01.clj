(ns advent-of-code.day01
  (:require [advent-of-code.utils :as u]))

;; This is about the simplest way I know of, for extracting numbers from a line
;; of text. Here I want single digits, so the RE is "\d". Normally it would be
;; "\d+".
(defn- parse-digits [line]
  (map #(Integer/parseInt %) (re-seq #"\d" line)))

;; I felt this would be faster than concatenating two one-character strings and
;; passing the result to Integer/parseInt.
(defn- get-val [pair] (+ (* 10 (first pair)) (last pair)))

(defn part-1
  "Day 01 Part 1"
  [input]
  (->> input
       u/to-lines
       (map parse-digits)
       (map get-val)
       (apply +)))

;; This private map is used to map string digits and the word-digits to their
;; actual numeric values.
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

;; Here, `re-seq` is used with capturing, because the zero-width-lookahead
;; was causing matches to come back as `nil`. For that reason, I use `comp`
;; with the above map and `last` to get the matched string out of the vector
;; that represents each match.
(defn- txt2digit [line]
  (map (comp t2d-map last)
       (re-seq #"(?=([0-9]|one|two|three|four|five|six|seven|eight|nine))"
               line)))

;; This is identical to part-1, except for using `txt2digit` in place of
;; `parse-digits`.
(defn part-2
  "Day 01 Part 2"
  [input]
  (->> input
       u/to-lines
       (map txt2digit)
       (map get-val)
       (apply +)))
