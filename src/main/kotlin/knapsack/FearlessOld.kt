package knapsack

// Greedy-by-density variant: sort all items by value/weight descending
// and fill the knapsack until the next item no longer fits. Pre-dates
// the threshold-split "fearless / cautious" variant in NewFearless.kt.
fun selectItemsFearless(items: List<Item>, capacity: Int): List<Item> {
    val fearlessItems = items.sortedByDescending { it.value.toDouble() / it.weight }
    val survivingItems = mutableListOf<Item>()

    var remainingCapacity = capacity
    for (item in fearlessItems) {
        if (item.weight <= remainingCapacity) {
            survivingItems.add(item)
            remainingCapacity -= item.weight
        }
    }

    return survivingItems
}
