# Refactor backlog

Ranked by impact-to-effort. Top three implemented on
`polish/audit-20260513`. Full rationale and per-item context lives in
[AUDIT.md](AUDIT.md).

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
| 16 | GitHub-archive the repo (`Settings → Archive this repository`) — preserves the Kotlin-literacy signal while marking the repo as a finished academic artifact rather than abandoned current work | S (1 click) | Owner decision |
