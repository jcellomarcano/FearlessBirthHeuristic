# FearlessBirthHeuristic

[![Kotlin](https://img.shields.io/badge/Kotlin-1.8-blueviolet.svg)](https://kotlinlang.org)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A Kotlin implementation of the **"El que tenga miedo a morir, que no
nazca"** metaheuristic for the **0/1 Knapsack Problem**, plus a
dynamic-programming reference solver for benchmark comparison.

This is a university coursework artifact — Algorithm Design subject,
Universidad Simón Bolívar — accompanying the paper
[`docs/knapsack-solutions.pdf`](docs/knapsack-solutions.pdf) by
Jesús Marcano and Pedro Samuel Fagundez. It is published here as a
reference implementation, not as a maintained library.

## The heuristic in one paragraph

For each item, compute its value-to-weight ratio. Pick a threshold and
split items into two groups: **fearless** (ratio ≥ threshold) and
**cautious** (ratio < threshold). Sort each group by ratio descending.
Fill the knapsack greedily from the fearless group first; if capacity
remains, continue from the cautious group. That's it. It is a
threshold-tuned variant of the classical greedy-by-density heuristic,
and the empirical claim of the paper is that on the tested
500-item / 1000-item instances it lands within a few percent of the DP
optimum while running orders of magnitude faster.

See the paper for the threshold-selection discussion, the comparison
against DP / GA / ACO / Tabu Search / Memetic Algorithm, and the
result tables. The headline numbers are also reproduced in
[`RESULTS.md`](RESULTS.md): on n=250 / n=500 random instances, the
metaheuristic lands within **<1% of the DP optimum** while running
**5–7× faster than DP itself**.

## What's in this repo

```
src/main/kotlin/knapsack/
├── Item.kt              # data class Item(value, weight)
├── FearlessOld.kt       # original greedy-by-density variant
├── NewFearless.kt       # threshold-split "fearless / cautious" variant
├── DPKnapSack.kt        # exact 0/1 knapsack via dynamic programming (reference)
└── Main.kt              # benchmark harness — runs all three on random inputs
src/test/kotlin/knapsack/
└── FearlessHeuristicTest.kt   # JUnit5 correctness check vs DP optimum
docs/
└── knapsack-solutions.pdf     # accompanying paper
build.gradle.kts             # Gradle build (Kotlin DSL)
settings.gradle.kts
```

## How to run it

### With Gradle (requires JDK 17+)

The repo ships `build.gradle.kts` + `settings.gradle.kts` but does **not**
commit the Gradle wrapper JAR (binary). Generate it once locally with a
system Gradle install:

```bash
gradle wrapper
./gradlew run
./gradlew test
```

### From IntelliJ IDEA

1. **File → Open** → select this repository's root directory.
2. IntelliJ will detect `build.gradle.kts` and offer to import as a
   Gradle project. Accept.
3. Open `src/main/kotlin/knapsack/Main.kt` and click the green ▶ gutter
   icon next to `fun main()`.

### Without Gradle (kotlinc only)

```bash
# Requires kotlinc on PATH (brew install kotlin)
kotlinc src/main/kotlin/knapsack/*.kt -include-runtime -d FearlessBirth.jar
java -jar FearlessBirth.jar
```

## Sample output

```
Algoritmo | Elementos | Tiempo (ms) | Valor | Peso
Heurística de selección | Run 1 | 0.00200 s | 14732 | 500
Heurística de selección | Run 2 | 0.00100 s | 14688 | 500
...
Programación dinámica | Run 10 | 1.23400 s | 14751 | 500
Promedio de tiempo: ... | Valor máximo: ... | Mediana: ... | Desviación estándar: ...
```

(The output language is Spanish — same as the paper.)

## Tuning

Defaults in `Main.kt`:

| Parameter | Default | Where |
|---|---|---|
| `numItems` | 1000 | `Main.kt:12` |
| `maxValue` | 59 | `Main.kt:13` |
| `maxWeight` | 19 | `Main.kt:14` |
| `capacity` | 500 | `Main.kt:15` |
| `numRuns` | 10 | `Main.kt:17` |
| `threshold` (fearless/cautious split) | 2.6 | `Main.kt:44` |

`Random` is not seeded — successive runs will produce different inputs.
See [REFACTOR_BACKLOG.md](REFACTOR_BACKLOG.md) item 14 for the
reproducibility task.

## Limitations and known issues

This is coursework, not production code. Remaining rough edges are
catalogued in [AUDIT.md](AUDIT.md) and [REFACTOR_BACKLOG.md](REFACTOR_BACKLOG.md).
If you intend to use this as a starting point for something serious,
read `AUDIT.md` first.

## References

- Marcano, J. & Fagundez, P. S. *Knapsack Problem solucionado con
  múltiples enfoques*. Universidad Simón Bolívar. See
  [`docs/knapsack-solutions.pdf`](docs/knapsack-solutions.pdf).
- GeeksforGeeks, *0/1 Knapsack Problem | DP-10*.
  <https://www.geeksforgeeks.org/0-1-knapsack-problem-dp-10/>

## License

MIT. See [LICENSE](LICENSE).

## Author

- Jesús Marcano ([@jcellomarcano](https://github.com/jcellomarcano))
- Paper co-author: Pedro Samuel Fagundez
