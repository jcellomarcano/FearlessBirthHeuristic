# Refactor backlog

Ranked by impact-to-effort. **Items 1–15 are from the initial audit.
Items 17–21 surfaced during the post-audit deprecation /
optimization sweep and the live local benchmark.** All implementation
shipped on `polish/audit-20260513`. Full rationale and per-item
context lives in [AUDIT.md](AUDIT.md) — see §1 (findings) and §1b
(post-audit sweep) for the why.

| Rank | Item | Effort | Status |
|---|---|---|---|
| 1 | Add `.gitignore`, stop tracking `out/`, `.idea/`, `*.iml` | S (10m) | **DONE** |
| 2 | Rewrite README to match reality (no Gradle, actual run path, move PDF to `docs/`) | S (30m) | **DONE** |
| 3 | Remove duplicate `main()` from `FearlessOld.kt` and `NewFearless.kt` | S (10m) | **DONE** |
| 4 | Add Gradle build (`build.gradle.kts` + `settings.gradle.kts`, Kotlin 1.9 / JVM 17 toolchain, JUnit5 platform, `application` plugin) | M (45m) | **DONE** — wrapper JAR generated locally with `gradle wrapper` |
| 5 | Add JUnit5 correctness tests (5 cases: empty input, zero capacity, capacity invariant, DP textbook optimum, fearless ≥ 90% of DP) | S (30m) | **DONE** |
| 6 | Add `RESULTS.md` with paper benchmark numbers for n=250 and n=500 across all 7 algorithms, plus the headline ratios that validate the paper's claim | M (2–4h) | **DONE** |
| 7 | Replace magic threshold with `const val FEARLESS_THRESHOLD` + one-line justification | S (5m) | **DONE** |
| 8 | Rename `f1` → `valueDensity`, `f2` → `fitsIn` (top-level + parameter names) | S (5m) | **DONE** |
| 9 | Replace deprecated `sumBy` with `sumOf` (already implicitly done — the only `sumBy` calls lived inside the demo `main()` blocks removed in commit 3) | S (2m) | **DONE** |
| 10 | Fix integer-division median bug — extracted to `List<Int>.median(): Double` | S (2m) | **DONE** |
| 11 | Replace `for (i in 1..3) { when (i) … }` with a typed list of `Algorithm(label, solve)` records iterated with `.map` | S (15m) | **DONE** |
| 12 | Resolve the half-dead `generateNewItems` flag — deleted; benchmark always uses randomized instances | S (5m) | **DONE** |
| 13 | Add `package knapsack` declarations; move sources under `src/main/kotlin/knapsack/` | S (10m) | **DONE** |
| 14 | Seed `Random` in benchmark for reproducibility — `generateItems(…, rng: Random = Random.Default)`, `main()` seeds with `BENCHMARK_SEED = 42L` | S (10m) | **DONE** |
| 15 | Locale-pin `Double.format` to `Locale.US` so the table does not break on Spanish/European locales | S (2m) | **DONE** |
| 16 | GitHub-archive the repo (`Settings → Archive this repository`) — preserves the Kotlin-literacy signal while marking the repo as a finished academic artifact rather than abandoned current work | S (1 click) | **Owner decision** |
| 17 | Switch benchmark timing from `kotlin.system.measureTimeMillis` (1 ms resolution) to `kotlin.time.measureTimedValue` (ns resolution). Without this, sub-ms MH trials were silently rounded to `0 ms` or `1 ms` and the paper's 0.0001 s precision was unobtainable. | S (15m) | **DONE** |
| 18 | Cache `valueDensity` once per item inside `fearlessMetaheuristic` — was being recomputed on every `sortedByDescending` comparison plus two `.filter` passes (~20× the necessary call count at n=1000). Now `.map { it to valueDensity(it) }` + `.partition` + `.sortedByDescending { it.second }`, exactly `n` density calls. | S (10m) | **DONE** |
| 19 | Replace `Triple<Int, Int, Long>` benchmark result with named `data class RunResult(totalValue, totalWeight, time: Duration)`. Self-documenting accessors and required by #17 since `Duration` (not `Long ms`) is now the time type. | S (5m) | **DONE** |
| 20 | Rewrite README to portfolio-grade structure (tagline blockquote, badges with logos, headline-results table inline, 4-line quickstart, architecture tree, inline algorithm + complexity, tuning constants table, testing matrix, reproduce-the-paper section) | M (45m) | **DONE** |
| 21 | Live local validation: compile with `kotlinc 2.3.21 / JDK 17`, run twice (seeds 42 and 1337) on developer hardware, record live numbers (MH 99.965%–99.997 % of DP value, 7–8× faster) in `RESULTS.md § "Local reproduction"`. Also documents the HotSpot JIT warm-up effect (trial 1: 5–30× slower than steady state). | S (30m) | **DONE** |
| 22 | Set GitHub repo description + topics so the at-a-glance card on github.com displays a marketing-grade tagline rather than empty metadata: `gh repo edit jcellomarcano/FearlessBirthHeuristic --description "Kotlin metaheuristic for the 0/1 Knapsack Problem — within <1% of DP optimum, ~6× faster. USB coursework artifact (Marcano & Fagundez)." --add-topic kotlin --add-topic knapsack-problem --add-topic metaheuristic --add-topic optimization --add-topic algorithms --add-topic dynamic-programming` | S (5m) | **Owner action** |
