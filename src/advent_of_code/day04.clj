(ns advent-of-code.day04
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]))

(defn- parse-card [card]
  (let [[winning mine] (str/split card #"\s+[|]\s+")
        w'             (u/parse-out-longs winning)
        m'             (u/parse-out-longs mine)]
    [(set (rest w')) m']))

(defn- score-card [[winning mine]]
  (count (filter winning mine)))

(defn part-1
  "Day 04 Part 1"
  [input]
  (->> input
       u/to-lines
       (map parse-card)
       (map score-card)
       (filter pos?)
       (map dec)
       (map #(reduce * (repeat % 2)))
       (reduce +)))

(defn- adjust [counts n wins]
  (let [x (counts n)]
    (loop [[m & ms] (range wins), counts counts]
      (cond
        (nil? m) counts
        :else    (recur ms (update counts (+ n m 1) + x))))))

(defn- count-cards [cards]
  (let [counts (vec (repeat (count cards) 1))]
    (loop [[n & ns] (range (count cards)), counts counts]
      (cond
        (nil? n) (reduce + counts)
        :else    (recur ns (adjust counts n (score-card (cards n))))))))

(defn part-2
  "Day 04 Part 2"
  [input]
  (->> input
       u/to-lines
       (mapv parse-card)
       count-cards))
