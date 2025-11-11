package br.com.example.juscom

import kotlin.math.pow

object LevelingManager {

    private const val BASE_XP = 100.0
    private const val EXPONENT = 1.5

    /**
     * Calculates the total XP required to reach a specific level.
     * @param level The target level.
     * @return The total XP needed to achieve that level.
     */
    fun getPointsForLevel(level: Int): Long {
        if (level <= 1) return 0
        // Formula: base_xp * (level - 1)^exponent
        return (BASE_XP * (level - 1).toDouble().pow(EXPONENT)).toLong()
    }

    /**
     * Determines a user's current level based on their total XP.
     * @param points The user's total accumulated XP.
     * @return The calculated current level.
     */
    fun calculateLevelFromPoints(points: Long): Int {
        if (points < BASE_XP) return 1

        // This is the reverse of the getPointsForLevel formula:
        // level = ((points / base_xp)^(1/exponent)) + 1
        val level = (points / BASE_XP).pow(1 / EXPONENT) + 1
        return level.toInt()
    }

}
