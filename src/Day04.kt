import kotlin.math.pow
import kotlin.math.roundToInt

fun main() {
    fun score(matchingCards: Int) =
        if (matchingCards == 0) 0 else (2.0).pow(matchingCards - 1).roundToInt()

    fun parseCardPacks(input: List<String>) = input.map { line ->
        line.split(":")[1].split("|").map { set ->
            Regex("\\d+").findAll(set).map { it.value.toInt() }
        }
    }

    fun part1(input: List<String>) =
        parseCardPacks(input).sumOf { sets -> score(sets[0].count { it in sets[1] }) }

    fun part2(input: List<String>) = parseCardPacks(input)
        .map { sets -> sets[0].count { it in sets[1] } }
        .let { packs ->
            val count = HashMap<Int, Int>()

            packs.forEachIndexed { idx, pack ->
                repeat(count.getOrDefault(idx, 1)) {
                    repeat(pack) { count[idx + it + 1] = count.getOrDefault(idx + it + 1, 1) + 1 }
                }
            }
            List(packs.size) { idx -> (count[idx] ?: 1) }
        }
        .sum()

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 30)

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}
