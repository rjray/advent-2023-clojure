(ns advent-of-code.day07
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]))

;; This is used to map the characters of a poker hand to numeric values for
;; comparison/ranking.
(def ^:private ^:dynamic values {\2 2 \3 3 \4 4 \5 5 \6 6 \7 7 \8 8 \9 9 \T 10
                                 \J 11 \Q 12 \K 13 \A 14 \1 1})

;; Disambiguate a "two pair" hand from a "three of a kind".
(defn- two-pair? [parts]
  (= (list 1 2 2) (sort (vals parts))))

;; Disambiguate a "full house" hand from a "four of a kind".
(defn- full-house? [parts]
  (= (list 2 3) (sort (vals parts))))

;; For part 2: Apply jokers in the hand. Remove the jokers temporarily, then
;; add their number in to the card that occurs the most (sans jokers). Note that
;; if *all* cards were jokers, there's a corner case (in the `if` s-expr).
(defn- apply-jokers [parts]
  (let [jokers (parts \1)
        parts' (dissoc parts \1)
        key    (last (sort-by parts' (keys parts')))]
    (if key (update parts' key + jokers) {\1 5})))

;; Get the hand's type, a value from 1-7 in ascending order of strength.
(defn get-type [hand]
  (let [parts' (into {} (map #(hash-map (first %) (count (last %)))
                             (group-by identity hand)))
        parts  (if (pos? (get parts' \1 0))
                 (apply-jokers parts')
                 parts')]
    (case (count (keys parts))
      5 1
      4 2
      3 (if (two-pair? parts) 3 4)
      2 (if (full-house? parts) 5 6)
      1 7)))

;; Turn a line into a hand. Separate out the cards (":hand") from the bid-value
;; (":bid"). To this, add the :type using `get-type`.
(defn- to-hands [line]
  (let [[hand bid] (str/split line #"\s+")
        bid         (parse-long bid)]
    {:bid bid, :hand hand, :type (get-type hand)}))

;; A comparative function for sorting hands that are of the same type/rank. I
;; feel like this could be done better, with Clojure primitives.
(defn- cmp-hands [h1 h2]
  (loop [[a & as] (:hand h1), [b & bs] (:hand h2)]
    (cond
      (nil? a) 0
      (= a b)  (recur as bs)
      :else    (- (values a) (values b)))))

;; Sort the hands of the same rank (:type) in ascending order of their relative
;; value.
(defn- sort-rank [rank]
  (sort cmp-hands rank))

;; Given all the hands, produce an ordered (ascending) list of the hands based
;; on their type and their ordering within type. Starts by grouping them by
;; :type, then produces a sequence of just the sub-lists in order of type.
;; These sub-groups are sorted within their rank, then all the sub-lists are
;; concatenated with `apply`+`concat`.
(defn- rank-hands [hands]
  (let [grouped (group-by :type hands)]
    (mapcat sort-rank (map grouped (sort (keys grouped))))))

;; Calculate each hand's winning value. This is the :bid value multiplied by
;; the position in the list (starting at 1). Use `(iterate inc 1)` to provide
;; the continuous list of incremental numbers.
(defn- get-winnings [hands]
  (map #(* (:bid %1) %2) hands (iterate inc 1)))

(defn part-1
  "Day 07 Part 1"
  [input]
  (->> input
       u/to-lines
       (map to-hands)
       rank-hands
       get-winnings
       (reduce +)))

;; This only differs from part 1 by the third s-expr in the ->> threading. That
;; converts all `J` characters to `1`, for treatment as jokers.
(defn part-2
  "Day 07 Part 2"
  [input]
  (->> input
       u/to-lines
       (map #(str/replace % "J" "1"))
       (map to-hands)
       rank-hands
       get-winnings
       (reduce +)))
