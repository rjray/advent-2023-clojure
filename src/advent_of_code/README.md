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

Day 3 (2893/3560, ~52:00).

Here is where my utils code paid off. I already had code for turning things
like the map-style input into a matrix.

Part 1 was to find all numbers adjacent to a symbol. This wasn't hard, as we've
been asked to "look around" points on maps before. Part 2 was to find all cases
where a `*` symbol was adjacent to exactly two numbers. That took a little more
work, but I actually finished it in less time than part 1 took.

## [day04.clj](day04.clj)

Day 4 (5260/6763, ~43:00).

This was a simple day, on the face of it. Got part 1 done in about 12-15
minutes, and coded part 2 in about another 10. I ran it, submitted the answer,
and it was wrong. I proceeded to spend another 15-20 minutes trying to debug
it. In the process, I finally got desperate-enough to have it `prn` the
"counts" vector for each loop iteration.

THIS time, the answer was different. I submitted that, and it was correct.

I have *no* idea why this happened. Subsequent runs of 4/2 give the correct
answer consistently. I can't replicate the first "answer". Since it seems to
have been a glitch, I chose not to count it as a wrong submission for my
stats-keeping.

## [day05.clj](day05.clj)

Day 5 (7845/38381, ???).

This was the first problem in which a brute-force approach for part 2 was not
computationally feasible.

Part 1 was pretty basic, though I over-engineered the parsing and
representation of the maps out of caution for part 2. In the end, a combination
of `partition` and `reduce` carried the puzzle.

Part 2, well, I gave up for the night on it. It was late in my current
time-zone (MST) and I had to be up early the next day to catch a plane home. So
I returned to this problem this afternoon (hence the huge place-number for part
2). Try as I may, I could not crack this on my own. I turned to reddit and read
some different solutions in different languages. The one I managed to
understand the most (that is, be the least confused by) was a Python solution
([here](https://github.com/Leftfish/Advent-of-Code-2023/blob/main/05/d05.py))
that does sampling to narrow down the general range before doing a final range
with a step of 1. The biggest problem with it, was that I couldn't use it on
the test data due to the size of the stepping. So when I submitted my answer
for part 2, it was a leap of faith. Luckily, it was correct.

## [day05bis.clj](day05bis.clj)

After some thought, I decided to refactor this code a little bit with the
knowledge that I didn't need to preserve the names of the maps and/or reference
them by name. Nothing really changed in the algorithms themselves.

I shortened the file by 36 lines (some of which came from comments being
simpler), but the real surprise came from the time difference: part 1's
run-time went down roughly 20%, while part 2's went down about 87%. My guess is
that, since part 2's sampling approach made significantly more calls to
`find-location` the savings there propagated more than it had in part 1.

## [day06.clj](day06.clj)

Day 6 (4297/3403, 16:35).

Wow, I think this is about the fastest I've finished a non-day-1 pair. Of
course, I was still #4297 and #3403, respectively.

Part 1 took an inordinate amount of time because of a dumb error that took too
long to debug. I had used the wrong value in the math, and spent a good 5+
minutes figuring that out.

Once I finished that, though, part 2 only took another 3 minutes. I was able to
effect the "merging" of the number with `clojure.string/replace` and a regular
expression of `[^\d]`. As a brute-force approach, it took 7.7s to run (versus
the 3ms that part 1 took). But done is done!

## [day06bis.clj](day06bis.clj)

Not very much changed here, just dropped the brute-force approach in favor of
using the [quadratic formula](https://en.wikipedia.org/wiki/Quadratic_formula)
instead.

## [day07.clj](day07.clj)

Day 7 (7593/7614, 1:48:36).

A poker simulation. Pretty sure there was something like this in a previous
year.

Part 1 was the basic part, and it took about 1:07:00. I struggled most with
sorting the hands within a rank correctly; early attempts kept mangling the
data somehow. Once I got that right, the rest fell into place and I got a right
answer.

Part 2 was a real looper: Now the `J` card is a joker, with some special rules
associated with it. I struggled with this one, trying to figure out the
"proper" way to override the `values` map for just part 2, to make the `J`
value be 1 instead of 11. Nothing I tried worked. Then it hit me: I just used
`clojure.string/replace` to turn all `J` instances into `1`, and added `1` to
`values` with a value of 1. Then I treated `1` as a joker. After the first
attempt on puzzle data crashed due to the smart-ass giving us a single hand of
all-jokers, I fixed that bug and got part 2 correct.

## [day07bis.clj](day07bis.clj)

This is a weak revision so far. I've only changed the expression on line 29 (in
`get-type`) to explicitly use `frequencies` as opposed to reimplementing it.

I am still certain that the sorting of hands within a rank (the combination of
`sort-rank` and `cmp-hands`) could be done in a way that is more concise, more
Clojure-like, and (probably) more efficient.

## [day08.clj](day08.clj)

Day 8 (16722/12029, ~1:09:49).

(Time reflects that I did not start this until almost exactly two hours after
the puzzle unlocked.)

Part 1 was a breeze. I initially did it with liberal use of `keyword`, until I
saw the requirements of part 2. The keyword-approach worked fine for part 1,
but changing everything back to plain strings also worked fine.

Part 2 was *another* case of brute force being totally unfeasible. Luckily, it
became clear that there were cycles of different lengths for each of the
starting nodes. So I reached into `clojure.math.numeric-tower` for the `lcm`
function and found the LCM for all the path-cycle-lengths. It took a while to
figure this solution out, though. 16 min or so for part 1, another 55-ish for
part 2.

I doubt I'll need to revise this one. I don't think it'll get faster or more
succinct.

## [day08bis.clj](day08bis.clj)

OK, a *little* bit of revision.

I removed `walk'` and make `walk` work for both parts. That trimmed 4-5 lines
(not counting the comments). I also replaced the numeric-tower `lcm` with a
version I got from reddit user `miran1` (https://www.reddit.com/user/miran1).
This didn't really shorten the code (other than removing the `:require` of the
numeric-tower lib), but it eliminated a start-up warning I was getting about
that library redefining `abs`.

## [day09.clj](day09.clj)

Day 9 (15636/15051, 3:23:44).

The time-elapsed on part 1 includes an hour or so to drive home from a club
gathering, where I had started (but not finished) part 1.

I don't know what to think, here. I wrote part 1 and submitted it based on the
puzzle input, to be told the answer was wrong. I tried and tried to find a bug,
no dice. So, I submitted the same answer again, thinking maybe I had missed a
digit when doing the copy/paste. Still wrong. I copied and ran two working
Python solutions and two other Clojure solutions, all gave the same answer.
After posting on reddit, the AoC creater himself said that the answer was
correct and I should try again. I did, and this time it was accepted.

Part 2 was solved 6 minutes later, most of that 6 minutes was updating people
in the Clojurians Slack and updating the reddit post.

~~I'm not going to count this "wrong" answer, either. I don't know what went
wrong, but I copy/pasted from my terminal. There were no invisible/hidden
characters there.~~ I have determined that I was at fault, after all. Updating
the main README.md as well.

## [day09bis.clj](day09bis.clj)

Just a minor change here, but a huge improvement. Using a trick shown by Raghav
on the Clojurians Slack instance, I rewrote `extrapolate` to be two much
smaller defn's. The first one, `infinite-diffs`, uses `iterate` to produce an
"infinite" lazy sequence of the diffs from an initial sequence. The rewritten
`extrapolate` uses `take-while` to get these diffs until a diff appears that is
all zeros. At that point, it `map`'s `last` over these and sums them (basically
what the original did with a `loop`/`recur` combo).

Run-times are half that of the original code. And it's one SLOC line shorter as
well.

## [day10.clj](day10.clj)

Day 10 (4894/2687, 2:05:12).

Part 1 took 1:08:52, part 2 took a little less than an hour after that.

Part 2 took this long because I made not one but TWO mistakes when carrying
over some Python logic that I borrowed from
[here](https://github.com/ricbit/advent-of-code/blob/main/2023/adv10-r.py). The
first mistake was that I didn't properly combine the test for a character to be
`.` AND for the count of `|` characters to be odd. But, the worse of the two
mistakes was that I didn't notice that the Python author had hard-coded a
change of the `S` character for its actual pipe character. This was necessary
for getting the find-internal-points logic to work. My first attempt at part 2
was too high as a result. So, that's three mistakes by day 10... that said, my
part 2 finish was my highest yet this year.

I'll definitely be revisiting this later. I don't like having the
`S`-substitution being hard-coded like that.

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
