(ns advent-of-code.day10
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]))

;; This tests whether a point [y x] is valid within the [max-y max-x] range.
(defn- valid? [[y x] [max-y max-x]]
  (and (>= x 0)
       (>= y 0)
       (< x max-x)
       (< y max-y)))

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

(defn- build-struct [matrix]
  (let [max-y (count matrix)
        max-x (count (first matrix))]
    {:graph  (into {} (for [y (range max-y), x (range max-x)]
                        {[y x] (reaches (get-in matrix [y x])
                                        y x max-y max-x)}))
     :matrix matrix}))

(defn- has? [coll val]
  (if (some #(= val %) coll) true false))

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
(defn- erase-not [{:keys [seen matrix]}]
  (let [max-y (count matrix)
        max-x (count (first matrix))]
    (partition max-x (for [y (range max-y), x (range max-x)]
                       (if (seen [y x]) (get-in matrix [y x]) \.)))))

(defn- count-interior [line]
  ;; S->J is hard-coded. Necessary evil for now.
  (let [line' (str/replace (str/replace (str/replace line "S" "J")
                                        #"F-*7|L-*J" "")
                           #"F-*J|L-*7" "|")]
    (loop [[ch & chs] line', interior 0, ans 0]
      (cond
        (nil? ch) ans
        (= ch \|) (recur chs (inc interior) ans)
        ;; ch == \.
        :else     (recur chs interior (if (= ch \.)
                                        (+ ans (rem interior 2))
                                        ans))))))

(defn- count-area [matrix]
  (map #(count-interior (apply str %)) matrix))

(defn part-2
  "Day 10 Part 2"
  [input]
  (->> input
       u/to-matrix
       build-struct
       find-furthest
       erase-not
       count-area
       (reduce +)))
