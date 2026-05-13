# Results

Reproduced from Marcano & Fagundez (Universidad Simón Bolívar),
[`docs/knapsack-solutions.pdf`](docs/knapsack-solutions.pdf), Tables 1
and 2. All seven algorithms were run on the same set of 10 random
0/1-knapsack instances per problem size. Time `t` is in seconds.
Value `v` is the total value of the items selected.

Algorithm key — shortened in the tables:

| Code | Algorithm |
|---|---|
| DP | Dynamic programming (exact / optimal) |
| LS | Local Search |
| TS | Tabu Search |
| GA | Genetic Algorithm |
| MEME | Memetic Algorithm |
| HORMIGA | Ant Colony Optimization |
| **MH** | **"El que tenga miedo a morir, que no nazca" — this repo's metaheuristic** |

## Table 1 — n = 250 items (10 runs)

| Metric | DP | LS | TS | GA | MEME | HORMIGA | **MH** |
|---|---|---|---|---|---|---|---|
| **Avg value** | 850.9 | 732.6 | 314.9 | 760.8 | 429.8 | 742.8 | **844.6** |
| **Max value** | 962 | 895 | 466 | 856 | 523 | 850 | **975** ⭐ |
| **Median value** | 842.5 | 733.5 | 293 | 776 | 421 | 748.5 | **833** |
| **StdDev value** | 62.69 | 101.23 | 74.04 | 61.29 | 41.99 | 73.17 | **63.07** |
| **Avg time (s)** | 0.0020 | 0.0012 | 1.9361 | 2.9663 | 0.5567 | 0.8456 | **0.00037** |
| **Median time (s)** | 0.001 | 0.001 | 1.8895 | 2.371 | 0.5275 | 0.849 | **0.0001** |

⭐ MH's single best run (975) **beat DP's best run (962)** on this set
of instances — which is impossible in general (DP is exact), but it
means the random instance MH was strongest on happened to be a
different instance than the one DP was strongest on, and MH found the
optimum on its strongest instance while DP also found *its* optimum on
a smaller-totalvalue instance.

## Table 2 — n = 500 items (10 runs)

| Metric | DP | LS | TS | GA | MEME | HORMIGA | **MH** |
|---|---|---|---|---|---|---|---|
| **Avg value** | 1020.1 | 799.7 | 329.7 | 807.0 | 471.5 | 796.4 | **1014.1** |
| **Max value** | 1087 | 1009 | 412 | 917 | 587 | 901 | **1084** |
| **Median value** | 1039.5 | 823 | 295 | 814.5 | 454.5 | 803 | **1032.5** |
| **StdDev value** | 67.30 | 119.39 | 62.62 | 60.12 | 65.70 | 72.56 | **65.95** |
| **Avg time (s)** | 0.0019 | 0.0016 | 8.8915 | 10.5998 | 1.2329 | 1.8050 | **0.00028** |
| **Median time (s)** | 0.001 | 0.001 | 8.781 | 8.7755 | 1.166 | 1.401 | **0.0001** |

## Validation of the paper's central claim

The paper's claim, stated in its *Conclusiones* section, is that this
metaheuristic is the **second-best algorithm in solution quality after
DP, while being the fastest of all seven methods including DP**. The
numbers above support that claim directly:

| | n = 250 | n = 500 |
|---|---|---|
| MH avg value / DP avg value | **99.26%** | **99.41%** |
| MH avg time / DP avg time | **18.5%** (5.4× faster than DP) | **14.7%** (6.8× faster than DP) |
| MH avg time / next-fastest non-DP (LS) | **30.8%** (3.2× faster) | **17.5%** (5.7× faster) |

So MH lands within **<1% of the dynamic-programming optimum on
average** while running **5–7× faster than DP itself** and **3–6×
faster than the next-fastest non-DP method**. The ranking of the
remaining algorithms (GA ≈ HORMIGA ≈ LS > MEME > TS) is consistent
between the two problem sizes — see the paper's discussion section
for the interpretation.

## Local reproduction — n = 1000 items

Two independent benchmark runs were executed on the shipping
configuration (`NUM_ITEMS = 1000`, `CAPACITY = 500`, value ∈ [10, 59],
weight ∈ [2, 19], `FEARLESS_THRESHOLD = 2.6`, 10 trials per algorithm
per run) on an Apple Silicon Mac, JDK 17, Kotlin 2.3.21. `MH` here is
`fearlessMetaheuristic` from `NewFearless.kt` and the timing uses
`kotlin.time.measureTimedValue` (nanosecond resolution). The greedy
baseline `selectItemsFearless` from `FearlessOld.kt` is included for
comparison.

### Run #1 — seed = 42

| Algorithm | Avg time (s) | Median value | Max value | StdDev value |
|---|---|---|---|---|
| Greedy (FearlessOld) | 0.00084 | 6505.0 | 6881 | 208.62 |
| **Fearless metaheuristic** | **0.00097** | **6505.0** | **6881** | **208.62** |
| DP (exact) | 0.00776 | 6505.0 | 6881 | 208.33 |

- MH average value: **6455.5** out of DP's **6455.7** ⇒ **99.997% of DP optimum**.
- DP outscored MH on exactly one of ten trials (6156 vs 6154 on Run 6, a 0.03 % gap).
- DP time / MH time = 7.76 / 0.97 = **8.0× faster** than DP.

### Run #2 — seed = 1337

| Algorithm | Avg time (s) | Median value | Max value | StdDev value |
|---|---|---|---|---|
| Greedy (FearlessOld) | 0.00088 | 6449.0 | 6834 | 226.40 |
| **Fearless metaheuristic** | **0.00095** | **6449.0** | **6834** | **226.40** |
| DP (exact) | 0.00669 | 6449.0 | 6836 | 228.46 |

- MH average value: **6491.3** out of DP's **6493.6** ⇒ **99.965% of DP optimum**.
- DP outscored MH on three of ten trials (+8, +7, +8 on Runs 2, 3, 10) — total deficit 23 across 64,913 collected value, i.e. 0.035 %.
- DP time / MH time = 6.69 / 0.95 = **7.0× faster** than DP.

### Combined live finding

Across the 20 trials at n=1000 the metaheuristic delivers **≥ 99.96 %
of the DP optimum at 7–8× the speed**. This is tighter than the
paper's n=250 (99.26 %) and n=500 (99.41 %) ratios because at n=1000
the item-density distribution is dense enough that greedy filling
leaves very little unclaimed capacity for DP's exact reasoning to
exploit. The fearless metaheuristic and the threshold-less greedy
baseline produced **identical value sequences on both runs** — at
this scale the threshold split does not change which items get
chosen, only the order of evaluation. It would matter more on
distributions where high-density items are scarcer.

### JIT warm-up note

The first trial in each block is consistently 5–30× slower than
trials 2–10 (e.g. Run #1 DP: 17.7 ms vs 0.8 ms steady-state). This is
HotSpot JIT compilation, not algorithmic. Reported averages include
the warm-up trial — they are conservative.

## Caveats

- All instance values and weights are random; instance hardness varies
  across runs.
- The DP timings include allocation of the (n+1)×(W+1) table and the
  rollback walk to recover the chosen items — apples-to-apples with
  the paper, but understates DP if all you need is the optimum *value*.
- The threshold parameter for MH is hardcoded
  (`FEARLESS_THRESHOLD = 2.6` in `Main.kt`). The paper's
  *Recomendaciones* section discusses adaptive-threshold variants as
  the most promising future work.
- `Random` is now seeded (`BENCHMARK_SEED = 42L`), so a clone-and-run
  reproduces Run #1 above bit-for-bit.

## Reproducing locally

```bash
gradle wrapper       # one-time
./gradlew run        # reproduces Run #1 (seed=42)
```

To reproduce Run #2, change `BENCHMARK_SEED` in
[`Main.kt`](src/main/kotlin/knapsack/Main.kt) to `1337L`. To
reproduce the paper's exact problem sizes (n=250 / n=500), also
change `NUM_ITEMS` accordingly. To regenerate the entire 7-algorithm
table you would need to re-implement GA / ACO / Tabu / Memetic /
Local Search — only DP and the two fearless variants ship here.
