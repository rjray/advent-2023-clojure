# Breakdown of Files

Jump to day: [1](#day01clj)&nbsp;|&nbsp;[2](#day02clj)&nbsp;|&nbsp;[3](#day03clj)&nbsp;|&nbsp;[4](#day04clj)&nbsp;|&nbsp;[5](#day05clj)&nbsp;|&nbsp;[6](#day06clj)&nbsp;|&nbsp;[7](#day07clj)&nbsp;|&nbsp;[8](#day08clj)&nbsp;|&nbsp;[9](#day09clj)&nbsp;|&nbsp;[10](#day10clj)&nbsp;|&nbsp;[11](#day11clj)&nbsp;|&nbsp;[12](#day12clj)&nbsp;|&nbsp;[13](#day13clj)&nbsp;|&nbsp;[14](#day14clj)&nbsp;|&nbsp;[15](#day15clj)&nbsp;|&nbsp;[16](#day16clj)&nbsp;|&nbsp;[17](#day17clj)&nbsp;|&nbsp;[18](#day18clj)&nbsp;|&nbsp;[19](#day19clj)&nbsp;|&nbsp;[20](#day20clj)&nbsp;|&nbsp;[21](#day21clj)&nbsp;|&nbsp;[22](#day22clj)&nbsp;|&nbsp;[23](#day23clj)&nbsp;|&nbsp;[24](#day24clj)&nbsp;|&nbsp;[25](#day25clj)

Here is a breakdown of the various files in this directory. Files with names of
the form `dayNN.clj` represent the code actually used to solve the problems
(with some tweaking done using a static analysis plug-in for Leiningen). Files
with `bis` in the name are modified/tuned versions of the given original day.
(If you see comments in a file, I can usually promise you they were added after
the fact.)

The numbers in parentheses in the descriptions of the files represent the rank
I had for when my solutions were submitted and accepted. Time, if given, is a
rough estimate of how long it took to solve both halves.

A given day and part can be run via:

```
lein run DAY PART
```

where `DAY` is a number from 1-25 and `PART` is 1 or 2. If there is a "bis"
version of a day, that can be run via:

```
lein run -b DAY PART
```

## [day01.clj](day01.clj)

Day 1 (5607/5280, 45:51).

Despite my preparation, I still fumbled a bit on part 1, causing it to take
over 11 minutes. But the badness came with part 2: I already got a wrong
submission, because I didn't realize the word-digits could overlap. Figuring
out the root of the problem took way too long; solving it only had a few small
mis-fires.

## [day02.clj](day02.clj)

Day 2 (65239/61792, ~53:00).

(Note: Due to being hella sick when this puzzle opened up, I didn't start it
until the next morning. So, rather than starting right at midnight EST I
started at about 10:40AM EST. Hence the super-low placement numbers.)

This was another problem where I spent far more time on the parsing of the
input data than I spent on the actual problem itself. I need to try to find
common threads in these puzzles that have the harder-to-parse data, and see if
I can convert them to utility functions.

The puzzles themselves were pretty simple. Part 1 was just filtering out the
games that could not be viable with the given number of cubes. This was pretty
straightforward with `not`/`some` over a basic predicate. Part 2 was to find
out the minimum number of cubes of each color needed for each game to be valid.
We were then to take those three number, multiply them, and total up all the
products. Again, very simple. It actually required less code and less time than
part 1 had.

## [day03.clj](day03.clj)

Day 3 (--/--).

## [day04.clj](day04.clj)

Day 4 (--/--).

## [day05.clj](day05.clj)

Day 5 (--/--).

## [day06.clj](day06.clj)

Day 6 (--/--).

## [day07.clj](day07.clj)

Day 7 (--/--).

## [day08.clj](day08.clj)

Day 8 (--/--).

## [day09.clj](day09.clj)

Day 9 (--/--).

## [day10.clj](day10.clj)

Day 10 (--/--).

## [day11.clj](day11.clj)

Day 11 (--/--).

## [day12.clj](day12.clj)

Day 12 (--/--).

## [day13.clj](day13.clj)

Day 13 (--/--).

## [day14.clj](day14.clj)

Day 14 (--/--).

## [day15.clj](day15.clj)

Day 15 (--/--).

## [day16.clj](day16.clj)

Day 16 (--/--).

## [day17.clj](day17.clj)

Day 17 (--/--).

## [day18.clj](day18.clj)

Day 18 (--/--).

## [day19.clj](day19.clj)

Day 19 (--/--).

## [day20.clj](day20.clj)

Day 20 (--/--).

## [day21.clj](day21.clj)

Day 21 (--/--).

## [day22.clj](day22.clj)

Day 22 (--/--).

## [day23.clj](day23.clj)

Day 23 (--/--).

## [day24.clj](day24.clj)

Day 24 (--/--).

## [day25.clj](day25.clj)

Day 25 (--/--).
