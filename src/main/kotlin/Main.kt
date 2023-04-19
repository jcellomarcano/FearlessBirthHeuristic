import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.system.measureTimeMillis

fun generateItems(numItems: Int, maxValue: Int, maxWeight: Int): List<Item> {
    return List(numItems) {
        Item(value = Random.nextInt(10, maxValue + 1), weight = Random.nextInt(2, maxWeight + 1))
    }
}

fun main() {
    val numItems = 1000
    val maxValue = 59
    val maxWeight = 19
    val capacity = 500
    val generateNewItems = true // Cambiar a 'false' si se desea utilizar un conjunto de elementos específico.
    val numRuns = 10

    val items = if (generateNewItems) {
        generateItems(numItems, maxValue, maxWeight)
    } else {
        // Añadir elementos específicos aquí si se desea utilizar un conjunto de elementos personalizado.
        listOf<Item>(
            Item(value = 60, weight = 10),
            Item(value = 100, weight = 20),
            Item(value = 120, weight = 30),
            Item(value = 70, weight = 40),
            Item(value = 80, weight = 50),
            Item(value = 30, weight = 10),
            Item(value = 40, weight = 20),
            Item(value = 90, weight = 30),
            Item(value = 20, weight = 10),
            Item(value = 10, weight = 5),
        )
    }
    val itemsArr = mutableListOf<List<Item>>()
    for (run in 1..numRuns){
        itemsArr.add(generateItems(numItems, maxValue, maxWeight))
    }

    val results = mutableListOf<List<Triple<Int, Int, Long>>>()

    // Ejecuta la heurística de selección de elementos.
    val threshold = 2.6
    for (i in 1..3) {
        val runResults = mutableListOf<Triple<Int, Int, Long>>()
        for (run in 1..numRuns) {
            val (selectedItems, executionTimeMillis) = when (i) {
                1 -> {
                    var result = listOf<Item>()
                    val time = measureTimeMillis {
                        result = selectItemsFearless(itemsArr[run-1], capacity)
                    }
                    Pair(result, time)
                }

                3 -> {
                    var result = listOf<Item>()
                    val time = measureTimeMillis {
                        result = dynamicProgrammingKnapsack(itemsArr[run-1], capacity)
                    }
                    Pair(result, time)
                }

                else -> {
                    var result = listOf<Item>()
                    val time = measureTimeMillis {
                        result = fearlessMetaheuristic(items, capacity, threshold, ::f1, ::f2)
                    }
                    Pair(result, time)
                }
            }

            runResults.add(Triple(selectedItems.sumOf { it.value }, selectedItems.sumOf { it.weight }, executionTimeMillis))
        }

        results.add(runResults)
    }
    val headers = arrayOf("Algoritmo", "Elementos", "Tiempo (ms)", "Valor", "Peso")
    val algorithms = arrayOf("Heurística de selección", "Heurística modificada", "Programación dinámica")
    println(headers.joinToString(separator = " | "))

    for (i in 0 until 3) {
        for (run in 0 until numRuns) {
            val (totalValue, totalWeight, executionTime) = results[i][run]
            val executionTimeInSeconds = executionTime / 1000.0 // Convierte a segundos

            println(
                "${algorithms[i]} | Run ${run + 1} | ${executionTimeInSeconds.format(5)} s | $totalValue | $totalWeight",
            )
        }
        val avgTime = results[i].map { it.third }.average() / 1000.0 // Convierte a segundos
        val maxVal = results[i].map { it.first }.maxOrNull() ?: 0
        val medianVal = results[i].map { it.first }.sorted().let { (it[it.size / 2] + it[(it.size - 1) / 2]) / 2 }
        val meanVal = results[i].map { it.first }.average()
        val stdDevVal = sqrt(results[i].map { (it.first - meanVal) * (it.first - meanVal) }.average())

        println("Promedio de tiempo: $avgTime ms | Valor máximo: $maxVal | Mediana: $medianVal | Desviación estándar: $stdDevVal")
    }
}

// Función de extensión para formatear un Double con un número específico de decimales
fun Double.format(decimals: Int): String {
    return "%.${decimals}f".format(this)
}
