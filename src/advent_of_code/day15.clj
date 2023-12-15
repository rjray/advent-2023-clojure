(ns advent-of-code.day15
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]))

(defn- HASH [string]
  (reduce (fn [val ch]
            (-> val
                (+ (int ch))
                (* 17)
                (rem 256)))
          0 (seq string)))

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

(defn- remove-part [box label]
  (filterv #(not= label (first %)) box))

(defn- add-lens [box label focal]
  (let [existing (first (for [lidx (range (count box))
                              :when (= label (first (box lidx)))]
                          lidx))]
    (if (nil? existing)
      ;; Not currently in box
      (conj box [label focal])
      ;; Is there
      (assoc-in box [existing 1] focal))))

(defn- process-part [box label op focal]
  (if (= op "-")
    (remove-part box label)
    (add-lens box label (parse-long focal))))

(defn- add-to-box [boxes inst]
  (let [[_ label op focal] (re-matches #"([a-z]+)([-=])(\d)?" inst)]
    (update boxes (HASH label) process-part label op focal)))

(defn- process-instructions [line]
  (reduce (fn [boxes inst]
            (add-to-box boxes inst))
          (vec (repeat 256 [])) (str/split line #",")))

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
