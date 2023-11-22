package com.akobusinska.letsplay.data.xml

enum class Tags(val fieldName: String) {
    BOARD_GAMES_LIST("boardgames"),
    BOARD_GAME("boardgame"),
    GAME_ID("objectid"),
    NAME("name"),
    PRIMARY("primary"),
    MIN_PLAYERS("minplayers"),
    MAX_PLAYERS("maxplayers"),
    MIN_PLAYTIME("minplaytime"),
    MAX_PLAYTIME("maxplaytime"),
    STARTING_AGE("age"),
    DESCRIPTION("description"),
    CATEGORY("boardgamecategory"),
    EXPANSION("boardgameexpansion"),
    COVER("image"),
    COLLECTION("items"),
    COLLECTION_ITEM("item")
}