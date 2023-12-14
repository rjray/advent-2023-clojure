(ns advent-of-code.day14
  (:require [advent-of-code.utils :as u]
            [clojure.set :as set]))

(defn- count-rocks [row]
  (count (filter #(= % \O) row)))

(defn- count-load [m]
  (reduce + (map #(* %2 (count-rocks %1))
                 (reverse m) (range 1 (inc (count m))))))

(defn- roll-left [row]
  (loop [row row]
    (let [row' (reduce (fn [v i]
                         (let [c1 (v i), c0 (v (dec i))]
                           (if (and (= c1 \O) (= c0 \.))
                             (assoc v (dec i) \O i \.)
                             v)))
                       row (range 1 (count row)))]
      (if (= row row') row (recur row')))))

(defn- roll-right [row]
  (loop [row row]
    (let [row' (reduce (fn [v i]
                         (let [c1 (v i), c0 (v (inc i))]
                           (if (and (= c1 \O) (= c0 \.))
                             (assoc v (inc i) \O i \.)
                             v)))
                       row (range (- (count row) 2) -1 -1))]
      (if (= row row') row (recur row')))))

(defn- tilt-north [matrix]
  (u/transpose (mapv roll-left (u/transpose matrix))))

(defn- tilt-south [matrix]
  (u/transpose (mapv roll-right (u/transpose matrix))))

(defn- tilt-west [matrix]
  (mapv roll-left matrix))

(defn- tilt-east [matrix]
  (mapv roll-right matrix))

(defn part-1
  "Day 14 Part 1"
  [input]
  (->> input
       u/to-matrix
       (tilt-north)
       count-load))

(defn- spin-once [matrix]
  (->> matrix
       tilt-north
       tilt-west
       tilt-south
       tilt-east))

(defn- find-nth [seen location n target]
  (let [cycle   (- n location)
        mapping (set/map-invert seen)
        remain  (mod (- target n) cycle)]
    (mapping (+ remain location))))

(defn- spin-cycle [target matrix]
  (loop [[m & ms] (iterate spin-once matrix), idx 0, seen {}]
    (if (seen m)
      (find-nth seen (seen m) idx target)
      (recur ms (inc idx) (assoc seen m idx)))))

(defn part-2
  "Day 14 Part 2"
  [input]
  (->> input
       u/to-matrix
       (spin-cycle 1000000000)
       count-load))
