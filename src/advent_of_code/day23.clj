(ns advent-of-code.day23
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]))

;; Map the four directions to the movement needed.
(def delta {:U [-1 0], :R [0 1], :D [1 0], :L [0 -1]})

(def square #{\. \^ \> \v \<})

(def forced {\^ :U, \> :R, \v :D, \< :L})

(defn- get-start-and-goal [matrix]
  {:S [0 (str/index-of (first matrix) \.)]
   :E [(dec (count matrix)) (str/index-of (last matrix) \.)]
   :G (into {} (for [y (range (count matrix)), x (range (count (first matrix)))
                     :when (square (get-in matrix [y x]))]
                 (hash-map [y x] (get-in matrix [y x]))))})

(defn- new-state [G steps pos] {:G G, :steps steps, :pos pos})

(defn- take-step [{:keys [G steps pos]}]
  (let [options   (if (forced (G pos)) [(forced (G pos))] [:U :R :D :L])
        new-steps (for [dir options
                        :let [pos' (mapv + pos (delta dir))]
                        :when (G pos')
                        :when (not (steps pos'))]
                    pos')]
    (map #(new-state G (conj steps %) %) new-steps)))

(defn- find-longest-path [{:keys [S E G]}]
  (loop [states (list (new-state G #{S} S)), fini ()]
    (if-let [state (first states)]
      (if (= (:pos state) E)
        (recur (rest states) (cons (dec (count (:steps state))) fini))
        (recur (into (rest states) (take-step state)) fini))
      (apply max fini))))

(defn part-1
  "Day 23 Part 1"
  [input]
  (->> input
       u/to-lines
       get-start-and-goal
       find-longest-path))

(defn- neighbors-of [vertex G]
  (for [dir [:U :R :D :L]
        :let [pos' (mapv + vertex (delta dir))]
        :when (G pos')]
    pos'))

(defn- get-position [G vertex step]
  (loop [length 1, prev vertex, position step, stop false, dead-end false]
    (if stop
      (when-not dead-end (list position length))
      (let [neighbors (neighbors-of position G)]
        (cond
          (and (= neighbors [prev]) (forced (G position)))
          ;; This is a dead-end: stop the loop and signal as much.
          (recur length prev position true true)

          (not= (count neighbors) 2)
          ;; This is a stop-point, but not a dead-end.
          (recur length prev position true false)

          :else
          ;; Continue the loop with the neighbor that isn't prev.
          (recur (inc length)
                 position
                 (first (filter #(not= prev %) neighbors))
                 false
                 false))))))

(defn- get-positions [G vertex]
  (for [step (neighbors-of vertex G)
        :let [position (get-position G vertex step)]
        :when position]
    position))

(defn- create-graph [{:keys [S E G]}]
  (loop [vertices (into clojure.lang.PersistentQueue/EMPTY [S])
         visited #{}
         graph {}]
    (if-let [vertex (peek vertices)]
      (if-not (visited vertex)
        (let [positions (get-positions G vertex)]
          (recur (into (pop vertices) (map first positions))
                 (conj visited vertex)
                 (assoc graph vertex positions)))
        (recur (pop vertices) visited graph))
      {:S S, :E E, :G graph})))

(defn- find-really-longest-path [{:keys [S E G]}]
  (loop [stack (list [S 0 #{S}]), paths ()]
    (if-let [[last length visited] (first stack)]
      (if (= last E)
        (recur (rest stack) (cons length paths))
        (let [new-states (for [[new edge-length] (G last)
                               :when (not (visited new))]
                           [new (+ length edge-length) (conj visited new)])]
          (recur (concat new-states (rest stack)) paths)))
      (apply max paths))))

(defn part-2
  "Day 23 Part 2"
  [input]
  (->> input
       u/to-lines
       get-start-and-goal
       create-graph
       find-really-longest-path))
