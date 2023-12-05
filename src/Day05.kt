fun <T> List<T>.toTriple(): Triple<T, T, T> {
    if (this.size != 3) {
        throw error("Invalid size for triple.")
    }
    return Triple(this[0], this[1], this[2])
}

fun <T> List<T>.split(elem: T): List<List<T>> = this.fold(ArrayList<ArrayList<T>>().apply { add(ArrayList()) }) { acc, it ->
    if (it == elem) acc.add(ArrayList()) else acc.last().add(it)
    acc
}

fun main() {
    data class Almanac(
        val seeds: Collection<Long>,
        val maps: List<Collection<Triple<Long, Long, Long>>>
    )

    fun parseInput(input: List<String>): Almanac {
        val seeds = Regex("\\d+").findAll(input.first()).map { it.value.toLong() }.toSet()
        val maps = input.drop(2).split("").map { map -> map.drop(1).map { line ->
            Regex("\\d+").findAll(line).map { it.value.toLong() }.toList().toTriple()
        } }

        return Almanac(seeds, maps)
    }

    fun getMappedValue(number: Long, mappings: Collection<Triple<Long, Long, Long>>) = mappings
        .find { number in it.second..<(it.second + it.third) }
        ?.let { number - it.second + it.first }
        ?: number

    fun part1(input: List<String>) = parseInput(input).let { almanac ->
        almanac.seeds.minOf { seed -> almanac.maps.fold(seed) { acc, cur -> getMappedValue(acc, cur) } }
    }

    fun part2(input: List<String>): Long {
        return input.size.toLong()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == 35L)

    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}
