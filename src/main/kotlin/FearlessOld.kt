

// Esta función representa el concepto de "no nacer" en la frase, ya que se enfoca en seleccionar
// solo elementos con alta relación valor-peso (más dispuestos a "arriesgarse a morir").
fun selectItemsFearless(items: List<Item>, capacity: Int): List<Item> {
    val fearlessItems = items.sortedByDescending { it.value.toDouble() / it.weight }
    val survivingItems = mutableListOf<Item>()

    var remainingCapacity = capacity
    for (item in fearlessItems) {
        if (item.weight <= remainingCapacity) {
            survivingItems.add(item) // El elemento es considerado y "arriesga morir".
            remainingCapacity -= item.weight
        }
    }

    return survivingItems
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

    // Aplicamos la heurística basada en la frase "El que tenga miedo a morir, que no nazca".
    val solution = selectItemsFearless(items, capacity)
    println("Items seleccionados (sobrevivientes): $solution")
    println("Valor total: ${solution.sumBy { it.value }}")
    println("Peso total: ${solution.sumBy { it.weight }}")
}
