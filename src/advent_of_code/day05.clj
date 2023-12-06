(ns advent-of-code.day05
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]))

(defn- create-block [[label & lines]]
  (let [label (keyword (first (str/split label #"\s+")))]
    (list label
          (reduce (fn [mapping line]
                    (let [[to from length] (u/parse-out-longs line)]
                      (assoc mapping from (list to length))))
                  {} lines))))

(defn- parse-block [block]
  (->> block
       u/to-lines
       create-block))

(defn- create-maps [[seeds & blocks]]
  (let [seeds  (u/parse-out-longs seeds)
        blocks (map parse-block blocks)]
    (assoc (into {} (map hash-map (map first blocks) (map last blocks)))
           :seeds seeds)))

(defn- get-val [base [off len] val]
  (if (>= val (+ base len)) val (+ (- val base) off)))

(defn- find-in-mapping [val fr to maps]
  (let [this-map-key (keyword (apply str (list fr "-to-" to)))
        this-map     (maps this-map-key)
        base         (last (take-while #(<= % val) (sort (keys this-map))))]
    (if base (get-val base (this-map base) val) val)))

(def ^:private pairs
  (partition 2 1 ["seed" "soil" "fertilizer" "water" "light" "temperature"
                  "humidity" "location"]))

(defn- find-location [maps seed]
  (reduce (fn [val pair]
            (find-in-mapping val (first pair) (last pair) maps))
          seed pairs))

(defn- find-locations [maps]
  (map #(find-location maps %) (:seeds maps)))

(defn part-1
  "Day 05 Part 1"
  [input]
  (->> input
       u/to-blocks
       create-maps
       find-locations
       (reduce min)))

;; These didn't work. Brute force is not viable here.
;; (defn- find-location-by-range [maps [base len]]
;;   (apply min (map #(find-location maps %) (range base (+ base len)))))

;; (defn- find-locations-by-range [maps]
;;   (map #(find-location-by-range maps %) (partition 2 (:seeds maps))))

;; Basing this on the Python code at:
;; https://github.com/Leftfish/Advent-of-Code-2023/blob/main/05/d05.py
(defn- find-smallest-in-range [rng maps step]
  (loop [[current & cs] (range (first rng) (apply + rng) step)
         minimum        [-1 -1]]
    (if (nil? current)
      minimum
      (let [newval (find-location maps current)]
        (if (or (= -1 (first minimum))
                (< newval (first minimum)))
          (recur cs [newval current])
          (recur cs minimum))))))

(defn- our< [[t11 t12] [t21 t22]]
  (if (not= t11 t21)
    (< t11 t21)
    (< t12 t22)))

(defn- find-smallest-in-ranges [maps]
  (let [seed-ranges (partition 2 (:seeds maps))
        stop-step   10000]
    (loop [step 100000000, minimum [-1 -1], seed-ranges seed-ranges]
      (if (< step stop-step)
        (let [final (list (- (ffirst seed-ranges) step) (* step 10))]
          (first (find-smallest-in-range final maps 1)))
        ;; else
        (let [range-mins  (map #(find-smallest-in-range % maps step)
                               seed-ranges)
              range-min   (reduce (fn [mins rm]
                                    (if (or (= -1 (first mins))
                                            (our< rm mins))
                                      rm
                                      mins))
                                  minimum range-mins)
              candidate   (list (- (last range-min) step) (* step 10))
              seed-ranges (list candidate)]
          (recur (/ step 10) range-min seed-ranges))))))

(defn part-2
  "Day 05 Part 2"
  [input]
  (->> input
       u/to-blocks
       create-maps
       find-smallest-in-ranges))
