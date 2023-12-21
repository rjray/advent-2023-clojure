(ns advent-of-code.day21
  (:require [advent-of-code.utils :as u]))

;; Map the four directions to the movement needed.
(def delta {:U [-1 0], :R [0 1], :D [1 0], :L [0 -1]})

;; Find the start-point in the field. Technically, it's always in the center.
;; But let's not assume.
(defn- find-start [matrix]
  (let [start (first (for [y (range (count matrix))
                           x (range (count (first matrix)))
                           :when (= \S (get-in matrix [y x]))]
                       [y x]))]
    (list start (assoc-in matrix start \.))))

;; Return the valid moves from `pos`. Each move has to be on `matrix` and be a
;; "." character.
(defn- moves [valid? matrix pos]
  (for [move [:U :R :D :L]
        :let [pos' (mapv + pos (delta move))]
        :when (valid? pos')
        :when (= \. (get-in matrix pos'))]
    pos'))

;; Take the possible steps for the set of `places` given. Loops over the
;; positions in `places` and gets the valid moves for each, adding them to a
;; new set.
(defn- take-steps [matrix valid? places]
  (loop [places places, steps #{}]
    (if-let [pos (first places)]
      (recur (disj places pos) (into steps (moves valid? matrix pos)))
      steps)))

;; Find the number of plots the elf can reach in `n` steps.
(defn- find-plots [n [start matrix]]
  (let [max-y  (count matrix)
        max-x  (count (first matrix))
        valid? (fn [[y x]]
                 (and (<= 0 y (dec max-y))
                      (<= 0 x (dec max-x))))]
    (-> #{start}
        (->> (iterate (partial take-steps matrix valid?)))
        (nth n))))

(defn part-1
  "Day 21 Part 1"
  [input]
  (->> input
       u/to-matrix
       find-start
       (find-plots 64)
       count))

;; Totally "adapted" (stolen) from Jan Szejko:
;; https://github.com/janek37/advent-of-code/blob/main/2023/day21.py

;; Create a graph from `matrix` that maps each plot square to the neighbors it
;; can reach. This keeps us from re-calculating this every time we visit a
;; given square.
(defn- to-graph [matrix]
  (let [max-y  (count matrix)
        max-x  (count (first matrix))
        valid? (fn [[y x]]
                 (and (<= 0 y (dec max-y))
                      (<= 0 x (dec max-x))))]
    (into {} (for [y (range max-y), x (range max-x)
                   :let [pos [y x]]
                   :when (= \. (get-in matrix pos))
                   :let [neighbors (moves valid? matrix pos)]]
               (hash-map pos neighbors)))))

;; Update the positions set passed in with new points. This is the part of the
;; original Python that I understand the least.
(defn- update-positions [graph width height positions]
  (loop [[[pos y x] & positions] positions, pos-set #{}]
    (if (nil? pos)
      pos-set
      (let [updates (map #(vector % y x) (graph pos))
            px1     (when (zero? (last pos))
                      [[(first pos) (dec width)] y (dec x)])
            px2     (when (= (dec width) (last pos))
                      [[(first pos) 0] y (inc x)])
            py1     (when (zero? (first pos))
                      [[(dec height) (last pos)] (dec y) x])
            py2     (when (= (dec height) (first pos))
                      [[0 (last pos)] (inc y) x])
            corners (filter (comp not nil?) (list px1 px2 py1 py2))]
        (recur positions (into pos-set (concat updates corners)))))))

;; Get the possible destinations over the "infinite" grid, through `steps`
;; total steps. `steps` here is a modulo of the total steps that reduces the
;; necessary computation.
(defn- get-dests-inf [graph width height start steps]
  (loop [i 0, positions #{start}]
    (if (= i steps)
      positions
      (recur (inc i) (update-positions graph width height positions)))))

;; Count the occurrences of grid-positions relative to the "main" grid.
(defn- get-positions-by-grid [positions]
  (reduce (fn [counts [_ y x]]
            (assoc counts [y x] (inc (get counts [y x] 0))))
          {} positions))

;; Do the multi-step calculation of the final answer, based on the `counts`
;; map created in the previous fn.
(defn- calculate [counts num]
  (let [tip     (apply + (map counts [[0 -2] [0 2] [-2 0] [2 0]]))
        edge1   (apply + (map counts [[-1 -2] [1 -2] [-1 2] [1 2]]))
        edge2   (apply + (map counts [[-1 -1] [1 -1] [-1 1] [1 1]]))
        center1 (counts [1 0])
        center2 (counts [0 0])]
    (+ tip
       (* edge1 num)
       (* edge2 (dec num))
       (* center1 num num)
       (* center2 (dec num) (dec num)))))

;; Find the total number of reachable plots in the "infinite" grid, for `steps`
;; total steps.
(defn- find-plots-inf [steps [start matrix]]
  (let [graph     (to-graph matrix)
        inf-start [start 0 0]
        height    (count matrix)
        width     (count (first matrix))]
    (-> graph
        (get-dests-inf width height inf-start (+ (mod steps width)
                                                 (* width 2)))
        get-positions-by-grid
        (calculate (quot steps width)))))

(defn part-2
  "Day 21 Part 2"
  [input]
  (->> input
       u/to-matrix
       find-start
       (find-plots-inf 26501365)))
