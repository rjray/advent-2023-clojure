(ns advent-of-code.day22
  (:require [advent-of-code.utils :as u]
            [clojure.set :as set]))

;; I got lost with a host of off-by-one errors and overlapping representations
;; of the objects. Correcting my errors owes a lot to Alex Alemi:
;; https://github.com/alexalemi/advent/blob/main/2023/clojure/p22.clj

(defn- to-forms [lines]
  (zipmap (range)
          (for [line lines]
            (apply vector (partition 3 (u/parse-out-longs line))))))

(defn- get-form [[[x0 y0 z0] [x1 y1 z1]]]
  (for [x (range x0 (inc x1)), y (range y0 (inc y1)), z (range z0 (inc z1))]
    [x y z]))

(defn- below-squares [[[x0 y0 z0] [x1 y1 _]]]
  (for [x (range x0 (inc x1)), y (range y0 (inc y1))]
    [x y (dec z0)]))

(defn- lower-form [[[x0 y0 z0] [x1 y1 z1]]]
  [[x0 y0 (dec z0)] [x1 y1 (dec z1)]])

(defn- move-down [filled form]
  (letfn [(supported-by [pos] (or (filled pos) (when (zero? (last pos)) true)))]
    (loop [form form]
      (let [under-us (keep supported-by (below-squares form))]
        (if (seq under-us)
          [form (set under-us)]
          (recur (lower-form form)))))))

(defn- find-supports [forms]
  (loop [queue (sort-by (comp last first val) forms)
         placed {}
         filled {}
         supports {}]
    (if-let [[label form] (first queue)]
      (let [[lowered-form supporting] (move-down filled form)]
        (recur (next queue)
               (assoc placed label lowered-form)
               (into filled (zipmap (get-form lowered-form) (repeat label)))
               (assoc supports label supporting)))
      supports)))

(defn- removeable [supports]
  (reduce disj
          (set (keys supports))
          (map first (filter #(= 1 (count %)) (vals supports)))))

(defn part-1
  "Day 22 Part 1"
  [input]
  (->> input
       u/to-lines
       to-forms
       find-supports
       removeable
       count))

(defn- update-vals-args [orig f & args]
  (reduce-kv (fn [acc k v]
               (let [v' (apply f v args)]
                 (if (empty? v') acc (assoc acc k v'))))
             {} orig))

(defn- find-removed [supports which]
  (loop [supports supports, queue #{which}, removed #{}]
    (if-let [cur (first queue)]
      (recur (update-vals-args supports disj cur)
             (into (disj queue cur)
                   (reduce-kv (fn [acc k v]
                                (if (= v #{cur}) (conj acc k) acc))
                              #{} supports))
             (conj removed cur))
      removed)))

(defn- get-removed-count [forms]
  (let [supports   (find-supports forms)
        supporting (set/difference (set (keys supports))
                                   (removeable supports))]
    (transduce (map (comp dec count (partial find-removed supports)))
               + supporting)))

(defn part-2
  "Day 22 Part 2"
  [input]
  (->> input
       u/to-lines
       to-forms
       get-removed-count))
