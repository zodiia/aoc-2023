import kotlin.math.max

fun main() {
    fun forEachGame(input: List<String>, cb: (Int, Sequence<Pair<String, String>>) -> Unit) {
        input.forEachIndexed { idx, line ->
            val sets = line.split(": ")[1]
                .splitToSequence(", ", "; ")
                .map { set -> set.split(" ").let { it[0] to it[1] } }
            val gameId = idx + 1
    
            cb(gameId, sets)
        }
    }
    
    fun part1(input: List<String>): Int {
        var res = 0
        val cubesMaxAmount = mapOf("red" to 12, "green" to 13, "blue" to 14)
        
        forEachGame(input) { gameId, sets ->
            if (sets.all { (amount, color) ->
                cubesMaxAmount.any { it.key == color && it.value >= amount.toInt() }
            }) {
                res += gameId
            }
        }
        return res
    }
    
    fun part2(input: List<String>): Int {
        var res = 0
    
        forEachGame(input) { _, sets ->
            val minAmount = HashMap<String, Int>()
    
            sets.forEach { (amount, color) ->
                minAmount[color] = max(minAmount[color] ?: 0, amount.toInt())
            }
            res += (minAmount["red"] ?: 0) * (minAmount["green"] ?: 0) * (minAmount["blue"] ?: 0)
        }
        return res
    }
    
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 8)
    check(part2(testInput) == 2286)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
