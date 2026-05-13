fun f1(item: Item): Double = item.value.toDouble() / item.weight
fun f2(item: Item, remainingCapacity: Int): Boolean = item.weight <= remainingCapacity

fun fearlessMetaheuristic(
    items: List<Item>,
    capacity: Int,
    threshold: Double,
    f1: (Item) -> Double,
    f2: (Item, Int) -> Boolean
): List<Item> {
    val highVWItems = items.filter { f1(it) >= threshold }
    val lowVWItems = items.filter { f1(it) < threshold }

    val fearlessItems = highVWItems.sortedByDescending { f1(it) }
    val cautiousItems = lowVWItems.sortedByDescending { f1(it) }

    val selectedItems = mutableListOf<Item>()

    var remainingCapacity = capacity
    for (item in fearlessItems) {
        if (f2(item, remainingCapacity)) {
            selectedItems.add(item) // Selecciona elementos dispuestos a "arriesgarse a morir".
            remainingCapacity -= item.weight
        }
    }

    for (item in cautiousItems) {
        if (f2(item, remainingCapacity)) {
            selectedItems.add(item) // Selecciona elementos "temerosos de morir" si aún hay espacio.
            remainingCapacity -= item.weight
        }
    }

    return selectedItems
}
