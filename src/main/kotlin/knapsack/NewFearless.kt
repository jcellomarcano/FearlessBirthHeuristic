package knapsack

fun valueDensity(item: Item): Double = item.value.toDouble() / item.weight

fun fitsIn(item: Item, remainingCapacity: Int): Boolean = item.weight <= remainingCapacity

fun fearlessMetaheuristic(
    items: List<Item>,
    capacity: Int,
    threshold: Double,
    valueDensity: (Item) -> Double,
    fitsIn: (Item, Int) -> Boolean,
): List<Item> {
    val fearlessItems = items.filter { valueDensity(it) >= threshold }.sortedByDescending(valueDensity)
    val cautiousItems = items.filter { valueDensity(it) < threshold }.sortedByDescending(valueDensity)

    val selectedItems = mutableListOf<Item>()
    var remainingCapacity = capacity

    for (item in fearlessItems) {
        if (fitsIn(item, remainingCapacity)) {
            selectedItems.add(item)
            remainingCapacity -= item.weight
        }
    }
    for (item in cautiousItems) {
        if (fitsIn(item, remainingCapacity)) {
            selectedItems.add(item)
            remainingCapacity -= item.weight
        }
    }

    return selectedItems
}
