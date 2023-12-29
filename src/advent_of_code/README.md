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

Day 11 (5315/6205, 1:13:47).

Finished part 1 in 41:13, then needed about 33 more minutes to finish overall.

Part 1 was tricky for getting the logic of expanding the universe within the
matrix that was the field. I didn't see part 2 coming very well, or I wouldn't
have spent so much time on the actual field representation.

In part 2, the "expansion went from 2 to 1,000,000 for each empty column and
row. Obviously, there wasn't going to be any field representation in this case.
The math escaped me for a bit, and I had an off-by-one error that I caught
before I submitted a part 2 answer.

The code isn't very pretty, though. I'll revise it to have less duplication
between parts.

## [day12.clj](day12.clj)

Day 12 (2473/10978, 14:00:02).

I solved part 1 relatively quickly (36:40), getting my highest finish so far
this year. But it was a brute-force solution and it was clear it wouldn't work
for part 2. I looked at a lot of different angles for part 2, including trying
to implement the heuristics suggested on the puzzle page to reduce the search
space.

It was clear this was a dynamic programming problem, but I couldn't see *how*
to apply dynamic programming in this case. I got frustrated and went to bed.

This morning, with a clearer mind, I looked at a few Python solutions. I picked
one that I understood better than the others and converted it. I hate doing
that, in place of my own understanding/solution. But it was that or spend
several more hours re-learning the finer points of dynamic programming. Even
this algorithm took over 5s to return the answer for part 2.

## [day13.clj](day13.clj)

Day 13 (7944/21115).

This sucked. Started about two hours late due to a hockey game, no big deal.
Part 1 went pretty well, but part 2's answer was too low. Fiddled for a bit,
but it was late and I chose to sleep on it.

Today wasn't any better. I just wasn't getting the corner-cases right, from
what I could tell. And my logic was all over the map. In the end, I took a
comparator defn from one of the Clojure Slack guys, and that worked. And is
*significantly* shorter than my non-working code had been.

## [day14.clj](day14.clj)

Day 14 (7261/17848, 13:38:01).

Started on time and finished part 1 in 1:09:29. But it took over 20s to run
part 1, and I knew that wouldn't be viable for part 2.

Part 2 was another "find the cycle and compute" problem, where you were to find
out the answer after 1,000,000,000 iterations of the process. I slept on this,
hence the long total time. I had to rewrite part 1 before doing this, and I
wish I had written part 1 this way from the start. The rewrite needed 43ms to
run what the original needed 20s for.

Happy with the code, and I think I can factor-out the "find the cycle" code
into something more generic for future use.

## [day15.clj](day15.clj)

Day 15 (10677/22388, 11:47:06).

Started this one just under two hours late, then slept after part 1 for
unspecified reasons.

Part 1 was very easy, finished in about 5 minutes of effort. Part 2 took about
40 minutes, mostly due to morning-brain fog. I had forgotten to convert the
focal length values from strings to integers, and this led to a misleading
error message from the Clojure run-time. I also mis-read the instructions for
part 2 and was initially hashing the whole instruction to get the box number,
instead of just the label.

Overall, the code is pretty tight. There may be things I can improve upon, once
I've read some other Clojure solutions. I like that all the non-public defn's
are under 10 lines in length.

## [day16.clj](day16.clj)

Day 16 (20025/19217, 14:08:44).

Started part 1 about 2:15 late, but I was exhausted from a holiday party and I
opted to go to sleep and finish this morning. Realistically, I spent about 30
minutes on each half (the second half took 35 minutes because I initially had a
bug to find and fix).

Part 1 went fairly easily. My first test-run of it blew out the stack, because
I wasn't tracking the squares I had already visited *and* the beam direction as
well. I was only tracking the "charged" squares. When I added a track of
entry-points as well, the code worked and got part 1 in about 129ms of
run-time.

Part 2 was not as hard as I had feared-- I was worried we might be tasked with
finding one (or more!) mirror/splitter to adjust in order to maximize the
charging. Rather, we were to try entering the beam from any of the edge-points.
This was much easier to code, and ran much faster than the other requirement
would have. But I got a wrong answer on my first submission for part 2, because
I had an off-by-one error in my code. Four of them, in fact, in the bodies of
the two `for`-comprehensions in `trace-all`. After explaining my approach to my
wife, I had a [rubber
duck](https://en.wikipedia.org/wiki/Rubber_duck_debugging) debugging moment and
realized the off-by-one errors. That gave me the correct answer (which was, of
course, only off from my initial answer by 1). Part 2 ran in about 28.5s.

## [day17.clj](day17.clj)

Day 17 (17965/16885, >24h).

I wasn't able to finish this one within the first 24 hours (I went to a hockey
game that night, and played in a classical music concert the next day).

This was harder for me than it seems to have been for the average competitor. I
tried a few things in which I would maintain extra state for counting the
number of times the position moves in a given direction. None of them worked,
and some of them were too complex to fully wrap my head around. I was also
dead-set on using the [A*](https://en.wikipedia.org/wiki/A*_search_algorithm)
search algorithm rather than ordinary Dijkstra, because I thought it would be
more efficient on the larger field that was the puzzle input.

After all my mis-starts and frustration, I started looking at other solutions.
Most of the Python ones I looked at used a priority queue (from a built-in
class). I was concerned I'd have to refresh my memory on this data structure
and write one, but then I looked at [Norman
Richard's](https://gitlab.com/maximoburrito/advent2023/-/blob/main/src/day17/main.clj)
solution and learned that there is an existing priority queue implementation. I
also saw that Norman did some rather clever things with the state of movement,
and tracking whether the position is moving left/right or up/down.

So my final code bears a strong resemblance to Norman's, though I don't know if
it is faster or not, being A* instead of Dijkstra. I do know that once I got
part 1 done, it was fairly small change to enable part 2. I was confident of my
answer for part 2, since I not only got the example answer correct but also the
second example (the field of all 1's and 9's) correct.

**Update**: I converted Norman's code to work in my framework and did a few
simple runs. For part 1, the difference in run-time was next to 0. But in part
2 his code was measurably faster, about 10-12%. A* was probably overkill in
this case, or perhaps having to track more states had an effect.

## [day18.clj](day18.clj)

Day 18 (4282/1954, 1:33:32).

Shoelace Formula and Pick's Theorem were (apparently) the words of the day.
Everyone was using them, so I did too.

## [day19.clj](day19.clj)

Day 19 (5967/4127, 3:45:34).

I lost a LOT of time on part 1, due to an error in a regular expression causing
an exception whose message was not helpful. Once I fixed my parsing, writing
the code to process the workflows went pretty cleanly.

On part 2, I had no idea how to proceed. I took some ideas from a Perl
solution, and some ideas from a Clojure solution. The Perl solution's URL is
hella long, so I can't easily link it here. The Clojure solution is
[here](https://github.com/alexalemi/advent/blob/main/2023/clojure/p19.clj), and
it filled in the gaps that I couldn't figure out from the Perl (which is hella
ironic, if you know me).

## [day19bis.clj](day19bis.clj)

Made a few small improvements, mostly cosmetic. Did speed up the running of
part 1 by about 10% or so. No changes to part 2.

## [day20.clj](day20.clj)

Day 20 (3773/8488, 14:21:09).

This was another day where I ended up sleeping on part 2. And that didn't
really help; I did not see that this was a matter of binary counters and gates.
Looking at other Clojure solutions and asking for help on the Slack channel was
what finally got part 2 done.

The problem itself is another of the "circuit" variety, with a single
broadcaster and a variety of points that are either "flip-flop" or
"conjunction" modules. You send a low pulse to the broadcaster via a button,
and the pulse propagates throughout the circuit.

Part 1 was to just count how many pulses are sent, low and high, after pressing
the button 1,000 times. This was a basic iterative approach, just "press" the
button 1,000 times and track all signals sent to all modules. Runs in about
230ms.

Part 2 was a typical twist: one of the modules is a "sink", and is also the
wire that "turns on the machine". You have to find out the minimum number of
button presses that are needed to get a low signal to that sink. Of course, you
can't brute-force this... that would be too easy (and too obvious). Here, I had
to talk to the other Clojure people. It turns out that the sink is fed by a
conjunction module with four inputs. All four inputs have to send a high pulse
at the same time for this module to send a low pulse to the sink. The solution
is to find the cycle of button-pushes for each of the four inputs to send a
high pulse, then take the LCM of those four numbers.

I won't share the answer (since that would be meaningless anyway), but it was
in the neighborhood of 225 *trillion*. I hope the elves can automate the
pressing of that button...

## [day21.clj](day21.clj)

Day 21 (3131/*, 2:06:41).

(I'm not counting my finish-position for part 2 for personal reasons.)

Part 1 was very straightforward. Kind of like a search, but with overlapping
steps being allowed. Based on the approach taken for part 2, I realize I could
speed up this implementation with some caching of the viable moves from each
garden plot. As it stands, part 1 takes about 1.3s, so I'm not worried about
that.

But part 2... part 2, I just cribbed a Python solution and turned it into
Clojure. I'm not proud of that, because I didn't (and still don't, really)
*understand* it. It's one thing when I see an algorithm and understand it and
just translate the concepts from language X to Clojure, but that wasn't today's
part 2.

The funny thing, is, that when I compared the Python answer to the Clojure one,
Clojure took almost exactly 2x the time that Python did. I imagine I have a
bottleneck somewhere that isn't obvious.

## [day22.clj](day22.clj)

Day 22 (8758/8338, 14:48:54).

I didn't even start part 1 until the next morning. And then, none of my code
seemed to work. Working from a solution by Alex Alemi posted in the Slack, I
got through part 1. I was also able to understand how it works, and what I had
been doing wrong (some off-by-one errors, some wonky premature-optimization
issues).

But part 2... well, I have the answer but I don't completely understand the
code. Again, flailed at it for a while before looking back to Alex's code. I
need to understand transducers better. This isn't the first time I've seen them
used by other Clojure programmers.

## [day23.clj](day23.clj)

Day 23 (3284/6421, 12:40:31).

The word of the day is "hubris". Part 1 went pretty well (despite starting it
about 15-20 minutes late). It got the answer for the puzzle data in 1.7s, which
(at the time) seemed fine. Part 2 was just a matter of turning the "slick"
squares into normal ones and re-running the same code. This would be a snap!

(Narrator: It was not, in fact, a "snap".)

Part 2's twist introduced several cycles into the graph that describes the
field. My part 1 code got caught in these and just spun and spun. For part 2, I
had to write a whole new solution. It reuses some of part 1 (for getting the
start and end points, and the initial parsing of the field), but the actual
computation part is all new.

## [day24.clj](day24.clj)

Day 24 (9924/6088, 20:53:28).

I got off to a late start on this puzzle, but I thought I made pretty short
work of part 1 (after a refresher on basic algebra). But I had something wrong
with my function to test if an intercept was in the past, and my first
submission for part 1 was wrong. I tried four or so variations on the routine,
but all of them returned the same number. Once again, I slept on it. After
flailing for another hour or so, I turned to reddit and found a Python solution
that had used the same basic algebra as I had for intersection detection. I
looked at the function that checked for the intersection being in the past, and
though it was super-subtle, I did finally find a small bug in my version. I
ended up adapting the function that person had written, and got the right
answer.

Part 2 was, to my brain, the hardest puzzle of the year (with Day 25 pending,
of course). I have a "refined" brute-force approach, based largely on the same
Python code (credited in my code): creates sets of invalid values for each of
the three velocity axes, then have a three-layer loop over a reasonable range
on each axis that skips invalida values. However, this code kept crashing with
various different run-time errors. Once I (finally) got it to run all the way
through... it had found no answer. Debugging this took *hours*. Finally, I
realized that my generation of the sets of invalid numbers was incorrect.
Fixing that led to more run-time errors, but fixing them led to the correct
answer.

This isn't the greatest amount of elapsed clock-time for a day (that goes to
Day 17), but it was definitely the hardest one to wrap my head around.

## [day25.clj](day25.clj)

Day 25 (3548/2927, 3:48:58).

Well, this *should* have been easy. The problem was to find a [minimum
cut](https://en.wikipedia.org/wiki/Minimum_cut) of a graph. A good algorithm
for this is [Karger's
algorithm](https://en.wikipedia.org/wiki/Karger%27s_algorithm), which I
happened to have already implemented in the 4-course [Algorithms
Specialization](https://www.coursera.org/specializations/algorithms) from
Coursera. And I still have the code!

Only, even with generous commentary, it took me hours to f-ing **understand**
the code.

Once I understood it well-enough to conform the puzzle data into the format
that the algorithm requires, I was able to get the correct answer for the test
data. But there was a hitch... Karger's is an algorithm that incorporates
randomness, and isn't always guaranteed to get the true minimum cut. When I
wrote and ran this for the Coursera class, the input had 200 vertices and the
code ran for 5x this, or 1,000 iterations. The puzzle data, though, has over
1500 vertices. Not only is 5x that enormous, but each iteration takes that much
longer on its own.

So, for now, I hard-coded the number of iterations and kept increasing that
number by a factor of two until the answer had a cut-size of 3. It needed
somewhere between 64 and 128 iterations, and ran for almost three minutes. But
it got the right answer, and I didn't need anyone else's code to crib from.

There will be a follow-up file for this one. I can clean this up considerably.
My Clojure-fu was much weaker when I originally wrote this.

## [day25bis.clj](day25bis.clj)

I haven't "cleaned up" the code as much as I thought I could, to be honest.

The first thing I did was to replace the hard-coded number of iterations with a
use of `filter` over a `pmap` that produces an infinite lazy sequence. It takes
the first response from the parallel mapping in which the cut-size is 3. I also
eliminated the `do-karger-min-cut` function, as that had been there only
because I couldn't nest #-declared lambda functions (for `sort` over `pmap`).
Now that I understand `partial`, I was able to use that for the inner function.

I spent most of the time on the parallelism of the code, as the `pmap`-based
approach waits for all current threads to complete before letting the program
exit. Thus, the answer will appear (with the time taken to reach it) but the
program will continue to run for several minutes after the answer appears. When
the number of iterations was hard-coded, this wasn't apparent. But it became
clear when using the filtering approach. I tried a few different things,
including making use of the
[Claypoole](https://github.com/clj-commons/claypoole) thread-tools library for
Clojure. Even with this, I could not get the program to terminate immediately
once a solution was found. I removed that code and am calling this one done. I
have a possibly-novel algorithmic approach written by someone on reddit (in
Perl, no less) that I plan to convert to FP and see how it compares.
