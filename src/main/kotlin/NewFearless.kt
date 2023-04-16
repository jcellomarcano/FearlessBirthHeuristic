

fun fearlessHeuristic(items: List<Item>, capacity: Int, threshold: Double): List<Item> {
    val highVWItems = items.filter { it.value.toDouble() / it.weight >= threshold }
    val lowVWItems = items.filter { it.value.toDouble() / it.weight < threshold }

    val fearlessItems = highVWItems.sortedByDescending { it.value.toDouble() / it.weight }
    val cautiousItems = lowVWItems.sortedByDescending { it.value.toDouble() / it.weight }

    val selectedItems = mutableListOf<Item>()

    var remainingCapacity = capacity
    for (item in fearlessItems) {
        if (item.weight <= remainingCapacity) {
            selectedItems.add(item) // Selecciona elementos dispuestos a "arriesgarse a morir".
            remainingCapacity -= item.weight
        }
    }

    for (item in cautiousItems) {
        if (item.weight <= remainingCapacity) {
            selectedItems.add(item) // Selecciona elementos "temerosos de morir" si aún hay espacio.
            remainingCapacity -= item.weight
        }
    }

    return selectedItems
}

fun main() {
    val numItems = 10
    val maxValue = 100
    val maxWeight = 50
    val capacity = 150
    val items = generateItems(numItems, maxValue, maxWeight)

    println("Items generados:")
    items.forEachIndexed { index, item ->
        println("Ítem ${index + 1}: Valor: ${item.value}, Peso: ${item.weight}")
    }

    val threshold = 1.5
    val solution = fearlessHeuristic(items, capacity, threshold)

    println("Items seleccionados: $solution")
    println("Valor total: ${solution.sumBy { it.value }}")
    println("Peso total: ${solution.sumBy { it.weight }}")
}
