(ns advent-of-code.day09
  (:require [advent-of-code.utils :as u]))

(defn- get-new-term [rows]
  (last
   (last
    (reduce (fn [acc row]
              (if (empty? acc)
                ;; We know the first row we look at is all 0's
                (conj acc (concat row (list 0)))
                (conj acc (concat row (list (+ (last row)
                                               (last (acc
                                                      (dec (count acc))))))))))
            [] rows))))

(defn- extrapolate [sequence]
  (loop [rows (list sequence), curr-row sequence]
    (let [next-row (map #(apply - (reverse %)) (partition 2 1 curr-row))]
      (if (zero? (reduce + next-row))
        (get-new-term (cons next-row rows))
        (recur (cons next-row rows) next-row)))))

(defn part-1
  "Day 09 Part 1"
  [input]
  (->> input
       u/to-lines
       (map u/parse-out-longs)
       (map extrapolate)
       ;;(reduce +)
       ))

(defn part-2
  "Day 09 Part 2"
  [input]
  (->> input))
