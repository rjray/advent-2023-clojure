(ns advent-of-code.day20
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]))

(defn- parse-line [line]
  (let [[module connections] (str/split line #"\s+->\s+")
        connections          (mapv keyword (str/split connections #", "))
        [mod-type module]    (if (= module "broadcaster")
                               [:B :broadcaster]
                               [(keyword (subs module 0 1))
                                (keyword (subs module 1))])]
    (hash-map module {:type mod-type :conn connections})))

(defn- to-machine [lines]
  (into {} (map parse-line lines)))

(defn- find-inputs [machine key]
  (for [part (keys machine)
        :when (seq (filter #{key} (get-in machine [part :conn])))]
    part))

(defn- assign-memory [machine]
  (reduce (fn [machine key]
            (let [part (key machine)]
              (assoc-in machine [key :memory]
                        (case (:type part)
                          :B nil
                          :% :off
                          :& (zipmap (find-inputs machine key) (repeat :lo))))))
          machine (keys machine)))

(defn- update-con [machine target]
  (if (get-in machine [:con target])
    machine
    (assoc-in machine [:con target] (:n machine))))

(defn- send-react [machine target signal from]
  (let [part (get machine target)]
    (if (nil? part)
      (list machine [:BLANK])
      (case (:type part)
        :B (cons machine (map #(vector % signal :B) (:conn part)))
        :% (if (= signal :hi)
             (list machine [:BLANK])
             (let [cur (:memory part)]
               (if (= cur :off)
                 (cons (assoc-in machine [target :memory] :on)
                       (map #(vector % :hi target) (:conn part)))
                 (cons (assoc-in machine [target :memory] :off)
                       (map #(vector % :lo target) (:conn part))))))
        :& (let [machine (assoc-in machine [target :memory from] signal)
                 part    (target machine)]
             (if (every? #{:hi} (vals (part :memory)))
               (cons machine
                     (map #(vector % :lo target) (:conn part)))
               (cons (update-con machine target)
                     (map #(vector % :hi target) (:conn part)))))))))

(defn- press-once [machine]
  (loop [queue (into clojure.lang.PersistentQueue/EMPTY [[:broadcaster :lo]])
         machine machine]
    (if (= :lo (get-in machine [:sinks :rx]))
      machine
      (let [[target signal from] (peek queue)]
        (case target
          nil    machine
          :BLANK (recur (pop queue) machine)
          (let [[machine & new] (send-react machine target signal from)]
            (recur (into (pop queue) new)
                   (update-in machine [:pulses signal] inc))))))))

(defn- count-pushes [n machine]
  (loop [machine (assoc machine :pulses {:lo 0, :hi 0} :con {} :n 0)]
    (if (= n (:n machine))
      (* (get-in machine [:pulses :hi]) (get-in machine [:pulses :lo]))
      (recur (press-once (update machine :n inc))))))

(defn part-1
  "Day 20 Part 1"
  [input]
  (->> input
       u/to-lines
       to-machine
       assign-memory
       (count-pushes 1000)))

;; For part 2, I could work backwards from :rx and determine what conjunction
;; feeds it, then what feeds that conjunction. But I'm going to hard-code it
;; for now, out of laziness.

(def ^:private gates (list :lh :fk :ff :mm))

(defn- all-gates [machine]
  (map #(get-in machine [:con %] 0) gates))

(defn- count-min-pushes [machine]
  (loop [machine (assoc machine :pulses {:lo 0, :hi 0} :con {} :n 0)]
    (let [gate-vals (all-gates machine)] 
      (if (some zero? gate-vals)
        (recur (press-once (update machine :n inc)))
        (reduce u/lcm gate-vals)))))

(defn part-2
  "Day 20 Part 2"
  [input]
  (->> input
       u/to-lines
       to-machine
       assign-memory
       count-min-pushes))
