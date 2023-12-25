(ns advent-of-code.day25
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]
            [clojure.set :as set]))

;; Turn the lines into a basic map "graph" in which the nodes are the keys, and
;; the value at each key is a set of the other nodes it reaches.
(defn- to-graph [lines]
  (update-vals (reduce (fn [graph [from & to]]
                         (loop [[to & tos] to, graph graph]
                           (if (nil? to)
                             graph
                             (recur tos (update (update graph from conj to)
                                                to conj from)))))
                       {} (map #(str/split % #"\W+") lines))
               set))

;; For this case, we need the graph nodes/edges to be expressed as numbers,
;; with each edge-set being a set of sets (pairs). This does that by first
;; creating a table that maps each node to a numerical index, then applying
;; that table to the basic graph that `to-graph` produced.
(defn- number-graph [graph]
  (let [table (zipmap (sort (keys graph)) (iterate inc 0))]
    (reduce (fn [g [k v]]
              (assoc g #{(table k)}
                     (set (map #(set [#{(table k)} #{(table %)}]) v))))
            {} graph)))

;; Create the edges data from the vertices array. This removes duplicates, so
;; it is only used when setting up the data, not when doing a contraction.
(defn- edges [vdata]
  (->> vdata
       (map last)
       (apply concat)
       (set)
       (seq)))

;; "Mutate" the list of edges by creating a new list in which any occurrences
;; of `u` or `v` are replaced with `uv`. Filter out self-loops by counting the
;; size of each edge.
(defn- mutate-edges [e u v uv]
  (let [test-elt #{u v}]
    (filter #(= 2 (count %))
            (map (fn [elt]
                   (let [[e1 e2] (seq elt)]
                     (set (list (if (test-elt e1) uv e1)
                                (if (test-elt e2) uv e2)))))
                 e))))

;; Select at random an edge `e` from `edata`. Then do a contraction of the two
;; vertices of `e`, returning new versions of vertex data and edge data. Edge
;; data may now have duplicate entries, but any self-loops will have been
;; removed.
(defn- contract [vdata edata]
  (let [e       (rand-nth edata)
        [v1 v2] (seq e)
        vnew    (set/union v1 v2)
        enew    (concat (vdata v1) (vdata v2))]
    (list (assoc (dissoc vdata v1 v2) vnew (mutate-edges enew v1 v2 vnew))
          (mutate-edges edata v1 v2 vnew))))

;; Run the Karger Minimum Cut algorithm once on the data provided. Returns a
;; map structure with the cut size, iteration number, and sets of nodes on each
;; side.
(defn- karger-min-cut-once [vdata edata n]
  (loop [[vdata edata] (list vdata edata)]
    (cond
      (= 2 (count vdata)) {:iter  n
                           :size  (count edata)
                           :left  (ffirst vdata)
                           :right (first (last vdata))}
      :else               (recur (contract vdata edata)))))

;; Run the actual algorithm (karger-min-cut-once) iters times. Collect all the
;; results into a list. Note the use of `pmap`, as every iteration is fully
;; independent of the others. Also, the `doall` forces all of the sequence to
;; be realized, without which the program doesn't always exit properly after
;; returning the answer.
(defn- do-karger-min-cut [vdata edata iters]
  (doall (pmap #(karger-min-cut-once vdata edata %) (range iters))))

;; Take the graph structure passed in and invoke `do-karger-min-cut` some
;; number of times. Sort the results by the size of the cut and return the
;; first in the list. The goal is for size to be 3, so the `iters` value was
;; gradually increased until we got a size-3 cut.
(defn- karger-min-cut [graph]
  (let [vdata graph
        edata (edges vdata)
        iters 128]
    (first (sort #(compare (:size %1) (:size %2))
                 (do-karger-min-cut vdata edata iters)))))

;; Get the answer for the puzzle. Return a list of the cut-size followed by the
;; product of the sizes of the two halves.
(defn- get-answer [cut]
  (list (:size cut) (* (count (:left cut)) (count (:right cut)))))

(defn part-1
  "Day 25 Part 1"
  [input]
  (->> input
       u/to-lines
       to-graph
       number-graph
       karger-min-cut
       get-answer))

(defn part-2
  "Day 25 Part 2"
  [input]
  "Congrats! You should have all 50 stars by now!")
