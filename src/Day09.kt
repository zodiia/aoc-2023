fun main() {
    fun List<Long>.deriveSequence(): List<Long> =
        this.windowed(2, 1, false).fold(ArrayList()) { acc, it -> acc.apply { add(it[1] - it[0]) } }

    fun List<Long>.deriveToZero(): List<List<Long>> = ArrayList<List<Long>>().also {
        it.add(this)
        while (it.last().any { v -> v != 0L }) {
            it.add(it.last().deriveSequence())
        }
    }

    fun List<List<Long>>.getNextNumber(op: Long.(Long) -> Long): Long = this.reversed().map(List<Long>::toMutableList).run {
        onEachIndexed { idx, it ->
            if (idx == 0) it.add(0) else it.add(op.invoke(this[idx].last(), this[idx - 1].last()))
        }
    }.last().last()

    fun parseInput(input: List<String>): List<List<Long>> =
        input.map { line -> Regex("-?\\d+").findAll(line).map { it.value.toLong() }.toList() }

    fun part1(input: List<String>) =
        parseInput(input).sumOf { seq -> seq.deriveToZero().getNextNumber { this + it } }

    fun part2(input: List<String>) =
        parseInput(input).sumOf { seq -> seq.deriveToZero().map { it.reversed() }.getNextNumber { this - it } }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 114L)
    check(part2(testInput) == 2L)

    val input = readInput("Day09")
    part1(input).println()
    part2(input).println()
}
