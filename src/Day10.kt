val directions = listOf(Vector2i(1, 0), Vector2i(0, 1), Vector2i(-1, 0), Vector2i(0, -1))

data class Vector2i(val x: Int, val y: Int) {
    operator fun plus(other: Vector2i) = Vector2i(x + other.x, y + other.y)
}

enum class PipeType(val char: Char, val move: (Vector2i) -> Vector2i) {
    VERTICAL('|', { it }),
    HORIZONTAL('-', { it }),
    NORTH_EAST('L', { if (it.y == 1) Vector2i(1, 0) else Vector2i(0, -1) }),
    NORTH_WEST('J', { if (it.y == 1) Vector2i(-1, 0) else Vector2i(0, -1) }),
    SOUTH_EAST('F', { if (it.y == -1) Vector2i(1, 0) else Vector2i(0, 1) }),
    SOUTH_WEST('7', { if (it.y == -1) Vector2i(-1, 0) else Vector2i(0, 1) }),
    START('S', { it }); // we will never move after going through the start

    companion object {
        fun fromChar(char: Char) = entries.find { it.char == char }
    }
}

fun <T> List<List<T>>.get(vec: Vector2i) = this[vec.y][vec.x]

fun <T> List<MutableList<T>>.set(vec: Vector2i, value: T) = this[vec.y].set(vec.x, value)

fun main() {
    fun findStart(map: List<List<PipeType?>>): Vector2i {
        val y = map.indexOfFirst { it.contains(PipeType.START) }
        val x = map[y].indexOfFirst { it == PipeType.START }
        return Vector2i(x, y)
    }

    fun parseInput(input: List<String>) = input.map { it.toList().map(PipeType::fromChar) } // input[y][x]

    fun List<List<Any>>.inBounds(pos: Vector2i) =
        pos.x >= 0 && pos.y >= 0 && pos.x < this.first().size && pos.y < this.size

    fun discoverExternalNodes(traversed: List<MutableList<Boolean>>, from: Vector2i) {
        if (!traversed.inBounds(from) || traversed.get(from)) {
            return
        }
        traversed.set(from, true)
        for (direction in directions) {
            discoverExternalNodes(traversed, from + direction)
        }
    }
    fun discoverAllExternalNodes(traversed: List<MutableList<Boolean>>) {
        repeat(traversed.size) {
            discoverExternalNodes(traversed, Vector2i(0, it))
            discoverExternalNodes(traversed, Vector2i(traversed.first().size - 1, it))
        }
        repeat(traversed.first().size) {
            discoverExternalNodes(traversed, Vector2i(it, 0))
            discoverExternalNodes(traversed, Vector2i(it, traversed.size - 1))
        }
    }

    fun day10(input: List<String>) = parseInput(input).let { map ->
        val start = findStart(map)
        val traversed = map.map { line -> line.map { false }.toMutableList() }
        var position = start
        var direction = directions.find { map.get(position + it) != null } ?: error("no starting position")
        var moves = 0

        do {
            traversed.set(position, true)
            position += direction
            direction = map.get(position)?.move?.invoke(direction) ?: error("invalid move")
            moves += 1
        } while (position != start)
        (moves / 2).println() // Part 1
        discoverAllExternalNodes(traversed)
        (traversed.sumOf { line -> line.count { !it } }).println() // Part 2
    }

    val testInput = readInput("Day10_test")
    val input = readInput("Day10")

    println("-- testing")
    day10(testInput)
    println("-- actual")
    day10(input)
}
