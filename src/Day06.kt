import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt
import kotlin.math.pow

const val EPSILON = 1e-10

fun main() {
    fun getWinCases(time: Double, distance: Double): Double {
        val first = ceil(((-time) + sqrt(time.pow(2) - 4 * (-1) * (-distance))) / -2 + EPSILON)
        val second = floor(((-time) - sqrt(time.pow(2) - 4 * (-1) * (-distance))) / -2 - EPSILON)
        return second - first + 1
    }

    fun parsePart1Input(input: List<String>) =
        input.map { line -> Regex("\\d+").findAll(line).map { it.value.toDouble() } }.let { it[0].zip(it[1]) }

    fun parsePart2Input(input: List<String>) =
        input.map { line -> Regex("\\d+").find(line.replace(" ", ""))?.value?.toDouble() ?: 0.0 }

    fun part1(input: List<String>) = parsePart1Input(input)
        .map { getWinCases(it.first, it.second) }
        .fold(1.0, Double::times)

    fun part2(input: List<String>) = parsePart2Input(input).let { getWinCases(it[0], it[1])}

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 288.0)

    val input = readInput("Day06")
    println(part1(input).toBigDecimal().toPlainString())
    println(part2(input).toBigDecimal().toPlainString())
}
