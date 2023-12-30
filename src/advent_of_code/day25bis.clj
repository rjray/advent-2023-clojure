(ns advent-of-code.day25bis
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
       vals
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

;; Select at random an edge from `edata`. Then do a contraction of the two
;; vertices of this edge (`v1` and `v2`) returning new versions of vertex data
;; and edge data. Edge data may now have duplicate entries, but any self-loops
;; will have been removed.
(defn- contract [vdata edata]
  (let [[v1 v2] (seq (rand-nth edata))
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

;; Take the graph structure passed in and invoke `karger-min-cut-once` some
;; number of times. Return the first result for which `:size` is 3.
(defn- karger-min-cut [vdata]
  (let [edata (edges vdata)]
    (first (filter #(= 3 (:size %))
                   (pmap (partial karger-min-cut-once vdata edata)
                         (iterate inc 1))))))

;; Get the answer for the puzzle. Return a list of the iteration number
;; followed by the product of the sizes of the two halves.
(defn- get-answer [{:keys [iter left right]}]
  (list iter (* (count left) (count right))))

(defn part-1
  "Day 25 Part 1"
  [input]
  (->> input
       u/to-lines
       to-graph
       number-graph
       karger-min-cut
       get-answer))

;; This version of the solution comes from the Perl code that I found through
;; https://www.reddit.com/r/adventofcode/comments/18qqmfb/2023_day_25_want_to_see_how_your_algorithm_scales/
;; on reddit. From there, I went to the author's comment on the day's solution
;; thread: https://www.reddit.com/r/adventofcode/comments/18qbsxs/2023_day_25_solutions/keug4yl/
;; That comment has the link to the Perl code.

;; Slight change to the previous `to-graph`, to also return the first node.
(defn- to-graph2 [lines]
  (list (first (str/split (first lines) #":" 2))
        (update-vals
         (reduce (fn [graph [from & to]]
                   (loop [[to & tos] to, graph graph]
                     (if (nil? to)
                       graph
                       (recur tos (update (update graph from conj to)
                                          to conj from)))))
                 {} (map #(str/split % #"\W+") lines))
         set)))

;; Iterate over the nodes by using a queue updated with each node's neighbors.
;; Return the count of nodes, the last node, and the initial positions.
(defn- mark-and-place [first-node graph]
  (loop [queue (list first-node), positions {}, count 0, last-node nil]
    (if-let [node (first queue)]
      (if (find positions node)
        (recur (rest queue) positions count node)
        (recur (concat (rest queue) (graph node))
               (assoc positions node (inc count))
               (inc count)
               node))
      (list count last-node positions))))

;; This is the inner foreach loop from the `findCut` function in the Perl code.
;; It iterates over the neighbors of `node`, calculating the average distance
;; to `node` itself and noting the largest gap (and the edge that has that gap).
;; Returns a tuple of the avg distance, maximum gap and maximum edge. The
;; latter two values will be passed into later calls here.
(defn- neighbors [graph pos node max-gap max-edge last-node]
  (let [neighbors (graph node)
        node-pos  (pos node)]
    (loop [[n & ns] neighbors, avg 0, max-gap max-gap, max-edge max-edge]
      (if (nil? n)
        (list (double (/ avg (count neighbors))) max-gap max-edge)
        (let [npos (pos n)
              gap  (abs (- npos node-pos))]
          (if (and (> gap max-gap) (not= n last-node))
            (recur ns (+ avg npos) gap (list node n))
            (recur ns (+ avg npos) max-gap max-edge)))))))

;; This is the outer foreach loop from `findCut`. It loops over the nodes of
;; the graph, making adjustments to positions map `pos`, the movement range
;; `mvmt`, and `max-gap` and `max-edge`. Returns a tuple of the updated `pos`,
;; `mvmt`, and the `max-edge` value.
(defn- adjust [graph pos first-node last-node]
  (loop [[node & nodes] (keys graph), pos pos, mvmt 0, max-gap 0, max-edge ()]
    (cond
      (nil? node) (list pos mvmt max-edge)
      (or (= node first-node)
          (= node last-node)) (recur nodes pos mvmt max-gap max-edge)
      :else
      (let [[avg new-gap new-edge] (neighbors graph pos node max-gap max-edge
                                              last-node)]
        (recur nodes
               (assoc pos node avg)
               (max (abs (- (pos node) avg)) mvmt)
               new-gap
               new-edge)))))

;; The implementation of `findCut` from the Perl code. This finds the edge that
;; maximizes the gap to its neighbors and returns that (along with the updated
;; position data, `pos`). Note that having the `mvmt` metric reach or drop
;; below 0.05 was later replaced with a maximum iteration length of 3 in the
;; original author's code.
(defn- find-cut [graph positions first-node last-node]
  (loop [[pos mvmt max-edge] (list positions 100 nil)]
    (if (<= mvmt 0.05)
      (list max-edge pos)
      (recur (adjust graph pos first-node last-node)))))

;; Update `graph` to remove the given edge. Since the graph is bi-directional,
;; this has to be removed from both ends.
(defn- delete-edges [graph src dst]
  (update-in (update-in graph [src] disj dst) [dst] disj src))

;; Find a cut of `graph` of size `cuts`. Once found, derive the product
;; specified by the puzzle.
(defn- split-graph [cuts [first-node graph]]
  (let [node-count              (count (keys graph))
        [_ last-node positions] (mark-and-place first-node graph)]
    (loop [iter 0, graph graph, positions positions]
      (if (= iter cuts)
        (let [[final-count] (mark-and-place first-node graph)]
          (* final-count (- node-count final-count)))
        (let [[[src dst] positions] (find-cut graph positions
                                              first-node last-node)]
          (recur (inc iter) (delete-edges graph src dst) positions))))))

(defn part-2
  "Day 25 Part 2"
  [input]
  (->> input
       u/to-lines
       to-graph2
       (split-graph 3)))
