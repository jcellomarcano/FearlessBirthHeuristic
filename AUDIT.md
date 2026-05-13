# AUDIT — FearlessBirthHeuristic

**Audited:** 2026-05-13
**Auditor brief:** Evaluate whether this repository should be pinned on
`github.com/jcellomarcano` as a portfolio asset supporting the positioning
*"Senior Mobile Architect — Payments & AI Integration · B2B contractor via
Parallel Digital LLC"*.
**Language:** Kotlin (verified — `src/main/kotlin/*.kt`, `.idea/kotlinc.xml`
pins `languageVersion=1.8`, `jvmTarget=1.8`).
**Repo type:** University coursework — a 0/1 Knapsack metaheuristic
implementation for the Algorithm Design subject at Universidad Simón
Bolívar. Single-author academic artifact, no build system, no tests.

---

## 1. Findings table

| Severity | Location | Issue | Why it matters | Recommended fix |
|---|---|---|---|---|
| **Critical** | `README.md:38–66` | README claims the project is built with Gradle (`./gradlew build`, `./gradlew run`) and pins Kotlin 1.9.x / JDK 17. **No `build.gradle.kts`, no `gradle/`, no `gradlew` exist.** A first-time visitor following the README will get `zsh: no such file or directory: ./gradlew`. | This is the #1 signal-killer. A reviewer who cannot clone-and-run within 60 seconds concludes the repo is abandoned or unserious. | Either (a) add a real Gradle wrapper so the README becomes true, or (b) rewrite the README to describe the actual setup (plain IntelliJ project, open in IDE, run `Main.kt`). Option (b) is the 10-minute fix; (a) is the 1-hour fix. |
| **Critical** | `FearlessBirth.iml`, `.idea/**`, `out/**` | IDE config files (`.iml`, `.idea/`) and compiled artifacts (`out/production/.../*.class`) are tracked in git. There is **no root `.gitignore`**. | Tracking IDE + build artifacts is the universally recognized "didn't read a Kotlin tutorial past page 2" signal. It breaks merges across machines/IDE versions and bloats clones with stale `.class` files. | Add a standard Kotlin/JVM `.gitignore` (`/out/`, `/build/`, `.idea/` except `.idea/runConfigurations`, `*.iml`, `.DS_Store`, `local.properties`). `git rm -r --cached` the now-ignored paths. |
| **High** | `src/main/kotlin/Main.kt:11`, `src/main/kotlin/FearlessOld.kt:20`, `src/main/kotlin/NewFearless.kt:37` | **Three competing `fun main()` entry points** in the same module. There is no way to tell which one the project "is". `FearlessOld.kt` and `NewFearless.kt` each contain a small ad-hoc demo `main()` that duplicates logic already in `Main.kt`. | Reads as scratch/experimental code, not a finished deliverable. A reviewer will not know which file to open first. | Keep the orchestrating `main()` in `Main.kt` only. Remove the `main()` blocks from `FearlessOld.kt` and `NewFearless.kt` — those files should expose only their algorithm functions. |
| **High** | `Main.kt:44` (`threshold = 2.6`), `Main.kt:12–15`, `NewFearless.kt:49` (`threshold = 1.5`) | Magic numbers everywhere: `threshold = 2.6` chosen with no comment, item bounds `(10..59)` and `(2..19)` hardcoded, capacity `500` hardcoded, threshold differs between files (`2.6` vs `1.5`). | Reviewers see "tutorial values" rather than "engineering choices". For an academic heuristic paper repo, the threshold choice is *the* finding — and it is unexplained in code. | Promote to named `const val` at top of `Main.kt` with a one-line comment citing the paper section / empirical sweep that justified the choice. E.g. `const val FEARLESS_THRESHOLD = 2.6 // value/weight ratio cutoff, calibrated for Section 4.2 instances`. |
| **High** | `Main.kt:79–98` | Reporting loop uses Spanish identifiers (`"Heurística de selección"`, `"Promedio de tiempo"`, `"Desviación estándar"`) and ad-hoc string concatenation. Header is `arrayOf<String>` but joined with `joinToString` — would idiomatically be `listOf`. Magic indices `1`, `3`, `else` in the `when (i)` branch (`Main.kt:48–72`) skip `i=2` to mean "modified heuristic", which is invisible. | Bilingual code (Spanish prose comments + English identifiers + Spanish UI strings) is acceptable for a coursework repo but the `when (i) { 1, 3, else }` branch is genuinely confusing — `else` carries semantic meaning, not a fallback. | Replace `for (i in 1..3) { when (i) { 1->…; 3->…; else->… } }` with `for (algorithm in listOf(::fearlessOld, ::fearlessMeta, ::dp))`. Sequential indices → first-class function references. ~10 lines, big readability win. |
| **High** | `Main.kt:36–39` vs `Main.kt:19–35` | `items` (the "specific items" path, lines 19–35) is generated/assigned but **never used** in the benchmark loop. The loop only consumes `itemsArr[run-1]` (line 53, 60) or — for `i=2` — the original `items` variable (line 68). This means the `generateNewItems` flag is half-wired: turning it off has no effect on cases `i=1` and `i=3`. | Subtle dead-flag bug. A reviewer who actually runs the code will find that toggling `generateNewItems = false` doesn't do what the comment suggests. | Either delete the `generateNewItems` branch and `items` variable entirely, or wire it into all three benchmark paths consistently. Probably delete — the benchmark wants randomized batches anyway. |
| **Medium** | `NewFearless.kt:53–54`, `FearlessOld.kt:36–37` | `solution.sumBy { it.value }` — `sumBy` was deprecated in Kotlin 1.4 (2020) in favor of `sumOf`, which is type-safe. `Main.kt:74` already uses `sumOf` correctly. | Mixed use of deprecated and current APIs in the same module — uneven modernization signals an unfinished sweep. | Replace both occurrences with `sumOf { it.value }`. |
| **Medium** | `Main.kt:1` | No package declaration on any file. All five files sit in the default (root) package. | Default package is the Kotlin equivalent of dumping `.java` files in `src/`. Reviewers expect at least `com.jcellomarcano.knapsack` or similar. | Add `package` declarations (e.g. `package knapsack`) and move files under `src/main/kotlin/knapsack/`. |
| **Medium** | `Main.kt:2`, `NewFearless.kt:42`, `FearlessOld.kt:25` | `kotlin.random.Random` is used with no seed, so benchmarks are not reproducible across runs. The README implicitly promises evaluatable results. | Reviewer who clones to reproduce paper Table 1 cannot. | Take `Random(seed)` as a parameter to `generateItems`, default to a `Random.Default` for normal use, expose a seeded variant for the benchmark in `Main.kt`. |
| **Medium** | `Main.kt:94` | `medianVal = it.sorted().let { (it[it.size / 2] + it[(it.size - 1) / 2]) / 2 }` — integer division on `Int` values; the median of `[1, 2, 3, 4]` returns `2`, not `2.5`. | Statistically incorrect for even-length samples (and the benchmark always uses `numRuns = 10`, i.e. even). | Cast to `Double` before dividing: `(it[…] + it[…]) / 2.0`. |
| **Medium** | `src/main/kotlin/` (all 5 files) | **No tests.** No `src/test/kotlin`. README's project-structure section claims a `src/test/` tree that does not exist. | A heuristic that claims to consistently find near-optimal solutions and outperform DP needs *some* test, even if it is just one parameterized correctness check against `dynamicProgrammingKnapsack` on a small fixed input. | Add `src/test/kotlin/FearlessHeuristicTest.kt` with one JUnit5 test: for `n ≤ 20`, fearless solution value should be ≥ 0.9 × DP optimum. Requires a real build system (see Critical). |
| **Medium** | repo root | `0_1 Knapsack Problem Solutions.pdf` (272 KB) is committed at repo root. Filename has a space, which trips shell commands. | The paper itself is the most valuable artifact here, but a top-level PDF with a spaces-in-name clutters the repo and inflates clone size. | Move to `docs/knapsack-solutions.pdf`. Reference from README. |
| **Low** | `DPKnapSack.kt:1–27` | DP function works on `IntArray` of size `(n+1) × (capacity+1)` allocated up-front. For `n=1000, capacity=500` that is 500K ints (~2 MB) — fine, but the benchmark in `Main.kt:12,15` uses `numItems=1000, capacity=500`, and the algorithm is `O(n·W)` time and space. The DP variant uses the standard rollback table. | Correctness OK. No issue per se, but the comparison is unfair: the DP variant allocates 2 MB on every benchmark run inside `measureTimeMillis`, including the rollback walk. Documented in code = good. | (Optional) Mention in a one-line comment that the timing includes allocation + reconstruction, since that is the apples-to-apples comparison reported in the paper. |
| **Low** | `NewFearless.kt:1–2` | Top-level functions named `f1` and `f2`. `f1` computes value/weight ratio; `f2` is a capacity-fit predicate. | Single-letter top-level function names are the textbook "lambda named in haste" smell. | Rename to `valueDensity(item)` and `fitsIn(item, remaining)`. Pass as `::valueDensity`, `::fitsIn` from `Main.kt:68`. |
| **Low** | `Main.kt:103–105` | `Double.format(decimals: Int)` extension uses `"%.${decimals}f".format(this)`, which is `java.util.Locale.getDefault()`-dependent — locale-aware formatting will print `0,12345` on Spanish/European locales and break the pipe-delimited table. | Cosmetic, but the table layout breaks on a `LC_ALL=es_ES.UTF-8` shell, which is presumably the author's. | Use `"%.${decimals}f".format(java.util.Locale.US, this)` or `String.format(Locale.US, …)`. |
| **Low** | `FearlessBirth.iml:1` | Module file is named `FearlessBirth`, repo is `FearlessBirthHeuristic`, README title is `Fearless Birth Heuristic - Algorithm Design Project`. Three different names for the same artifact. | Cosmetic but reads as half-renamed. | Rename module file to `FearlessBirthHeuristic.iml` if `.iml` is kept tracked — but per Critical row above, `.iml` should be untracked entirely. |
| **Low** (info) | git history (`git log --all -p`) | Scanned for `key`, `secret`, `token`, `password`, `bearer`. **No secrets found** in working tree or history. The only match is `JPasswordField` (a Java Swing UI class name) in `.idea/uiDesigner.xml`. | Confirmation that history can be kept; no `git filter-repo` needed. | None. |
| **Low** (info) | branches | Only `main`. No stale feature branches. | Clean branch hygiene. | None. |
| **Low** (info) | commit history (7 commits total) | `788a9c6 Initial commit`, `259da97 Fix from Heuristic to Metaheuristic`, `e64ddd0 Fix from Heuristic to Metaheuristic` (two commits with identical messages — likely an amend-then-recommit). No embarrassing messages, no personal data. | Acceptable. The duplicate message is minor noise. | None — too small to warrant history rewrite. |

---

## 2. Public-readiness review

| Item | Status | Note |
|---|---|---|
| LICENSE | ✅ Present | MIT, added 2026-05-13. Appropriate for academic + portfolio use. |
| README | ⚠️ Deficient | Well-structured and well-written prose, **but factually wrong about build tooling**. Claims Gradle wrapper that does not exist; claims JDK 17 while `.idea/misc.xml` pins JDK 19 and `kotlinc.xml` targets JVM 1.8. Project-structure tree is aspirational, not actual. |
| Naming | ✅ OK | Repo name `FearlessBirthHeuristic`, README title, and paper title are consistent. The `.iml` mismatch noted above is cosmetic. No typos, no embarrassing placeholders ("test1", "asdf"). |
| `.gitignore` (root) | ❌ Missing | No root `.gitignore`. Only `.idea/.gitignore` exists, which ignores subtrees of `.idea/` — but `.idea/` itself is tracked. |
| Branch hygiene | ✅ OK | Only `main`. |
| Commit history | ✅ OK | 7 commits, no personal data, no large blobs in history. `.git/` is 384 KB. |
| Binary clutter | ⚠️ | `out/production/.../*.class` (~48 KB) tracked. `0_1 Knapsack Problem Solutions.pdf` (272 KB) tracked — defensible since it is the accompanying paper, but should live under `docs/`. |
| Personal data leakage | ✅ Clean | No emails, tokens, machine-local paths, or screenshots in tracked content. `.idea/*.xml` uses `$PROJECT_DIR$` variables, not absolute paths. |
| Secrets in history | ✅ Clean | `git log --all -p | grep -iE "key\|secret\|token\|password"` returns only `JPasswordField` (Swing class name). |

---

## 3. Strategic positioning verdict

### a) Does this code support the stated positioning?

**No, not directly — and only weakly indirectly.**

The positioning is "Senior Mobile Architect — Payments & AI Integration".
This repo is:

- Not mobile (no Android, no iOS, no React Native — pure JVM CLI).
- Not payments (it solves academic 0/1 Knapsack).
- Not AI (a greedy ratio-based heuristic does not credibly read as
  "AI integration"; it is classical combinatorial optimization).
- Not senior-level production code (no build system, no tests, no
  package structure, deprecated APIs mixed with current ones, magic
  numbers, three competing entry points).

The only signal that aligns is *Kotlin literacy* — and a recruiter who
opens this expecting Android Kotlin will find JVM Kotlin without even
a Gradle file. That is a worse signal than no repo at all.

### b) What single addition or refactor would most increase its portfolio value?

**A `RESULTS.md` (or expanded README "Results" section) that shows real
numbers from the paper — value, runtime, std dev — for `n ∈ {250, 500}`
against DP, GA, ACO — with a single matplotlib/Kotlin-rendered chart
checked into `docs/`.** Effort: **M (2–4 hours).**

This converts a code dump into a reproducible empirical study, which is
the actual artifact a reviewer would value (and which the paper already
contains — it just is not surfaced in the repo). It also gives the repo
a defensible reason to exist for a non-mobile/non-payments engineer:
*"I write rigorous benchmarks, not just code."*

A close second (smaller effort, smaller payoff): add a proper Gradle
wrapper + one JUnit5 correctness test. Effort: **S (1 hour).**

### c) Pin / polish / archive / private / delete?

**Recommendation: (iii) stay public, unpinned + apply minimum hygiene.
Consider GitHub-archiving the repo once hygiene fixes ship.**

The repo is a legitimate academic artifact with a real co-authored
paper attached, a real LICENSE, and a real README. Deleting or making
it private throws away a credible "I shipped a coursework project
to public completion" signal — small, but real. **Pinning it, however,
would actively hurt** the stated positioning because pinning is the
explicit claim "this is among my best/most-representative work" — and
this is neither mobile, payments, nor AI. The right place for this
repo on the profile is *unpinned, public, with hygiene fixed*, so that
a recruiter who scrolls past the pinned-projects fold sees an
academically-grounded engineer rather than a broken README.

GitHub-archiving (the `Settings → Archive this repository` toggle) is
worth considering after hygiene fixes ship: it locks the repo
read-only and labels it `Archived` on the profile, which is the
clearest possible signal that "this is a finished academic artifact,
not current work-in-progress" — eliminating the "abandoned project"
penalty while preserving the Kotlin-literacy signal.

### d) Minimum viable polish (1–2 hours)

Three commits, scoped tightly. Implemented in this PR; see commit log
on branch `polish/audit-20260513`:

1. **`chore(repo): add .gitignore, stop tracking IDE and build artifacts`** — add a standard Kotlin/JVM `.gitignore`; `git rm --cached` the `out/`, `.idea/`, and `.iml` files. ~10 min.
2. **`docs(readme): rewrite README to match actual project state`** — remove Gradle claims, document the actual run path (open in IntelliJ → run `Main.kt`), keep the algorithm explanation and paper context intact, move the PDF to `docs/`. ~30 min.
3. **`refactor(main): consolidate three competing main() entry points`** — keep `Main.kt` as the only entry point. Remove the demo `main()` blocks from `FearlessOld.kt` and `NewFearless.kt`. ~10 min.

**Out of scope for the 1–2-hour window**, captured in
`REFACTOR_BACKLOG.md` below: adding a Gradle wrapper, adding tests,
fixing magic numbers, renaming `f1`/`f2`, fixing the median bug, the
`generateNewItems` dead-flag, package declarations, locale-aware
formatting.

### e) IP / NDA / licensing / trademark risks

**None identified.**

- The paper credits two authors (Jesús Marcano, Pedro Samuel Fagundez).
  If the co-author has not consented to public posting of the paper PDF,
  that is a courtesy issue, not a legal one — academic coursework is
  typically jointly owned. **Owner action:** confirm with the co-author
  before keeping the PDF tracked. If unsure, move the PDF to private
  storage and link from the README instead.
- No employer / NDA exposure: this is university coursework
  (Universidad Simón Bolívar), pre-dating any commercial engagement.
- MIT LICENSE is appropriate and unambiguous.
- "Fearless Birth Heuristic" is a non-trademarked Spanish-language
  phrase ("El que tenga miedo a morir, que no nazca") — no risk.

---

## 4. Refactor backlog (also in `REFACTOR_BACKLOG.md`)

Ranked by impact-to-effort. The top three were implemented in this PR.

| Rank | Item | Effort | Impact | Status |
|---|---|---|---|---|
| 1 | Add `.gitignore`, stop tracking `out/`, `.idea/`, `*.iml` | S (10m) | High — fixes the worst hygiene signal | **DONE** (commit 1) |
| 2 | Rewrite README to match reality (no Gradle, actual run path, move PDF to `docs/`) | S (30m) | Critical — current README actively misleads | **DONE** (commit 2) |
| 3 | Remove duplicate `main()` from `FearlessOld.kt` and `NewFearless.kt` | S (10m) | High — single entry point, no ambiguity | **DONE** (commit 3) |
| 4 | Add proper Gradle wrapper (`gradle init --type kotlin-application`) | M (45m) | High — makes README's `./gradlew run` true; enables tests | Backlog |
| 5 | Add one JUnit5 test: fearless ≥ 0.9 × DP optimum on `n ≤ 20` fixed input | S (30m, requires #4) | High — turns claim into evidence | Backlog |
| 6 | Add `RESULTS.md` with paper benchmark numbers + one chart | M (2–4h) | High — reframes repo as empirical study | Backlog |
| 7 | Replace magic `threshold = 2.6` / `1.5` with named `const val` + justification | S (5m) | Medium | Backlog |
| 8 | Rename `f1` → `valueDensity`, `f2` → `fitsIn` | S (5m) | Medium | Backlog |
| 9 | Replace deprecated `sumBy` with `sumOf` (`FearlessOld.kt:36–37`, `NewFearless.kt:53–54`) | S (2m) | Medium | Backlog |
| 10 | Fix integer-division median bug (`Main.kt:94`) — cast to `Double` | S (2m) | Medium | Backlog |
| 11 | Replace `for (i in 1..3) { when (i) … }` with `listOf(::fearlessOld, ::fearlessMeta, ::dp)` loop | S (15m) | Medium | Backlog |
| 12 | Delete or wire `generateNewItems` flag (currently half-dead) | S (5m) | Medium | Backlog |
| 13 | Add `package` declarations; move sources under `src/main/kotlin/knapsack/` | S (10m, requires #4) | Medium | Backlog |
| 14 | Seed `Random` in benchmark for reproducibility | S (10m) | Medium | Backlog |
| 15 | Locale-pin `Double.format` (`Main.kt:103`) to `Locale.US` | S (2m) | Low | Backlog |
| 16 | GitHub-archive the repo after items 4–6 ship (or instead of them, if owner decides) | S (1 click) | Strategic | Owner decision |

---

## TL;DR

**What was done:** Full audit of all 5 Kotlin source files, IDE config,
git history (no secrets), and README accuracy. Three hygiene commits
landed on `polish/audit-20260513`: added a proper `.gitignore` and
untracked `out/` + `.idea/` + `.iml`, rewrote the README to describe
the project as it actually exists (no Gradle, IntelliJ-only) with the
paper PDF moved to `docs/`, and removed the duplicate `main()` blocks
in `FearlessOld.kt` and `NewFearless.kt` so `Main.kt` is the sole
entry point. **What remains:** 13 backlog items — most are 5–30 min
fixes (deprecated `sumBy`, integer-division median bug, `f1`/`f2`
renames, magic-number `const val`s), plus three larger items (real
Gradle wrapper, one JUnit5 test, `RESULTS.md` with paper numbers)
that would together cost ~3 hours and turn the repo from
"finished coursework artifact" into "small but real empirical study".
**Owner decision needed:** (1) Confirm co-author Pedro Samuel
Fagundez consents to the paper PDF being publicly committed;
(2) decide between **(a)** ship the three backlog M-items (Gradle +
test + `RESULTS.md`) to make this a defensible unpinned-public repo,
or **(b)** GitHub-archive it now and move on — both are honest
positions, but **do not pin it** in either case, since this repo is
not mobile, not payments, and not AI, and so does not directly
support the stated "Senior Mobile Architect — Payments & AI
Integration" positioning.
