val digits = mapOf(
    "zero" to "z0o",
    "one" to "o1e",
    "two" to "t2o",
    "three" to "t3e",
    "four" to "f4r",
    "five" to "f5e",
    "six" to "s6x",
    "seven" to "s7n",
    "eight" to "e8t",
    "nine" to "n9e",
)

fun main() {
    fun addStringDigits(line: String): String = digits.entries.fold(line) { it, entry ->
        it.replace(entry.key, entry.value)
    }

    fun getCalibrationValue(line: String): Int = line
        .filter { it in '0'..'9' }
        .let { "${it.first()}${it.last()}".toInt() }

    fun part1(input: List<String>) =
        input.sumOf { getCalibrationValue(it) }

    fun part2(input: List<String>) =
        input.sumOf { getCalibrationValue(addStringDigits(it)) }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 142)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
