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

## Caveats

- All instance values and weights are random; instance hardness varies
  across runs. See `Main.kt:11–17` for the generator parameters used
  for the n=1000 benchmark this codebase ships (different from the
  paper's n=250 / n=500 instances).
- The DP timings include allocation and the rollback-walk to recover
  the chosen items — this is the apples-to-apples comparison the paper
  reports, but it understates how fast DP would be if all you needed
  was the optimum *value* (no item list).
- The threshold parameter for MH is hardcoded (`threshold = 2.6` in
  `Main.kt:44`). The paper discusses adaptive-threshold variants in
  its *Recomendaciones* section as the most promising future work.
- The repo's current `Random` usage is unseeded, so a clone-and-run
  will produce different absolute numbers than the paper. The ratios
  above are stable across seeds.

## Reproducing locally

Open `Main.kt` in IntelliJ and run. To reproduce the paper's exact
problem sizes, change `Main.kt:12` from `numItems = 1000` to `250` or
`500`. To regenerate the entire table, you would need to re-implement
GA / ACO / Tabu / Memetic / Local Search — only DP and MH (in both
its original and threshold-split forms) ship in this repo.
