(ns advent-of-code.day10
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]))

;; This tests whether a point [y x] is valid within the [max-y max-x] range.
(defn- valid? [[y x] [max-y max-x]]
  (and (>= x 0)
       (>= y 0)
       (< x max-x)
       (< y max-y)))

;; Find the points that the given point can reach, based on the character at
;; that point. Any `.` goes nowhere, and the `S` is marked specially so that it
;; can be identified later.
(defn- reaches [ch y x my mx]
  (case ch
    \. ()
    \S (list \*)
    \| (filter #(valid? % [my mx]) (list [(dec y) x] [(inc y) x]))
    \- (filter #(valid? % [my mx]) (list [y (dec x)] [y (inc x)]))
    \L (filter #(valid? % [my mx]) (list [(dec y) x] [y (inc x)]))
    \J (filter #(valid? % [my mx]) (list [(dec y) x] [y (dec x)]))
    \7 (filter #(valid? % [my mx]) (list [(inc y) x] [y (dec x)]))
    \F (filter #(valid? % [my mx]) (list [(inc y) x] [y (inc x)]))))

;; Build the initial structure. This has the original matrix data (needed for
;; part 2) and the graph that results from mapping each square to the 0-2 other
;; squares it can reach.
(defn- build-struct [matrix]
  (let [max-y (count matrix)
        max-x (count (first matrix))]
    {:graph  (into {} (for [y (range max-y), x (range max-x)]
                        {[y x] (reaches (get-in matrix [y x])
                                        y x max-y max-x)}))
     :matrix matrix}))

;; Given a collection (list) and a value, return `true` if the value is present.
(defn- has? [coll val]
  (if (some #(= val %) coll) true false))

;; Find the point on the loop furthest from the `S` point. Augments `struct`
;; with two new keys: `:furthest` is the distance to the furthest point from
;; `S` on the loop. `:seen` is a set of all the points that make up the loop.
(defn- find-furthest [struct]
  (let [graph (:graph struct)
        S     (first (filter #(= (graph %) '(\*)) (keys graph)))
        S-adj (filter #(has? (graph %) S) (keys graph))]
    (loop [queue (map #(vec [% 1]) S-adj), seen {S 0}]
      (if (empty? queue)
        (assoc struct
               :furthest (apply max (vals seen))
               :seen     (set (keys seen)))
        (let [[node dist] (first queue)
              frontier    (filter (comp not (partial contains? seen))
                                  (graph node))]
          (recur (concat (rest queue) (map #(vec [% (inc dist)]) frontier))
                 (assoc seen node dist)))))))

(defn part-1
  "Day 10 Part 1"
  [input]
  (->> input
       u/to-matrix
       build-struct
       find-furthest
       :furthest))

;; Out of fatigue, adapted from
;; https://github.com/ricbit/advent-of-code/blob/main/2023/adv10-r.py

;; Go through the original matrix, change all squares that are not part of the
;; loop (not in `seen`) to `.` characters. Rather than trying to modify `matrix`
;; in place, just generate a sequence of characters and then partition them by
;; `max-x`.
(defn- erase-not [{:keys [seen matrix]}]
  (let [max-y (count matrix)
        max-x (count (first matrix))]
    (partition max-x (for [y (range max-y), x (range max-x)]
                       (if (seen [y x]) (get-in matrix [y x]) \.)))))

;; Get a count of interior points on the given line. If the line contains the
;; `S`, turn it into a `J` per examination of the puzzle data.
(defn- count-interior [line]
  ;; S->J is hard-coded. Necessary evil for now.
  (let [line' (str/replace (str/replace (str/replace line "S" "J")
                                        #"F-*7|L-*J" "")
                           #"F-*J|L-*7" "|")]
    (loop [[ch & chs] line', interior 0, ans 0]
      ;; The basic algorithm is, count the `|` characters as we go. When a `.`
      ;; is seen, it is considered inside the loop if the current count of `|`
      ;; is odd. Rather than test the count for odd-ness, just add the `rem`
      ;; value.
      (cond
        (nil? ch) ans
        (= ch \|) (recur chs (inc interior) ans)
        ;; ch == \.
        :else     (recur chs interior (if (= ch \.)
                                        (+ ans (rem interior 2))
                                        ans))))))

;; Count the full area inside the loop by running `count-interior` over each
;; line and summing them up.
(defn- count-area [matrix]
  (reduce + (map #(count-interior (apply str %)) matrix)))

(defn part-2
  "Day 10 Part 2"
  [input]
  (->> input
       u/to-matrix
       build-struct
       find-furthest
       erase-not
       count-area))
