(ns advent-of-code.core
  (:require [advent-of-code.utils :as u]))

(defn -main
  "Used to dispatch tasks from the command line.

  Usage: lein run [ -b ] DAY PART

  where DAY and PART are integers selecting the day and part, respectively.

  If -b is given, load the 'bis' version of the day's code."
  [& args]
  (let [bis   (= (first args) "-b")
        day   (Integer/parseInt (if bis (second args) (first args)))
        part  (Integer/parseInt (if bis (nth args 2) (second args)))
        input (format "day%02d.txt" day)
        sub   (try (requiring-resolve
                    (symbol (format "advent-of-code.day%02d%s/part-%d"
                                    day (if bis "bis" "") part)))
                   (catch Exception _
                     (format "No%s fn found for day %d part %d."
                             (if bis " bis" "") day part)))]
    (cond
      (or (< day 1)
          (> day 25)) (.println *err* "Day out of range.")
      (or (< part 1)
          (> part 2)) (.println *err* "Part out of range.")
      (string? sub)   (.println *err* sub)
      :else           (let [[r t] (u/time-it (sub (u/read-input input)))]
                        (println r)
                        (println (format "\nTime: %.4fms" t))))))
