(ns advent-of-code.day19
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]))

;; The first map gets the actual fn from a keyword, and the second maps the
;; string (from the regexp) to the keyword.
(def ^:private op-map {:< <, :> >})
(def ^:private kw-map {"<" :<, ">" :>})

;; Parse one rule/step from a workflow. Returns a vector of [key op value rule].
;; (My regexp's are often stricter than necessary, I don't need to be so
;; "defensive" in parsing AoC input.)
(defn- part-step [step]
  (let [[_ field op val to] (re-matches #"([xmas])([<>])(\d+):([a-zAR]+)" step)]
    (vector (keyword field) (kw-map op) (parse-long val) (keyword to))))

;; Parse a single workflow. Separate the name from the content and process the
;; content parts with the previous fn. Returns a single-key hash-map that will
;; be folded into a master hash-map with `into`.
(defn- parse-workflow [workflow]
  (let [[_ name content] (re-matches #"([a-z]+)\{(.*?)\}" workflow)
        parts            (str/split content #",")]
    (hash-map (keyword name)
              (concat (map part-step (drop-last parts))
                      (list [:last nil nil (keyword (last parts))])))))

;; Parsing each part is much easier, since each part always has all four values.
(defn- parse-part [part]
  (into {} (map hash-map [:x :m :a :s] (u/parse-out-longs part))))

;; Take the two blocks (workflows and parts) and parse each separately.
(defn- to-data [[workflows parts]]
  {:workflows (into {} (map parse-workflow (u/to-lines workflows)))
   :parts (map parse-part (u/to-lines parts))})

;; Evaluate a part against a single workflow. Loop over the steps, splitting
;; each into the four parts. If we reach the :last step, just return its `to`
;; target. Otherwise, perform the test. If the test passes, return the rule's
;; `to` target, otherwise `recur` with the remaining steps.
(defn- evaluate [part workflow]
  (loop [[step & steps] workflow]
    (let [[k op val to] step]
      (case k
        :last to
        (if ((op-map op) (k part) val) to (recur steps))))))

;; Run `part` through all the workflows as a pipeline. The loop only goes over
;; `workflow`, and the `recur` uses `evaluate` to determine the next workflow
;; to process. When the part reaches either :A or :R, return that value.
(defn- pipeline [workflows part]
  (loop [workflow :in]
    (case workflow
      (:A :R) workflow
      (recur (evaluate part (workflow workflows))))))

;; Predicate to determine of part `part` is accepted.
(defn- accepted? [workflows part]
  (= :A (pipeline workflows part)))

;; Filter all parts on those that are "accepted".
(defn- get-accepted [{:keys [workflows parts]}]
  (filter #(accepted? workflows %) parts))

;; Total the numbers for all accepted parts.
(defn- count-accepted [data]
  (reduce + (map #(apply + (vals %)) (get-accepted data))))

(defn part-1
  "Day 19 Part 1"
  [input]
  (->> input
       u/to-blocks
       to-data
       count-accepted))

;; Part 2 owes a lot to two implementations. The Perl one is at Topaz's
;; paste-bin, and the link is too long to include here. The other is from
;; erdos:
;; https://github.com/alexalemi/advent/blob/main/2023/clojure/p19.clj

;; Adjust the given range, based on the `op` and `val` parameters. The `op` is
;; a keyword-representation of the greater-than/less-than operations.
(defn- update-range [[lo hi] op val]
  (case op
    :> [[lo (inc val)] [(inc val) hi]]
    :< [[val hi] [lo val]]))

;; Process one rule of a workflow. Call the previous fn with the element of
;; `parts` that corresponds to key `k`. Returns two items: the updated `parts`
;; and a range-mapping of the target transition for this rule, with different
;; `parts` values.
(defn- do-rule [parts [k op val to]]
  (let [[old new] (update-range (k parts) op val)]
    [(assoc parts k old) [to (assoc parts k new)]]))

;; Process a single workflow over all the parts. Apply each part to the
;; workflow to tighten the ranges in `parts`. Returns new name/parts pairs for
;; addition to the queue used in `count-possible`, below.
(defn- do-workflow [rules parts]
  (let [default     (last (last rules))
        rules       (drop-last rules)
        [orig mods] (reduce (fn [[cur mods] rule]
                              (let [[old new] (do-rule cur rule)]
                                [old (conj mods new)]))
                            [parts []] rules)]
    (conj mods [default orig])))

;; Derive the count of all possible combinations, based on the workflows we are
;; given. Start out the queue with the :in workflow and parts in the full range
;; acceptable. Note that the upper-range is one higher, to make the range-math
;; a little easier. When a given path leads to :A, add the product of the
;; final ranges for the parts to `total`.
(defn- count-possible [workflows]
  (loop [queue [[:in {:x [1 4001], :m [1 4001], :a [1 4001], :s [1 4001]}]]
         total 0]
    (if-let [[name parts] (peek queue)]
      (case name
        :A (recur (pop queue)
                  (+ total (reduce * (map (fn [[lo hi]]
                                            (- hi lo)) (vals parts)))))
        :R (recur (pop queue) total)
        (recur (into (pop queue) (do-workflow (name workflows) parts)) total))
      total)))

(defn part-2
  "Day 19 Part 2"
  [input]
  (->> input
       u/to-blocks
       to-data
       :workflows
       count-possible))
