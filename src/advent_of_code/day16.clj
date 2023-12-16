(ns advent-of-code.day16
  (:require [advent-of-code.utils :as u]))

;; This is used for easy changing of position based on the beam's direction.
(def ^:private delta {:n [-1 0]
                      :e [0 1]
                      :s [1 0]
                      :w [0 -1]})

;; This function takes the beam's position, direction, and the character it is
;; currently on. Based on the character and direction, it returns a new beam
;; representation (or two, if `ch` is one of the splitters). I could probably
;; do this more tightly with a mapping or two, or if there's a Clojure
;; equivalent of Rust's pattern matching that I don't yet know.
(defn- process-beam [pos dir ch]
  (case ch
    \. (list [pos dir])
    \- (case dir
         (:e :w) (list [pos dir])
         (:n :s) (list [pos :e] [pos :w]))
    \| (case dir
         (:n :s) (list [pos dir])
         (:w :e) (list [pos :n] [pos :s]))
    \\ (case dir
         :n (list [pos :w])
         :s (list [pos :e])
         :e (list [pos :s])
         :w (list [pos :n]))
    \/ (case dir
         :n (list [pos :e])
         :s (list [pos :w])
         :e (list [pos :n])
         :w (list [pos :s]))))

;; Trace a single beam from the initial point/direction. Count the number of
;; points in `matrix` that get charged. Note that `initial` starts OUTSIDE the
;; field, as the first thing done is to update the position.
(defn- trace [initial matrix]
  ;; Make a local `valid?` predicate based on the size of `matrix`.
  (letfn [(valid? [[y x]]
            (and (>= x 0)
                 (>= y 0)
                 (< x (count (first matrix)))
                 (< y (count matrix))))]
    ;; The sequence of "beams" will not be empty until the last beam has gone
    ;; off the edge of the field. The `seen` set tracks each time a point is
    ;; entered from each direction, since a second beam following a previous
    ;; path isn't going to change the count.
    (loop [[beam & beams] (list initial), seen #{}, charged #{}]
      (if (nil? beam)
        (count charged)
        (let [[pos dir] beam
              newpos    (mapv + pos (delta dir))]
          (if (and (valid? newpos) (not (seen (list newpos dir))))
            (recur (concat beams (process-beam newpos dir
                                               (get-in matrix newpos)))
                   (conj seen (list newpos dir))
                   (conj charged newpos))
            (recur beams seen charged)))))))

(defn part-1
  "Day 16 Part 1"
  [input]
  (->> input
       u/to-matrix
       (trace [[0 -1] :e])))

;; For part 2, this traces all beams from each edge-point going inward to the
;; field. This means that corner points like [0 0] are tested twice, once for
;; each direction.
(defn- trace-all [matrix]
  (let [max-y (count matrix)
        max-x (count (first matrix))]
    ;; I suspect there was probably a clever way to do this with just one `for`
    ;; block, using nested y/x ranges. But this was easier to do in a short
    ;; time.
    (flatten
     (concat
      (for [y (range max-y)]
        (list (trace [[y -1] :e] matrix) (trace [[y max-x] :w] matrix)))
      (for [x (range max-x)]
        (list (trace [[-1 x] :s] matrix) (trace [[max-y x] :n] matrix)))))))

(defn part-2
  "Day 16 Part 2"
  [input]
  (->> input
       u/to-matrix
       trace-all
       (apply max)))
