(ns advent-of-code.day04
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]))

;; Parse a single card. We need to separate groups of numbers, so first split
;; on the "|". Then use parse-out-longs from utils. The winning numbers will be
;; expressed as a set for easy testing, "my" numbers will be left as a list.
(defn- parse-card [card]
  (let [[winning mine] (str/split card #"\s+[|]\s+")
        w'             (u/parse-out-longs winning)
        m'             (u/parse-out-longs mine)]
    [(set (rest w')) m']))

;; Determine the "score" of a card-- how many of my numbers match the winning
;; numbers. Don't worry about converting that to points, we'll do that in the
;; main loop.
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

;; Adjust the vector of `counts` based on how many winning numbers card `n`
;; had. Note that each "prize" card is increased by `x`, where x is the number
;; of instances of card n that we have.
(defn- adjust [counts n wins]
  (let [x (counts n)]
    (loop [[m & ms] (range wins), counts counts]
      (cond
        (nil? m) counts
        :else    (recur ms (update counts (+ n m 1) + x))))))

;; Count the cards. Start at the first card and go forward. No need for any
;; backtracking due to the nature of the problem (all additions are forward).
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
