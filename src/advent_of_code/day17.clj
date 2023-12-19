(ns advent-of-code.day17
  (:require [advent-of-code.utils :as u]
            [clojure.data.priority-map :refer [priority-map]]))

;; Some basic "constants" for this: map digits to numbers, convert direction
;; keywords to movement vectors, and define "positive infinity" as the max long
;; value.
(def digit-map {\0 0, \1 1, \2 2, \3 3, \4 4, \5 5, \6 6, \7 7, \8 8, \9 9})
(def delta     {:n [-1 0], :e [0 1], :s [1 0], :w [0 -1]})
(def +inf      Long/MAX_VALUE)

;; Calculate the Manhattan Distance between two points.
(defn- manhattan-dist [[y1 x1] [y2 x2]]
  (+ (abs (- y1 y2)) (abs (- x1 x2))))

;; Wrapper around the utils/to-matrix function, to convert each character to a
;; number.
(defn- to-matrix [input]
  (mapv #(mapv digit-map %) (u/to-matrix input)))

;; Take the input and make it into a graph, with the coordinates as keys and
;; the heat numbers as values.
(defn- to-graph [input]
  (let [matrix (to-matrix input)]
    (into {} (for [y (range (count matrix)), x (range (count (first matrix)))]
               (hash-map [y x] (get-in matrix [y x]))))))

;; From here, heavily influenced by
;; https://gitlab.com/maximoburrito/advent2023/-/blob/main/src/day17/main.clj
;;
;; Pretty sure that solution uses straight Dijkstra, where this is A*. But I'm
;; also not sure that this is any faster...

;; Get a move from `pos` in the `dir` direction for `n` steps. Return the
;; position and the cost for that position based on `base` and the heat-loss
;; at each step.
(defn- move-cost [graph base pos dir n]
  (reduce (fn [[cur-pos cur-cost] _]
            (let [pos'  (mapv + cur-pos dir)
                  cost' (+ cur-cost (graph pos' 0))]
              [pos' cost']))
          [pos base] (range 1 (inc n))))

;; Get the move from the position in `state`, heading `n` steps in direction
;; `dir`. The return value has the new state (with `horiz` toggled) and the cost
;; for that move. If the new position is outside the graph, returns nil.
(defn- move [graph base state dir n]
  (let [[pos horiz]  state
        [pos' cost'] (move-cost graph base pos (dir delta) n)
        horiz'       (not horiz)]
    (when (graph pos') [[pos' horiz'] cost'])))

;; An implementation of A* search that keeps track of movement direction along
;; with each coordinate pair. The keys in the queue combine a Boolean with the
;; coordinates, and use the F-score as the value. Thus, `open` doubles as both
;; "openSet" and "fScore" in the pseudocode in the Wikipedia entry for A*
;; (https://en.wikipedia.org/wiki/A*_search_algorithm).
(defn- A* [moves graph]
  (let [goal (last (sort (keys graph)))
        h    (partial manhattan-dist goal)]
    (loop [open (priority-map [[0 0] true] (h [0 0]), [[0 0] false] (h [0 0]))
           g-score {[[0 0] true] 0, [[0 0] false] 0}]
      (let [[[pos horiz :as state] _] (first open)]
        (cond
          (nil? state) "failed"
          (= pos goal) (g-score state)
          :else
          (let [pool (for [dir (if horiz [:e :w] [:n :s]), n moves
                           :let [state' (move graph
                                              (g-score state)
                                              state dir n)]
                           :when state'
                           :let [[key' cost] state'
                                 f-cost          (+ cost (h (first key')))]
                           :when (< cost (g-score key' +inf))
                           :when (< f-cost (open key' +inf))]
                       (conj state' f-cost))]
            (recur (into (pop open) (map #(vector (first %) (last %)) pool))
                   (into g-score (map #(vector (first %) (second %))
                                      pool)))))))))

(defn part-1
  "Day 17 Part 1"
  [input]
  (->> input
       to-graph
       (A* (range 1 4))))

(defn part-2
  "Day 17 Part 2"
  [input]
  (->> input
       to-graph
       (A* (range 4 11))))
