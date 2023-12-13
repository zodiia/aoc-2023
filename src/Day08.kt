import kotlin.math.pow
import kotlin.math.sqrt

fun Long.pow(factor: Int) = toDouble().pow(factor).toLong()

fun Long.sqrt() = sqrt(toDouble()).toLong()

fun Long.primeFactors(): Map<Long, Int> {
    var current = this

    return (2..this.sqrt()).associateWithTo(HashMap()) {
        var divisions = 0
        while (current % it == 0L) {
            current /= it
            divisions += 1
        }
        divisions
    }.also { if (current != 1L) it[current] = 1 }.filterValues { it > 0 }
}

fun List<Long>.lcm() = fold(HashMap<Long, Int>()) { acc, it ->
    it.primeFactors().forEach { (k, v) -> if (acc.getOrDefault(k, 0) < v) acc[k] = v }
    acc
}.entries.fold(1L) { acc, (k, v) -> acc * k.pow(v) }

fun main() {
    data class CamelMap(
        val instructions: List<Char>,
        val directions: HashMap<String, Pair<String, String>>,
    )

    fun parseInput(input: List<String>) = CamelMap(
        input.first().toList(),
        input.drop(2).associateTo(HashMap()) {
            it.substring(0..2) to (it.substring(7..9) to it.substring(12..14))
        }
    )

    fun runPath(map: CamelMap, start: String, endChars: Int): Int {
        var instructions = 0
        var current = start

        do {
            current = if (map.instructions[instructions % map.instructions.size] == 'L') {
                map.directions[current]?.first ?: current
            } else {
                map.directions[current]?.second ?: current
            }
            instructions += 1
        } while (!current.takeLast(endChars).all { it == 'Z' })
        return instructions
    }

    fun runPaths(map: CamelMap, starts: List<String>) = starts.map { runPath(map, it, 1).toLong() }.lcm()

    fun part1(input: List<String>) = runPath(parseInput(input), "AAA", 3)

    fun part2(input: List<String>) = parseInput(input).let { map ->
        runPaths(map, map.directions.keys.filter { it.endsWith('A') })
    }

    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("Day08_test1")
    val testInput2 = readInput("Day08_test2")
    check(part1(testInput1) == 2)
    check(part2(testInput2) == 6L)

    val input = readInput("Day08")
    part1(input).println()
    part2(input).println()
}
