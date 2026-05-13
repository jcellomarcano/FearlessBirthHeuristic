# Refactor backlog

Ranked by impact-to-effort. Top three implemented on
`polish/audit-20260513`. Full rationale and per-item context lives in
[AUDIT.md](AUDIT.md).

| Rank | Item | Effort | Status |
|---|---|---|---|
| 1 | Add `.gitignore`, stop tracking `out/`, `.idea/`, `*.iml` | S (10m) | **DONE** |
| 2 | Rewrite README to match reality (no Gradle, actual run path, move PDF to `docs/`) | S (30m) | **DONE** |
| 3 | Remove duplicate `main()` from `FearlessOld.kt` and `NewFearless.kt` | S (10m) | **DONE** |
| 4 | Add Gradle wrapper (`gradle init --type kotlin-application`) so the README's `./gradlew run` becomes true and tests become possible | M (45m) | Backlog |
| 5 | Add one JUnit5 test: `fearlessMetaheuristic` value ≥ 0.9 × DP optimum on a fixed `n ≤ 20` instance (requires #4) | S (30m) | Backlog |
| 6 | Add `RESULTS.md` with paper benchmark numbers (`n ∈ {250, 500}`, vs. DP/GA/ACO/Tabu/Memetic) and one chart in `docs/` | M (2–4h) | Backlog |
| 7 | Replace magic `threshold = 2.6` (`Main.kt:44`) and `1.5` (`NewFearless.kt:49`) with a single `const val FEARLESS_THRESHOLD` + one-line justification | S (5m) | Backlog |
| 8 | Rename `f1` → `valueDensity`, `f2` → `fitsIn` (`NewFearless.kt:1–2`) | S (5m) | Backlog |
| 9 | Replace deprecated `sumBy` with `sumOf` in `FearlessOld.kt:36–37` and `NewFearless.kt:53–54` | S (2m) | Backlog |
| 10 | Fix integer-division median bug at `Main.kt:94` — cast to `Double` before dividing by 2 | S (2m) | Backlog |
| 11 | Replace `for (i in 1..3) { when (i) { 1->…; 3->…; else->… } }` (`Main.kt:45–72`) with a `listOf(::fearlessOld, ::fearlessMeta, ::dp)` loop | S (15m) | Backlog |
| 12 | Resolve the half-dead `generateNewItems` flag (`Main.kt:16,19–35`) — either wire it through all three benchmark paths or delete it | S (5m) | Backlog |
| 13 | Add `package` declarations; move sources under `src/main/kotlin/knapsack/` (requires #4 to avoid breaking IntelliJ run config) | S (10m) | Backlog |
| 14 | Seed `Random` in benchmark for reproducibility — accept a `Long` seed in `generateItems`, default to `Random.Default` | S (10m) | Backlog |
| 15 | Locale-pin `Double.format` (`Main.kt:103–105`) to `Locale.US` so the table does not break on Spanish/European locales | S (2m) | Backlog |
| 16 | GitHub-archive the repo (`Settings → Archive this repository`) once items 4–6 ship — or instead of them, if owner decides not to invest further effort | S (1 click) | Owner decision |
