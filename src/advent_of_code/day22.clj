(ns advent-of-code.day22
  (:require [advent-of-code.utils :as u]
            [clojure.set :as set]))

;; I got lost with a host of off-by-one errors and overlapping representations
;; of the objects. Correcting my errors owes a lot to Alex Alemi:
;; https://github.com/alexalemi/advent/blob/main/2023/clojure/p22.clj

;; Convert the lines of object boundaries into two-element vectors that describe
;; the two end-points.
(defn- to-forms [lines]
  (zipmap (range)
          (for [line lines]
            (apply vector (partition 3 (u/parse-out-longs line))))))

;; Get a seq of all the "blocks" in a given form. Note that only one of the
;; three ranges in the `for`-comprehension will actually be a loop-- the other
;; two axes' values will be equal. We just don't know which two.
(defn- get-form [[[x0 y0 z0] [x1 y1 z1]]]
  (for [x (range x0 (inc x1)), y (range y0 (inc y1)), z (range z0 (inc z1))]
    [x y z]))

;; Get a seq of the squares that are exactly one z-level below the form's
;; current coordinates.
(defn- below-squares [[[x0 y0 z0] [x1 y1 _]]]
  (for [x (range x0 (inc x1)), y (range y0 (inc y1))]
    [x y (dec z0)]))

;; Lower the coordinates of the given form's two endpoints by 1 in the z-axis.
(defn- lower-form [[[x0 y0 z0] [x1 y1 z1]]]
  [[x0 y0 (dec z0)] [x1 y1 (dec z1)]])

;; Lower the given form as much as it can, based on the squares known to hold
;; blocks based on the `filled` map.
(defn- move-down [filled form]
  (letfn [(supported-by [pos] (or (filled pos) (when (zero? (last pos)) true)))]
    (loop [form form]
      (let [under-us (keep supported-by (below-squares form))]
        (if (seq under-us)
          [form (set under-us)]
          (recur (lower-form form)))))))

;; Find the form(s) that currently support each form in the sequence `forms`.
;; Produces a map of the form-index-numbers mapped to a `set` instance of the
;; forms that are supporting that one.
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

;; Determine which forms can be removed without causing any others to fall.
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

;; Part 2 went much worse for me than part 1. This is much more like the source
;; solution than part 1 was.

;; A utility for essentially doing `update-vals` but with extra arguments to
;; the updating function `f`. If `args` is empty/nil, then it basically behaves
;; just like `update-vals`.
(defn- update-vals-args [orig f & args]
  (reduce-kv (fn [acc k v]
               (let [v' (apply f v args)]
                 (if (empty? v') acc (assoc acc k v'))))
             {} orig))

;; For the form indexed by `which`, find the set of other forms that would fall
;; if `which` is removed.
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

;; Get the accumulated count of the individual fall-counts for each form that
;; is not already known to not affect any others (the output from part 1, here
;; the declaration of `supports` in the `let`-binding).
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
