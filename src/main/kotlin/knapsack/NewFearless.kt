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
    // Compute the value/weight ratio once per item; previously sortedByDescending
    // and the two .filter passes recomputed it ~2(n + n log n) times.
    val scored = items.map { it to valueDensity(it) }
    val (fearless, cautious) = scored.partition { (_, density) -> density >= threshold }

    val fearlessSorted = fearless.sortedByDescending { it.second }.map { it.first }
    val cautiousSorted = cautious.sortedByDescending { it.second }.map { it.first }

    val selected = mutableListOf<Item>()
    var remaining = capacity

    fun tryAdd(item: Item) {
        if (fitsIn(item, remaining)) {
            selected.add(item)
            remaining -= item.weight
        }
    }

    fearlessSorted.forEach(::tryAdd)
    cautiousSorted.forEach(::tryAdd)

    return selected
}
