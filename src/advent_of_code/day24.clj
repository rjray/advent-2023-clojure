(ns advent-of-code.day24
  (:require [advent-of-code.utils :as u]
            [clojure.math.combinatorics :as comb]))

;; Parse all the lines into pairs of triples that represent the coordinate and
;; velocity of each hailstone.
(defn- to-coords [lines]
  (mapv (comp (partial mapv vec) (partial partition 3) u/parse-out-longs)
        lines))

;; This adapted from:
;; https://gitlab.com/davidsharick/advent-of-code-2023/-/blob/main/day24/day24.py
(defn- not-past [point line1 line2]
  (when (not (nil? (point 0)))
    (every? identity (for [d (range 2), [h0 h1] [line1 line2]]
                       (if (or (and (> (h1 d) 0) (> (point d) (h0 d)))
                               (and (< (h1 d) 0) (< (point d) (h0 d)))
                               (and (zero? (h1 d)) (= (point d) (h0 d))))
                         true false)))))

;; Find the intersection between to two points, ensuring that it is within the
;; bounding-box defined by `lo` and `hi`. Returns a `true` value, or `false` if
;; there is no intersection, the lines are parallel, or the intersection is in
;; the past.
(defn- intersect-within [lo hi [p1 v1] [p2 v2]]
  (let [c1 (-' (*' (v1 0) (p1 1)) (*' (v1 1) (p1 0)))
        c2 (-' (*' (v2 0) (p2 1)) (*' (v2 1) (p2 0)))
        b1 (-' (v1 0))
        b2 (-' (v2 0))
        a1 (v1 1)
        a2 (v2 1)
        d  (-' (*' a1 b2) (*' a2 b1))
        Px (if (zero? d) nil (/ (-' (*' b1 c2) (*' b2 c1)) d))
        Py (if (zero? d) nil (/ (-' (*' a2 c1) (*' a1 c2)) d))
        Fx (not-past [Px Py] [p1 v1] [p2 v2])]
    (and (not (zero? d)) (<= lo Px hi) (<= lo Py hi) Fx)))

;; Count the number of pairs that intercept within the given bounding box.
;; Might be able to rewrite this as a `filter` usage with `intersect-within`
;; as the predicate.
(defn- find-2d-intercepts [lo hi coords]
  (apply + (for [[a b] (comb/combinations (range (count coords)) 2)]
             (if (intersect-within lo hi (coords a) (coords b)) 1 0))))

(defn part-1
  "Day 24 Part 1"
  [input]
  (->> input
       u/to-lines
       to-coords
       (find-2d-intercepts 200000000000000 400000000000000)))

;; Having almost no idea of where to start, I'm basing this on the same code as
;; the `not-past` above. This part of the Python, in turn, credits the following
;; reddit comments:
;;
;; https://old.reddit.com/r/adventofcode/comments/18pptor/2023_day_24_part_2java_is_there_a_trick_for_this/keps780/
;; https://old.reddit.com/r/adventofcode/comments/18pptor/2023_day_24_part_2java_is_there_a_trick_for_this/kepxbew/

;; Create the vector of three sets that track the invalid values for the goal
;; velocity in each of the three axes.
(defn- make-invalid-sets [coords]
  (reduce (fn [acc [d rng]]
            (update-in acc [d] into rng))
          [#{} #{} #{}]
          (for [l1 coords, l2 coords
                :when (not= l1 l2)
                d (range 3)
                :when (and (> (get-in l1 [0 d]) (get-in l2 [0 d]))
                           (> (get-in l1 [1 d]) (get-in l2 [1 d])))]
            (list d (range (get-in l2 [1 d]) (get-in l1 [1 d]))))))

;; Find the intersection point of the lines for the two hailstones passed in.
;; Tried to re-use the intersection code from part 1, but here we need the
;; actual (X, Y) pair, and we wouldn't use the lo/hi values at all.
(defn- intersection-point [[p1 v1] [p2 v2]]
  (let [c1 (-' (*' (v1 0) (p1 1)) (*' (v1 1) (p1 0)))
        c2 (-' (*' (v2 0) (p2 1)) (*' (v2 1) (p2 0)))
        b1 (-' (v1 0))
        b2 (-' (v2 0))
        a1 (v1 1)
        a2 (v2 1)
        d  (-' (*' a1 b2) (*' a2 b1))
        Px (if (zero? d) nil (/ (-' (*' b1 c2) (*' b2 c1)) d))
        Py (if (zero? d) nil (/ (-' (*' a2 c1) (*' a1 c2)) d))]
    (when Px [(double Px) (double Py)])))

;; Test for a "good" intersection between the goal-point and the passed-in
;; hailstone value.
(defn- good-intersect [goal hs]
  (loop [[d & ds] (range 3), good true]
    (cond
      (nil? d) good

      (and (zero? (get-in hs [1 d]))
           (= (bigint (goal d)) (get-in hs [0 d])))
      (recur ds good)

      (not= 0 (bigint (mod (- (goal d) (get-in hs [0 d]))
                           (get-in hs [1 d]))))
      (recur ds false)

      ;; This looks redundant with regards to the first clause, but that first
      ;; clause is there to prevent evaluation of the second clause (when the
      ;; first is true).
      :else
      (recur ds good))))

;; Find the sum of the (X, Y, Z) coordinates of the goal value, if a fitting
;; goal triple can be found from this list of values in `nh`. If no fit is
;; found, returns 0.
(defn- find-position-sum [nh]
  (let [hs1 (first nh)
        hs2 (second nh)
        ipt (intersection-point hs1 hs2)]
    (if (and ipt
             (not-past ipt hs1 hs2)
             (= 0.0 (mod (ipt 0) 1))
             (= 0.0 (mod (ipt 1) 1)))
      (let [t0 (quot (-' (ipt 0) (get-in hs1 [0 0])) (get-in hs1 [1 0]))
            t1 (quot (-' (ipt 0) (get-in hs2 [0 0])) (get-in hs2 [1 0]))
            z0 (+' (get-in hs1 [0 2]) (*' (get-in hs1 [1 2]) t0))
            z1 (+' (get-in hs2 [0 2]) (*' (get-in hs2 [1 2]) t1))]
        (if (= z0 z1)
          (let [goal (conj ipt z0)]
            (loop [[hs3 & hr] (drop 2 nh), value 0]
              (if (nil? hs3)
                value
                (if (good-intersect goal hs3)
                  (recur () (bigint (apply +' goal)))
                  (recur hr value)))))
          0))
      0)))

;; Solve for the position the rock needs to be at. Does a three-level loop over
;; the three axes for the velocity, skipping any that are not valid. For each
;; triple that gets through the checks, create a new list of the hailstone
;; coordinates structures that are tuned to the potential velocity. Call the
;; `find-position-sum` function on the new list, filter out all zeroes that
;; result, and take the answer.
(defn- solve-for-position [coords]
  (let [invalid (make-invalid-sets coords)]
    (first
     (filter pos?
             (for [x (range -500 501) :when (not ((invalid 0) x))
                   y (range -500 501) :when (not ((invalid 1) y))
                   z (range -500 501) :when (not ((invalid 2) z))
                   :let [newh (for [[p v] coords]
                                [p [(- (v 0) x) (- (v 1) y) (- (v 2) z)]])]]
               (find-position-sum newh))))))

(defn part-2
  "Day 24 Part 2"
  [input]
  (->> input
       u/to-lines
       to-coords
       solve-for-position))
