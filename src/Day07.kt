val cardOrder = listOf('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2')
val cardOrderWithJoker = listOf('A', 'K', 'Q', 'T', '9', '8', '7', '6', '5', '4', '3', '2', 'J')

fun Collection<Char>.countLetters(joker: Boolean) = HashMap<Char, Int>().also { map ->
    this.forEach { map[it] = (map[it] ?: 0) + 1 }
    if (joker) {
        val jCards = map.remove('J') ?: 0
        map.maxByOrNull { map[it.key] ?: 0 }?.let {
            map[it.key] = it.value + jCards
        }
    }
}

fun <K, V> Map<K, V>.findKeys(where: (V) -> Boolean) = filterValues(where).keys

enum class CamelHandType(
    val priority: Int,
    val challenge: (Map<Char, Int>) -> Boolean,
) {
    FIVE_OF_A_KIND(7, { it.findKeys { v -> v == 5 }.isNotEmpty() }),
    FOUR_OF_A_KIND(6, { it.findKeys { v -> v == 4 }.isNotEmpty() }),
    FULL_HOUSE(5, {
        (it.findKeys { v -> v == 3 }.isNotEmpty() && it.findKeys { v -> v == 2 }.isNotEmpty())
                || it.findKeys { v -> v == 3}.size > 1
    }),
    THREE_OF_A_KIND(4, { it.findKeys { v -> v == 3 }.isNotEmpty() }),
    TWO_PAIRS(3, { it.findKeys { v -> v == 2 }.size >= 2 }),
    ONE_PAIR(2, { it.findKeys { v -> v == 2 }.size == 1 }),
    HIGH_CARD(1, { true })
}

fun Map<Char, Int>.getCamelType(): CamelHandType =
    CamelHandType.entries.find { it.challenge(this) } ?: CamelHandType.HIGH_CARD

data class CamelHand(val cards: List<Char>, val bet: Int, val joker: Boolean) : Comparable<CamelHand> {
    private val order: List<Char>
        get() = if (joker) cardOrderWithJoker else cardOrder

    override fun compareTo(other: CamelHand): Int {
        val thisType = cards.countLetters(joker).getCamelType()
        val otherType = other.cards.countLetters(joker).getCamelType()

        if (thisType.priority != otherType.priority) {
            return thisType.priority - otherType.priority
        }
        return this.cards.zip(other.cards)
            .map { order.indexOf(it.second) - order.indexOf(it.first) }
            .find { it != 0 } ?: 0
    }
}

fun main() {
    fun parseInput(input: List<String>, joker: Boolean): List<CamelHand> = input.map {
        CamelHand(it.substring(0..<5).toList(), it.substring(6).toInt(), joker)
    }

    fun day07(input: List<String>, part2: Boolean) = parseInput(input, part2)
        .sorted()
        .mapIndexed { idx, it -> it.bet * (idx + 1) }
        .sum()

    fun part1(input: List<String>): Int = day07(input, false)

    // Note: part2 doesn't work.
    // It will not get fixed, I gave up because I would probably need to rewrite pretty much everything.
    fun part2(input: List<String>): Int = day07(input, true)

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 6440)
    check(part2(testInput) == 5905)

    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()
}
