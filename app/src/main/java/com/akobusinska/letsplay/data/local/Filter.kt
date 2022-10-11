package com.akobusinska.letsplay.data.local

data class Filter(
    var numberOfPlayers: Int = 2,
    var maxPlaytime: Int = 480,
    var age: Int = 18,
    var excludeRecommendedForMore: Boolean = false
)
