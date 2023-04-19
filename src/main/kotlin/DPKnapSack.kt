fun dynamicProgrammingKnapsack(items: List<Item>, capacity: Int): List<Item> {
    val n = items.size
    val dp = Array(n + 1) { IntArray(capacity + 1) }

    for (i in 1..n) {
        for (w in 0..capacity) {
            if (items[i - 1].weight <= w) {
                dp[i][w] = maxOf(dp[i - 1][w], dp[i - 1][w - items[i - 1].weight] + items[i - 1].value)
            } else {
                dp[i][w] = dp[i - 1][w]
            }
        }
    }

    val selectedItems = mutableListOf<Item>()
    var i = n
    var w = capacity
    while (i > 0 && w > 0) {
        if (dp[i][w] != dp[i - 1][w]) {
            selectedItems.add(items[i - 1])
            w -= items[i - 1].weight
        }
        i--
    }

    return selectedItems.reversed()
}
