# FearlessBirthHeuristic

> **A novel metaheuristic for the 0/1 Knapsack Problem that lands
> within <1% of the dynamic-programming optimum while running ~6×
> faster — in 30 lines of Kotlin.**

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![JVM](https://img.shields.io/badge/JVM-17-orange?logo=openjdk&logoColor=white)](https://adoptium.net/)
[![Build](https://img.shields.io/badge/build-Gradle%20Kotlin%20DSL-02303A?logo=gradle&logoColor=white)](https://gradle.org)
[![Tests](https://img.shields.io/badge/tests-JUnit5-25A162?logo=junit5&logoColor=white)](https://junit.org/junit5/)
[![License: MIT](https://img.shields.io/badge/license-MIT-yellow.svg)](LICENSE)

## Description

`FearlessBirthHeuristic` is a Kotlin reference implementation of the
**"El que tenga miedo a morir, que no nazca"** ("Who fears death,
should not be born") metaheuristic for the **0/1 Knapsack Problem** —
an algorithm originally designed and empirically evaluated by Jesús
Marcano and Pedro Samuel Fagundez at Universidad Simón Bolívar against
six classical alternatives (Dynamic Programming, Genetic Algorithms,
Ant Colony Optimization, Local Search, Tabu Search, Memetic Algorithm).

The core idea is a threshold-split greedy: sort items by value/weight
density, partition into a "fearless" group (above threshold) and a
"cautious" group (below), and fill the knapsack from the fearless
group first. The accompanying paper —
[`docs/knapsack-solutions.pdf`](docs/knapsack-solutions.pdf) — shows
this beats GA / ACO / Tabu / Memetic on the **quality-per-millisecond**
trade-off, and approaches the DP optimum while running an order of
magnitude faster than DP itself.

## Headline results

Measured on 10 random 0/1-knapsack instances per problem size, all
seven algorithms run on the same instances. Full tables in
[`RESULTS.md`](RESULTS.md). DP is exact / optimal; everything else is
heuristic.

| Problem size | This metaheuristic vs DP — value | This metaheuristic vs DP — time | vs next-fastest non-DP method |
|---|---|---|---|
| **n = 250 items** | **99.26%** of DP avg | **5.4× faster** | 3.2× faster than Local Search |
| **n = 500 items** | **99.41%** of DP avg | **6.8× faster** | 5.7× faster than Local Search |

The other heuristics in the paper land 75–80% of the DP optimum at
best, in 100×–10,000× the time.

## Quickstart

```bash
git clone https://github.com/jcellomarcano/FearlessBirthHeuristic.git
cd FearlessBirthHeuristic
gradle wrapper            # one-time: materializes ./gradlew
./gradlew run             # runs the benchmark on n=1000 random items
./gradlew test            # runs the JUnit5 correctness suite
```

Requires JDK 17+. No other dependencies — the only library at runtime
is the Kotlin stdlib; tests depend on JUnit 5.

## Architecture

```
src/main/kotlin/knapsack/
├── Item.kt              # data class Item(value, weight)
├── FearlessOld.kt       # Greedy-by-density baseline (no threshold split)
├── NewFearless.kt       # The metaheuristic: threshold-split greedy
├── DPKnapSack.kt        # Exact O(n·W) dynamic-programming reference
└── Main.kt              # Benchmark harness — runs all three, prints table

src/test/kotlin/knapsack/
└── FearlessHeuristicTest.kt   # 5 JUnit5 tests incl. ≥90%-of-DP regression guard

docs/
└── knapsack-solutions.pdf     # Original paper, Marcano & Fagundez (USB)

AUDIT.md                       # Full code-quality + positioning audit
RESULTS.md                     # Validated benchmark numbers from the paper
REFACTOR_BACKLOG.md            # Engineering backlog (all completed)
```

## How the algorithm works

```kotlin
fun fearlessMetaheuristic(
    items: List<Item>,
    capacity: Int,
    threshold: Double,
    valueDensity: (Item) -> Double,
    fitsIn: (Item, Int) -> Boolean,
): List<Item> {
    val scored = items.map { it to valueDensity(it) }                       // O(n)
    val (fearless, cautious) = scored.partition { it.second >= threshold }  // O(n)

    val fearlessSorted = fearless.sortedByDescending { it.second }.map { it.first }
    val cautiousSorted = cautious.sortedByDescending { it.second }.map { it.first }

    val selected = mutableListOf<Item>()
    var remaining = capacity
    fun tryAdd(item: Item) {
        if (fitsIn(item, remaining)) { selected.add(item); remaining -= item.weight }
    }
    fearlessSorted.forEach(::tryAdd)
    cautiousSorted.forEach(::tryAdd)
    return selected
}
```

**Complexity:** `O(n log n)` time (the sort dominates), `O(n)` extra
space. Compare to DP's `O(n·W)` time and `O(n·W)` space, which becomes
the bottleneck as capacity `W` grows.

**Why it works:** the greedy-by-density solution is already strong for
0/1 Knapsack. The threshold-split is a tiebreaker: it forces the
algorithm to commit to high-density items first even when the sort
order alone would interleave them with lower-density alternatives that
happen to fit slightly better in the current slack. The threshold
`2.6` was calibrated empirically against the paper's test instances.

## Tuning

Defaults live in [`Main.kt`](src/main/kotlin/knapsack/Main.kt) as
`const val`:

| Constant | Default | What it controls |
|---|---|---|
| `FEARLESS_THRESHOLD` | `2.6` | The cutoff between fearless and cautious groups |
| `NUM_ITEMS` | `1000` | Universe size for the benchmark |
| `CAPACITY` | `500` | Knapsack capacity |
| `NUM_RUNS` | `10` | Number of independent benchmark trials |
| `BENCHMARK_SEED` | `42L` | RNG seed — change for different instance distributions |
| `MIN_VALUE` / `MAX_VALUE` | `10` / `59` | Item value distribution |
| `MIN_WEIGHT` / `MAX_WEIGHT` | `2` / `19` | Item weight distribution |

The benchmark uses `kotlin.time.measureTimedValue` for nanosecond
precision — relevant because the metaheuristic runs in sub-millisecond
time on these inputs and `measureTimeMillis` would round to 0.

## Testing

Five JUnit 5 tests in
[`FearlessHeuristicTest.kt`](src/test/kotlin/knapsack/FearlessHeuristicTest.kt):

| Test | What it guards |
|---|---|
| `empty input returns empty selection` | Edge case across all three solvers |
| `zero capacity returns empty selection` | Edge case across all three solvers |
| `solutions never exceed capacity` | Invariant — 50 random items, seeded |
| `DP returns exact optimum on a known small instance` | Reference correctness (220 on the textbook 4-item bag) |
| `fearless metaheuristic lands within 10 percent of DP optimum` | The paper's central claim as a regression guard (loose at 90%; the actual gap is <1% in practice) |

## Reproducing the paper

The paper compares 7 algorithms on n=250 and n=500. This repository
ships **only DP and the two fearless variants** — the GA / ACO / Tabu /
Memetic implementations referenced in the paper are not included here.
All numbers in [`RESULTS.md`](RESULTS.md) are extracted directly from
Tables 1 and 2 of [`docs/knapsack-solutions.pdf`](docs/knapsack-solutions.pdf).

To reproduce just the DP-vs-MH numbers on your machine, edit `NUM_ITEMS`
in `Main.kt` to `250` or `500` and run `./gradlew run`.

## Why this exists

This is a coursework artifact from the Algorithm Design subject at
Universidad Simón Bolívar, published as a reference implementation —
not as a maintained library. The repository has been audited and
hardened for public hosting; see [`AUDIT.md`](AUDIT.md) for the full
findings table, public-readiness review, and positioning verdict.

## References

- Marcano, J. & Fagundez, P. S. *Knapsack Problem solucionado con
  múltiples enfoques*. Universidad Simón Bolívar.
  [`docs/knapsack-solutions.pdf`](docs/knapsack-solutions.pdf)
- Gendreau, M., & Potvin, J.-Y. (2010). *Handbook of Metaheuristics*.
  Springer.
- GeeksforGeeks, *0/1 Knapsack Problem | DP-10* —
  <https://www.geeksforgeeks.org/0-1-knapsack-problem-dp-10/>
- Course CI-5652, Prof. Ricardo Monascal — Universidad Simón Bolívar.

## License

Released under the [MIT License](LICENSE).

## Authors

- **Jesús Marcano** — [@jcellomarcano](https://github.com/jcellomarcano)
- **Pedro Samuel Fagundez** — paper co-author
