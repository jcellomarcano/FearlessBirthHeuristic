package knapsack

import java.util.Locale
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.system.measureTimeMillis

// Threshold tuned for the paper's n=250 / n=500 instances. Items with
// a value/weight ratio at or above this cutoff are placed in the
// "fearless" group and tried first; everything else falls back to
// the "cautious" group. See AUDIT.md item 7 and the paper's section
// 2.7 for the discussion behind the choice.
const val FEARLESS_THRESHOLD = 2.6

// Defaults for the benchmark harness — match the paper's n=500 / large
// problem-size regime. Override in main() or via your own driver.
const val NUM_ITEMS = 1000
const val MAX_VALUE = 59
const val MIN_VALUE = 10
const val MAX_WEIGHT = 19
const val MIN_WEIGHT = 2
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

    val results: List<List<Triple<Int, Int, Long>>> = algorithms.map { algorithm ->
        itemSets.map { items ->
            var selection: List<Item> = emptyList()
            val timeMillis = measureTimeMillis { selection = algorithm.solve(items, CAPACITY) }
            Triple(selection.sumOf { it.value }, selection.sumOf { it.weight }, timeMillis)
        }
    }

    println(listOf("Algoritmo", "Elementos", "Tiempo (ms)", "Valor", "Peso").joinToString(" | "))

    for ((index, algorithm) in algorithms.withIndex()) {
        for ((run, result) in results[index].withIndex()) {
            val (totalValue, totalWeight, executionTime) = result
            val executionTimeInSeconds = executionTime / 1000.0
            println("${algorithm.label} | Run ${run + 1} | ${executionTimeInSeconds.format(5)} s | $totalValue | $totalWeight")
        }
        val avgTime = results[index].map { it.third }.average() / 1000.0
        val maxVal = results[index].maxOf { it.first }
        val medianVal = results[index].map { it.first }.median()
        val meanVal = results[index].map { it.first }.average()
        val stdDevVal = sqrt(results[index].map { (it.first - meanVal) * (it.first - meanVal) }.average())

        println("Promedio de tiempo: ${avgTime.format(5)} s | Valor máximo: $maxVal | Mediana: ${medianVal.format(1)} | Desviación estándar: ${stdDevVal.format(4)}")
    }
}

fun List<Int>.median(): Double {
    require(isNotEmpty()) { "median of empty list is undefined" }
    val sorted = sorted()
    val mid = sorted.size / 2
    return if (sorted.size % 2 == 0) (sorted[mid - 1] + sorted[mid]) / 2.0 else sorted[mid].toDouble()
}

fun Double.format(decimals: Int): String = "%.${decimals}f".format(Locale.US, this)
