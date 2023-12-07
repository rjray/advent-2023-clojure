(ns advent-of-code.day05bis
  (:require [advent-of-code.utils :as u]))

;; Parse one block. The blocks were created by u/to-blocks, this will handle a
;; single one. Splits into lines, drops the name line, and parses the rest.
;; Sorts the resulting tuples for easier searching later.
(defn- parse-map [block]
  (sort-by first
           (reduce (fn [mapping line]
                     (let [[to from length] (u/parse-out-longs line)]
                       (cons (list from to length) mapping)))
                   () (drop 1 (u/to-lines block)))))

;; Create the 7 maps along with the list of seeds.
(defn- create-maps [[seeds & blocks]]
  {:seeds (u/parse-out-longs seeds), :maps (map parse-map blocks)})

;; Get the value for the seed `val`, using the values from a mapping.
(defn- get-val [[base off len] val]
  (if (>= val (+ base len)) val (+ (- val base) off)))

;; Find a value in a given mapping, using the rules given in the puzzle.
(defn- find-in-mapping [val this-map]
  (if-let [row (last (take-while #(<= (first %) val) this-map))]
    (get-val row val)
    val))

;; Using `find-in-mapping`, get the location for a given `seed`.
(defn- find-location [maps seed]
  (reduce find-in-mapping seed maps))

;; Find the locations for all seeds read from the input data.
(defn- find-locations [{:keys [seeds maps]}]
  (map #(find-location maps %) seeds))

(defn part-1
  "Day 05 Part 1"
  [input]
  (->> input
       u/to-blocks
       create-maps
       find-locations
       (reduce min)))

;; Basing this on the Python code at:
;; https://github.com/Leftfish/Advent-of-Code-2023/blob/main/05/d05.py

;; Find the smallest location in the given seed-range, using sampling based on
;; the value of `step`.
(defn- find-smallest-in-range [rng maps step]
  (first (sort-by first
                  (map #(list (find-location maps %) %)
                       (range (first rng) (apply + rng) step)))))

;; This is a quick-and-dirty "copy" of Python's ability to use `<` on tuples.
(defn- our< [[t11 t12] [t21 t22]]
  (if (not= t11 t21) (< t11 t21) (< t12 t22)))

;; Find the smallest location over the set of ranges, based on taking the
;; numbers in `:seeds` pair-wise. Note that because of the huge values of
;; `step` and `stop-step`, this can't be run on the example input from the
;; puzzle page.
(defn- find-smallest-in-ranges [{:keys [seeds maps]}]
  (let [seed-ranges (partition 2 seeds), stop-step 10000]
    (loop [step 100000000, minimum -1, seed-ranges seed-ranges]
      (if (< step stop-step)
        (let [final (list (- (ffirst seed-ranges) step) (* step 10))]
          (first (find-smallest-in-range final maps 1)))
        ;; else
        (let [range-mins  (map #(find-smallest-in-range % maps step)
                               seed-ranges)
              range-min   (reduce (fn [mins rm]
                                    (if (or (= -1 mins)
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
