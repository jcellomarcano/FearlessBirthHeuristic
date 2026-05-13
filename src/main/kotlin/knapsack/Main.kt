package knapsack

import java.util.Locale
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.measureTimedValue

// Threshold tuned for the paper's n=250 / n=500 instances. Items with
// a value/weight ratio at or above this cutoff are placed in the
// "fearless" group and tried first; everything else falls back to
// the "cautious" group. See AUDIT.md item 7 and the paper's section
// 2.7 for the discussion behind the choice.
const val FEARLESS_THRESHOLD = 2.6

// Defaults for the benchmark harness — match the paper's n=500 / large
// problem-size regime. Override in main() or via your own driver.
const val NUM_ITEMS = 1000
const val MIN_VALUE = 10
const val MAX_VALUE = 59
const val MIN_WEIGHT = 2
const val MAX_WEIGHT = 19
const val CAPACITY = 500
const val NUM_RUNS = 10
const val BENCHMARK_SEED = 42L

fun generateItems(numItems: Int, maxValue: Int, maxWeight: Int, rng: Random = Random.Default): List<Item> =
    List(numItems) {
        Item(value = rng.nextInt(MIN_VALUE, maxValue + 1), weight = rng.nextInt(MIN_WEIGHT, maxWeight + 1))
    }

private data class Algorithm(
    val label: String,
    val solve: (List<Item>, Int) -> List<Item>,
)

data class RunResult(val totalValue: Int, val totalWeight: Int, val time: Duration)

fun main() {
    val rng = Random(BENCHMARK_SEED)
    val itemSets = List(NUM_RUNS) { generateItems(NUM_ITEMS, MAX_VALUE, MAX_WEIGHT, rng) }

    val algorithms = listOf(
        Algorithm("Heurística de selección") { items, cap -> selectItemsFearless(items, cap) },
        Algorithm("Heurística modificada") { items, cap ->
            fearlessMetaheuristic(items, cap, FEARLESS_THRESHOLD, ::valueDensity, ::fitsIn)
        },
        Algorithm("Programación dinámica") { items, cap -> dynamicProgrammingKnapsack(items, cap) },
    )

    val results: List<List<RunResult>> = algorithms.map { algorithm ->
        itemSets.map { items ->
            val (selection, duration) = measureTimedValue { algorithm.solve(items, CAPACITY) }
            RunResult(selection.sumOf { it.value }, selection.sumOf { it.weight }, duration)
        }
    }

    println(listOf("Algoritmo", "Elementos", "Tiempo (s)", "Valor", "Peso").joinToString(" | "))

    for ((index, algorithm) in algorithms.withIndex()) {
        val runs = results[index]
        for ((runIndex, result) in runs.withIndex()) {
            val seconds = result.time.toDouble(DurationUnit.SECONDS)
            println("${algorithm.label} | Run ${runIndex + 1} | ${seconds.format(5)} s | ${result.totalValue} | ${result.totalWeight}")
        }

        val values = runs.map { it.totalValue }
        val avgTimeSeconds = runs.map { it.time.toDouble(DurationUnit.SECONDS) }.average()
        val maxValue = values.max()
        val medianValue = values.median()
        val meanValue = values.average()
        val stdDevValue = sqrt(values.map { (it - meanValue) * (it - meanValue) }.average())

        println(
            "Promedio de tiempo: ${avgTimeSeconds.format(5)} s | " +
                "Valor máximo: $maxValue | " +
                "Mediana: ${medianValue.format(1)} | " +
                "Desviación estándar: ${stdDevValue.format(4)}",
        )
    }
}

fun List<Int>.median(): Double {
    require(isNotEmpty()) { "median of empty list is undefined" }
    val sorted = sorted()
    val mid = sorted.size / 2
    return if (sorted.size % 2 == 0) (sorted[mid - 1] + sorted[mid]) / 2.0 else sorted[mid].toDouble()
}

fun Double.format(decimals: Int): String = "%.${decimals}f".format(Locale.US, this)
