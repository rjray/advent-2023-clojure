(ns advent-of-code.day15
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]))

;; The hashing algorithm is quite simple. Note that I use "->" for threading,
;; since `rem` wants the value in slot 1.
(defn- HASH [string]
  (reduce (fn [val ch]
            (-> val
                (+ (int ch))
                (* 17)
                (rem 256)))
          0 (seq string)))

;; For part 1, just split the instructions and feed all of them to HASH.
(defn- hash-all [line]
  (map HASH (str/split line #",")))

(defn part-1
  "Day 15 Part 1"
  [input]
  (->> input
       u/to-lines
       first
       hash-all
       (reduce +)))

;; Process the `-` instruction, removing the labeled lens if present.
(defn- remove-lens [box label]
  (filterv #(not= label (first %)) box))

;; Process the `=` instruction, adding the labeled lens. Replace the existing
;; label if present, keeping the new lens in the same slot as the old.
(defn- add-lens [box label focal]
  (let [existing (first (for [lidx (range (count box))
                              :when (= label (first (box lidx)))]
                          lidx))]
    (if (nil? existing)
      ;; Not currently in box
      (conj box [label focal])
      ;; Is there
      (assoc-in box [existing 1] focal))))

;; Process the instruction. This just examines the value of `op` and dispatches
;; to either `remove-lens` or `add-lens`.
(defn- process-part [box label op focal]
  (if (= op "-")
    (remove-lens box label)
    (add-lens box label (parse-long focal))))

;; Take the given instruction, determine its target box, and add/remove in that
;; box as appropriate.
(defn- add-to-box [boxes inst]
  (let [[_ label op focal] (re-matches #"([a-z]+)([-=])(\d)?" inst)]
    (update boxes (HASH label) process-part label op focal)))

;; Use `reduce` to process the stream of instructions. Sets up the `boxes`
;; value as a vector of 256 empty vectors and iterates over the split line.
(defn- process-instructions [line]
  (reduce (fn [boxes inst]
            (add-to-box boxes inst))
          (vec (repeat 256 [])) (str/split line #",")))

;; Compute the total focal power of every lens in the series of boxes. There
;; may be a slightly-cleaner way to do this that associates the index with each
;; element, but `range` works fine here.
(defn- get-focal-power [boxes]
  (for [bidx (range 256), lidx (range (count (boxes bidx)))]
    (* (inc bidx) (inc lidx) (last ((boxes bidx) lidx)))))

(defn part-2
  "Day 15 Part 2"
  [input]
  (->> input
       u/to-lines
       first
       process-instructions
       get-focal-power
       (reduce +)))
