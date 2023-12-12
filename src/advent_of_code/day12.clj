(ns advent-of-code.day12
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]
            [clojure.math.combinatorics :as comb]))

;; This was the original solution to part 1, a brute-force approach that was
;; useless for part 2.

(comment
  (defn- valid? [pat nums]
    (let [groups (map count (re-seq #"#+" (str/join pat)))]
      (= groups nums)))

  (defn- fill-in [pat opts]
    (loop [[ch & chs] pat, opts opts, new []]
      (cond
        (nil? ch) new
        (= ch \?) (recur chs (rest opts) (conj new (first opts)))
        :else     (recur chs opts (conj new ch)))))

  (defn- find-arrangements [mult line]
    (let [[pat nums] (str/split line #"\s+")
          pat        (str/join "?" (repeat mult pat))
          nums       (str/join "," (repeat mult nums))
          nums       (u/parse-out-longs nums)
          slots      (count (filter #(= \? %) pat))
          options    (comb/selections [\. \#] slots)]
      (count (filter #(valid? (fill-in pat %) nums) options)))))

;; A dynamic-programming approach, based somewhat on
;; https://github.com/jonathanpaulson/AdventOfCode/blob/master/2023/12.py and
;; https://github.com/ypisetsky/advent-of-code-2023/blob/main/day12.py

(def dyn-prog
  (memoize
   (fn [pat cnts ppos c-cnt c-pos]
     (cond
       (= ppos (count pat)) (if (= c-pos (count cnts)) 1 0)
       
       (= (pat ppos) \#)    (dyn-prog pat cnts (inc ppos) (inc c-cnt) c-pos)
       
       (or (= (pat ppos) \.)
           (= c-pos (count cnts)))
       (cond
         (and (< c-pos (count cnts))
              (= c-cnt (cnts c-pos))) (dyn-prog pat cnts
                                                (inc ppos) 0 (inc c-pos))
         (zero? c-cnt)                (dyn-prog pat cnts (inc ppos) 0 c-pos)
         :else                        0)

       :else
       (let [hcnt (dyn-prog pat cnts (inc ppos) (inc c-cnt) c-pos)]
         (cond
           (= c-cnt (cnts c-pos)) (+ hcnt (dyn-prog pat cnts
                                                    (inc ppos) 0 (inc c-pos)))
           (zero? c-cnt)          (+ hcnt (dyn-prog pat cnts
                                                    (inc ppos) 0 c-pos))
           :else                  hcnt))))))

(defn- find-arrangements [mult line]
  (let [[pat nums] (str/split line #"\s+")
        pat        (conj (vec (str/join "?" (repeat mult pat))) \.)
        nums       (vec (u/parse-out-longs (str/join "," (repeat mult nums))))]
    (dyn-prog pat nums 0 0 0)))

(defn part-1
  "Day 12 Part 1"
  [input]
  (->> input
       u/to-lines
       (map (partial find-arrangements 1))
       (reduce +)))

(defn part-2
  "Day 12 Part 2"
  [input]
  (->> input
       u/to-lines
       (map (partial find-arrangements 5))
       (reduce +)))
