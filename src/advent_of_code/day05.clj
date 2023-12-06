(ns advent-of-code.day05
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]))

;; Create the block for a single mapping. Takes the "name" and converts it to a
;; keyword, then massages the numbers into something I can use, in map form.
(defn- create-block [[label & lines]]
  (let [label (keyword (first (str/split label #"\s+")))]
    (list label
          (reduce (fn [mapping line]
                    (let [[to from length] (u/parse-out-longs line)]
                      (assoc mapping from (list to length))))
                  {} lines))))

;; Parse one block. The blocks were created by u/to-blocks, this will handle a
;; single one by breaking it into lines and calling the above defn.
(defn- parse-block [block]
  (->> block
       u/to-lines
       create-block))

;; Create the 7 x-to-y maps. It turns out that this is over-engineered, as I
;; didn't look at the input file closely-enough to realize the maps were already
;; in the order they would be used. There was no reason to store the map "name"
;; with each one.
(defn- create-maps [[seeds & blocks]]
  (let [seeds  (u/parse-out-longs seeds)
        blocks (map parse-block blocks)]
    (assoc (into {} (map hash-map (map first blocks) (map last blocks)))
           :seeds seeds)))

;; Get the value for the seed `val`, using the values from a mapping.
(defn- get-val [base [off len] val]
  (if (>= val (+ base len)) val (+ (- val base) off)))

;; Find a value in a given mapping, using the rules given. `fr` and `to` are
;; used to create the keyword that indexes the map, but it turns out that this
;; wasn't necessary after all. I'll probably refactor this code later to remove
;; this.
(defn- find-in-mapping [val fr to maps]
  (let [this-map-key (keyword (apply str (list fr "-to-" to)))
        this-map     (maps this-map-key)
        base         (last (take-while #(<= % val) (sort (keys this-map))))]
    (if base (get-val base (this-map base) val) val)))

;; This creates the pairs that specify each map. The 8 "elements" are in order,
;; and the `(partition 2 1 ...)` creates all the adjacent pairs.
(def ^:private pairs
  (partition 2 1 ["seed" "soil" "fertilizer" "water" "light" "temperature"
                  "humidity" "location"]))

;; Using `find-in-mapping` and `pairs`, get the location for a given `seed`.
(defn- find-location [maps seed]
  (reduce (fn [val pair]
            (find-in-mapping val (first pair) (last pair) maps))
          seed pairs))

;; Find the locations for all seeds read from the input data.
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

;; Find the smallest location in the given seed-range, using sampling based on
;; the value of `step`. I realize now, looking at it, that I could do the
;; management of `newval` on lines 79-81 better.
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

;; This is a quick-and-dirty "copy" of Python's ability to use `<` on tuples.
(defn- our< [[t11 t12] [t21 t22]]
  (if (not= t11 t21)
    (< t11 t21)
    (< t12 t22)))

;; Find the smallest location over the set of ranges, based on taking the
;; numbers in `:seeds` pair-wise. Note that because of the huge values of
;; `step` and `stop-step`, this can't be run on the example input from the
;; puzzle page.
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
