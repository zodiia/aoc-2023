import kotlin.math.max

fun <T> List<T>.split(elem: T): List<List<T>> = this.fold(ArrayList<ArrayList<T>>().apply { add(ArrayList()) }) { acc, it ->
    if (it == elem) acc.add(ArrayList()) else acc.last().add(it)
    acc
}

fun <T : Comparable<T>> ClosedRange<T>.intersectWith(other: ClosedRange<T>) =
    this.start <= other.endInclusive && this.endInclusive >= other.start

val LongRange.size: Long
    get() = last - first + 1L

fun <T> MutableCollection<T>.filterAndRemove(predicate: (T) -> Boolean): List<T> = filter(predicate).also(this::removeAll)

fun main() {
    data class RangeMapping(val from: LongRange, val to: LongRange) {
        val size: Long
            get() = from.size

        fun splitAfter(size: Long) = RangeMapping(from.first..<from.first + size, to.first..<to.first + size) to
            RangeMapping(from.first + size..from.last, to.first + size..to.last)

        fun getMappedValue(value: Long) = value + (to.first - from.first)

        override operator fun equals(other: Any?) =
            other != null && other is RangeMapping && from.first == other.from.first && to.last == other.to.last
    }

    data class Almanac(
        val seeds: Collection<Long>,
        val seedRanges: List<LongRange>,
        val mapping: Collection<RangeMapping>,
    )

    fun Collection<RangeMapping>.getMappedValue(value: Long) = find { it.from.contains(value) }?.getMappedValue(value) ?: value

    fun mergeRangeMappings(fromRanges: Collection<RangeMapping>, toRanges: Collection<RangeMapping>): List<RangeMapping> {
        val remainingRanges = fromRanges.toMutableList()
        val newRanges = ArrayList<RangeMapping>()

        toRanges.forEach { range ->
            val contained = remainingRanges.filterAndRemove { it.to.intersectWith(range.from) }.sortedBy { it.to.first }.toMutableList()
            val firstSplit = contained.first().let { it.splitAfter(range.from.first - it.to.first) }
            val lastSplit = contained.last().let { it.splitAfter(range.from.last - it.to.first + 1L) }

            contained.filterIndexed { idx, _ -> idx == 0 || idx == contained.size - 1 }.let { contained.removeAll(it) }
            contained.addAll(setOf(lastSplit.first, firstSplit.second))
            if (firstSplit.first.size > 0L) {
                remainingRanges.add(firstSplit.first)
            }
            if (lastSplit.second.size > 0L) {
                remainingRanges.add(lastSplit.second)
            }
            if (firstSplit.second.size > range.size || lastSplit.first.size > range.size || firstSplit.second == lastSplit.first) {
                contained.clear()
                contained.add(RangeMapping(firstSplit.second.from.first..lastSplit.first.from.last, firstSplit.second.to.first..lastSplit.first.to.last))
            }
            if (contained.size == 0) {
                contained.add(lastSplit.first)
            }
            contained.forEach {
                val position = it.to.first - range.from.first
                val inclusiveLength = it.to.last - it.to.first

                newRanges.add(RangeMapping(it.from, (range.to.first + position)..(range.to.first + position + inclusiveLength)))
            }
        }
        newRanges.addAll(remainingRanges)
        return newRanges
    }

    fun parseInput(input: List<String>): Almanac {
        val seeds = Regex("\\d+").findAll(input.first()).map { it.value.toLong() }.toSet()
        val seedRanges = seeds.windowed(2, 2) { it[0]..<it[0] + it[1] }
        val maps = input.drop(2).split("").map { map -> map.drop(1).map { line ->
            Regex("\\d+").findAll(line).map { it.value.toLong() }.toList().let { RangeMapping(it[1]..<it[1] + it[2], it[0]..<it[0] + it[2]) }
        } }
        val mapping = maps.fold(listOf(RangeMapping(0..UInt.MAX_VALUE.toLong(), 0..UInt.MAX_VALUE.toLong()))) { acc, cur ->
            mergeRangeMappings(acc, cur)
        }

        return Almanac(seeds, seedRanges, mapping.sortedBy { it.from.first })
    }

    fun part1(input: List<String>) = parseInput(input).let { almanac ->
        almanac.seeds.minOf { seed -> almanac.mapping.getMappedValue(seed) }
    }

    fun part2(input: List<String>) = parseInput(input).let { almanac ->
        almanac.seedRanges.minOf { range ->
            almanac.mapping.filter { it.from.intersectWith(range) }
                .minOf { it.getMappedValue(max(it.from.first, range.first)) }
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == 35L)
    check(part2(testInput) == 46L)

    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}
