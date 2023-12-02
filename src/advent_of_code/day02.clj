(ns advent-of-code.day02
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]))

(defn- parse-round [round]
  (let [round (str/replace round "," "")
        parts (partition 2 (str/split round #"\s+"))]
    (reduce (fn [acc cur]
              (assoc acc (keyword (last cur)) (Integer/parseInt (first cur))))
            {} parts)))

(defn- parse-one-line [line]
  (let [[_ game content] (re-matches #"Game (\d+): (.*)" line)
        rounds           (str/split content #"; ")
        game             (Integer/parseInt game)]
    {:game game, :rounds (map parse-round rounds)}))

(defn- parse-games [lines]
  (map parse-one-line lines))

(defn- not-possible [r g b round]
  (or (< r (get round :red 0))
      (< g (get round :green 0))
      (< b (get round :blue 0))))

(defn- is-possible? [r g b {rounds :rounds}]
  (not (some #(not-possible r g b %) rounds)))

(defn- possible-games [r g b games]
  (map :game (filter #(is-possible? r g b %) games)))

(defn part-1
  "Day 02 Part 1"
  [input & vals]
  (->> input
       u/to-lines
       parse-games
       (possible-games 12 13 14)
       (reduce +)))

(defn- get-min-needed [{rounds :rounds}]
  (list (apply max (map #(get % :red 0) rounds))
        (apply max (map #(get % :green 0) rounds))
        (apply max (map #(get % :blue 0) rounds))))

(defn part-2
  "Day 02 Part 2"
  [input]
  (->> input
       u/to-lines
       parse-games
       (map get-min-needed)
       (map #(apply * %))
       (reduce +)))
