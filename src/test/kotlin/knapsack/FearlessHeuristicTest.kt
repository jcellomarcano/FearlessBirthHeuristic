package knapsack

import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FearlessHeuristicTest {

    @Test
    fun `empty input returns empty selection`() {
        assertEquals(emptyList(), selectItemsFearless(emptyList(), capacity = 100))
        assertEquals(emptyList(), fearlessMetaheuristic(emptyList(), capacity = 100, threshold = 2.0, ::f1, ::f2))
        assertEquals(emptyList(), dynamicProgrammingKnapsack(emptyList(), capacity = 100))
    }

    @Test
    fun `zero capacity returns empty selection`() {
        val items = listOf(Item(value = 60, weight = 10), Item(value = 100, weight = 20))
        assertEquals(emptyList(), selectItemsFearless(items, capacity = 0))
        assertEquals(emptyList(), fearlessMetaheuristic(items, capacity = 0, threshold = 2.0, ::f1, ::f2))
        assertEquals(emptyList(), dynamicProgrammingKnapsack(items, capacity = 0))
    }

    @Test
    fun `solutions never exceed capacity`() {
        val rng = Random(seed = 42)
        val items = List(50) { Item(value = rng.nextInt(10, 60), weight = rng.nextInt(2, 20)) }
        val capacity = 200

        for (selection in listOf(
            selectItemsFearless(items, capacity),
            fearlessMetaheuristic(items, capacity, threshold = 2.6, ::f1, ::f2),
            dynamicProgrammingKnapsack(items, capacity),
        )) {
            assertTrue(selection.sumOf { it.weight } <= capacity)
        }
    }

    @Test
    fun `DP returns exact optimum on a known small instance`() {
        // Classic textbook 4-item instance — optimum = 220 (items 2 and 3).
        val items = listOf(
            Item(value = 60, weight = 10),
            Item(value = 100, weight = 20),
            Item(value = 120, weight = 30),
            Item(value = 70, weight = 40),
        )
        val dpValue = dynamicProgrammingKnapsack(items, capacity = 50).sumOf { it.value }
        assertEquals(220, dpValue)
    }

    @Test
    fun `fearless metaheuristic lands within 10 percent of DP optimum`() {
        // The paper's central empirical claim, scaled down: on random
        // instances small enough that DP is tractable as a reference,
        // the metaheuristic should reach at least 90% of the optimum.
        val rng = Random(seed = 12345)
        val items = List(20) { Item(value = rng.nextInt(10, 60), weight = rng.nextInt(2, 20)) }
        val capacity = 80

        val dpValue = dynamicProgrammingKnapsack(items, capacity).sumOf { it.value }
        val mhValue = fearlessMetaheuristic(items, capacity, threshold = 2.6, ::f1, ::f2).sumOf { it.value }

        assertTrue(
            mhValue >= 0.9 * dpValue,
            "fearlessMetaheuristic value $mhValue should be ≥ 90% of DP optimum $dpValue",
        )
    }
}
