data class EngineMap(
    val parts: HashSet<EnginePartNumber> = HashSet(),
    val symbols: HashSet<EngineSymbol> = HashSet(),
)

data class EnginePartNumber(
    val startX: Int,
    val endX: Int,
    val posY: Int,
    val number: Int,
) {
    fun adjacentTo(x: Int, y: Int): Boolean = y in (posY - 1)..(posY + 1)
            && startX <= (x + 1)
            && endX >= (x - 1)
}

data class EngineSymbol(
    val x: Int,
    val y: Int,
    val symbol: String,
) {
    fun gearRatio(map: EngineMap): Int {
        if (symbol != "*") {
            return 0
        }
        val partNumbers = map.parts.filter { it.adjacentTo(x, y) }
        if (partNumbers.size != 2) {
            return 0
        }
        return partNumbers[0].number * partNumbers[1].number
    }
}

fun main() {
    fun parseEngineMap(input: List<String>): EngineMap {
        val engineMap = EngineMap()

        input.forEachIndexed { lineIdx, line ->
            Regex("\\d+").findAll(line).forEach {
                engineMap.parts.add(EnginePartNumber(it.range.first, it.range.last, lineIdx, it.value.toInt()))
            }
            Regex("[^\\d.]").findAll(line).forEach {
                engineMap.symbols.add(EngineSymbol(it.range.first, lineIdx, it.value))
            }
        }
        return engineMap
    }

    fun part1(input: List<String>): Int = parseEngineMap(input).let { engineMap ->
        engineMap.parts
            .filter { part -> engineMap.symbols.any { part.adjacentTo(it.x, it.y) } }
            .sumOf(EnginePartNumber::number)
    }

    fun part2(input: List<String>): Int = parseEngineMap(input).let { engineMap ->
        engineMap.symbols.sumOf { it.gearRatio(engineMap) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 4361)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}
