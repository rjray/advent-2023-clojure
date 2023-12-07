(ns advent-of-code.day07
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]))

(def ^:private ^:dynamic values {\2 2 \3 3 \4 4 \5 5 \6 6 \7 7 \8 8 \9 9 \T 10
                                 \J 11 \Q 12 \K 13 \A 14 \1 1})

(defn- two-pair? [parts]
  (= (list 1 2 2) (sort (vals parts))))

(defn- full-house? [parts]
  (= (list 2 3) (sort (vals parts))))

(defn- apply-jokers [parts]
  (let [jokers (parts \1)
        parts' (dissoc parts \1)
        key    (last (sort-by parts' (keys parts')))]
    (if key (update parts' key + jokers) {\J 5})))

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

(defn- to-hands [line]
  (let [[hand bid] (str/split line #"\s+")
        bid         (parse-long bid)]
    {:bid bid, :hand hand, :type (get-type hand)}))

(defn- cmp-hands [h1 h2]
  (loop [[a & as] (:hand h1), [b & bs] (:hand h2)]
    (cond
      (nil? a) 0
      (= a b)  (recur as bs)
      :else    (- (values a) (values b)))))

(defn- sort-rank [rank]
  (sort cmp-hands rank))

(defn- rank-hands [hands]
  (let [grouped (group-by :type hands)]
    (apply concat (map sort-rank (map #(grouped %) (sort (keys grouped)))))))

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
