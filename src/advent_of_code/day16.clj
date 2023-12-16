(ns advent-of-code.day16
  (:require [advent-of-code.utils :as u]))

(def ^:private delta {:n [-1 0]
                      :e [0 1]
                      :s [1 0]
                      :w [0 -1]})

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

(defn- trace [initial matrix]
  (letfn [(valid? [[y x]]
            (and (>= x 0)
                 (>= y 0)
                 (< x (count (first matrix)))
                 (< y (count matrix))))]
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

(defn- trace-all [matrix]
  (let [max-y (count matrix)
        max-x (count (first matrix))]
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
