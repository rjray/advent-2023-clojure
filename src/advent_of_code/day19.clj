(ns advent-of-code.day19
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]))

(def ^:private op-map {:< <, :> >})
(def ^:private kw-map {"<" :<, ">" :>})

(defn- part-step [step]
  (let [[_ field op val to] (re-matches #"([xmas])([<>])(\d+):([a-zAR]+)" step)]
    (vector (keyword field) (kw-map op) (parse-long val) (keyword to))))

(defn- parse-workflow [workflow]
  (let [[_ name content] (re-matches #"([a-z]+)\{(.*?)\}" workflow)
        parts            (str/split content #",")]
    (hash-map (keyword name)
              (concat (map part-step (drop-last parts))
                      (list [:last nil nil (keyword (last parts))])))))

(defn- parse-part [part]
  (into {} (map hash-map [:x :m :a :s] (u/parse-out-longs part))))

(defn- to-data [[workflows parts]]
  {:workflows (into {} (map parse-workflow (u/to-lines workflows)))
   :parts (map parse-part (u/to-lines parts))})

(defn- evaluate [part workflow]
  (loop [[step & steps] workflow]
    (let [[k op val to] step]
      (case k
        :last to
        (if ((op-map op) (k part) val) to (recur steps))))))

(defn- pipeline [workflows part]
  (loop [workflow :in]
    (case workflow
      (:A :R) workflow
      (recur (evaluate part (workflow workflows))))))

(defn- accepted? [workflows part]
  (= :A (pipeline workflows part)))

(defn- get-accepted [{:keys [workflows parts]}]
  (filter #(accepted? workflows %) parts))

(defn- count-accepted [data]
  (reduce + (map #(apply + (vals %)) (get-accepted data))))

(defn part-1
  "Day 19 Part 1"
  [input]
  (->> input
       u/to-blocks
       to-data
       count-accepted))

(defn- update-range [[lo hi] op val]
  (case op
    :> [[lo (inc val)] [(inc val) hi]]
    :< [[val hi] [lo val]]))

(defn- do-rule [parts [k op val to]]
  (let [[old new] (update-range (k parts) op val)]
    [(assoc parts k old) [to (assoc parts k new)]]))

(defn- do-workflow [rules parts]
  (let [default     (last (last rules))
        rules       (drop-last rules)
        [orig mods] (reduce (fn [[cur mods] rule]
                              (let [[old new] (do-rule cur rule)]
                                [old (conj mods new)]))
                            [parts []] rules)]
    (conj mods [default orig])))

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
