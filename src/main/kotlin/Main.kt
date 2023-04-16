import kotlin.random.Random

fun generateItems(numItems: Int, maxValue: Int, maxWeight: Int): List<Item> {
    return List(numItems) {
        Item(value = Random.nextInt(1, maxValue + 1), weight = Random.nextInt(9, maxWeight + 1))
    }
}

fun main() {
    val numItems = 10000
    val maxValue = 100
    val maxWeight = 120
    val capacity = 97
    val generateNewItems = true // Cambiar a 'false' si se desea utilizar un conjunto de elementos específico.

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

    println("Items generados:")
    items.forEachIndexed { index, item ->
        println("Ítem ${index + 1}: Valor: ${item.value}, Peso: ${item.weight}")
    }

    // Ejecuta la heurística de selección de elementos.
    val threshold = 1.5
    for (i in 1..2) {
        if (i == 1) {
            val fearlessSolution = selectItemsFearless(items, capacity)
            println("Items seleccionados (heurística de selección): $fearlessSolution")
            println("Valor total: ${fearlessSolution.sumOf { it.value }}")
            println("Peso total: ${fearlessSolution.sumOf { it.weight }}")
        } else {
            val fearlessHeuristic = fearlessHeuristic(items, capacity, threshold)
            println("Items seleccionados (heurística de selección): $fearlessHeuristic")
            println("Valor total: ${fearlessHeuristic.sumOf { it.value }}")
            println("Peso total: ${fearlessHeuristic.sumOf { it.weight }}")
        }
    }
}
