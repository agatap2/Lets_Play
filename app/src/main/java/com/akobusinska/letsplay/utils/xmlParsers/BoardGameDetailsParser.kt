package com.akobusinska.letsplay.utils.xmlParsers

import com.akobusinska.letsplay.data.xml.BoardGame
import com.akobusinska.letsplay.data.xml.Tags.BOARD_GAME
import com.akobusinska.letsplay.data.xml.Tags.CATEGORY
import com.akobusinska.letsplay.data.xml.Tags.COVER
import com.akobusinska.letsplay.data.xml.Tags.DESCRIPTION
import com.akobusinska.letsplay.data.xml.Tags.EXPANSION
import com.akobusinska.letsplay.data.xml.Tags.GAME_ID
import com.akobusinska.letsplay.data.xml.Tags.MAX_PLAYERS
import com.akobusinska.letsplay.data.xml.Tags.MAX_PLAYTIME
import com.akobusinska.letsplay.data.xml.Tags.MIN_PLAYERS
import com.akobusinska.letsplay.data.xml.Tags.MIN_PLAYTIME
import com.akobusinska.letsplay.data.xml.Tags.NAME
import com.akobusinska.letsplay.data.xml.Tags.PRIMARY
import com.akobusinska.letsplay.data.xml.Tags.STARTING_AGE
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

class BoardGameDetailsParser {

    @Throws(XmlPullParserException::class, IOException::class)
    fun readBoardGameDetails(parser: XmlPullParser): BoardGame {
        parser.require(XmlPullParser.START_TAG, null, BOARD_GAME.fieldName)

        val id: Int = parser.getAttributeValue(null, GAME_ID.fieldName).toInt()
        var minPlayers: Int? = null
        var maxPlayers: Int? = null
        var minPlayTime: Int? = null
        var maxPlayTime: Int? = null
        var age: Int? = null
        var name: String? = null
        var description: String? = null
        var categories: String? = null
        val expansions: ArrayList<Int> = arrayListOf()
        var image: String? = null


        while (parser.next() != XmlPullParser.END_TAG) {

            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                MIN_PLAYERS.fieldName -> minPlayers = readMinPlayers(parser)
                MAX_PLAYERS.fieldName -> maxPlayers = readMaxPlayers(parser)
                MIN_PLAYTIME.fieldName -> minPlayTime = readMinPlayTime(parser)
                MAX_PLAYTIME.fieldName -> maxPlayTime = readMaxPlayTime(parser)
                STARTING_AGE.fieldName -> age = readAge(parser)
                NAME.fieldName -> {
                    if (name.isNullOrBlank())
                        name = readBoardGameName(parser)
                    else skip(parser)
                }

                DESCRIPTION.fieldName -> description = readDescription(parser)
                CATEGORY.fieldName -> categories += readCategory(parser)
                EXPANSION.fieldName -> expansions.add(readExpansion(parser))
                COVER.fieldName -> image = readImage(parser)
                else -> skip(parser)
            }
        }

        return BoardGame(
            id,
            minPlayers,
            maxPlayers,
            minPlayTime,
            maxPlayTime,
            age,
            name,
            description,
            categories,
            expansions,
            image
        )
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readBoardGameName(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, null, NAME.fieldName)
        val primary = parser.getAttributeValue(null, PRIMARY.fieldName)
        val name = readText(parser)
        parser.require(XmlPullParser.END_TAG, null, NAME.fieldName)

        return if (primary == "true") name else ""
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readMinPlayers(parser: XmlPullParser): Int {
        parser.require(XmlPullParser.START_TAG, null, MIN_PLAYERS.fieldName)
        val minPlayers = readText(parser).toInt()
        parser.require(XmlPullParser.END_TAG, null, MIN_PLAYERS.fieldName)
        return minPlayers
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readMaxPlayers(parser: XmlPullParser): Int {
        parser.require(XmlPullParser.START_TAG, null, MAX_PLAYERS.fieldName)
        val maxPlayers = readText(parser).toInt()
        parser.require(XmlPullParser.END_TAG, null, MAX_PLAYERS.fieldName)
        return maxPlayers
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readMinPlayTime(parser: XmlPullParser): Int {
        parser.require(XmlPullParser.START_TAG, null, MIN_PLAYTIME.fieldName)
        val minPlayTime = readText(parser).toInt()
        parser.require(XmlPullParser.END_TAG, null, MIN_PLAYTIME.fieldName)
        return minPlayTime
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readMaxPlayTime(parser: XmlPullParser): Int {
        parser.require(XmlPullParser.START_TAG, null, MAX_PLAYTIME.fieldName)
        val maxPlayTime = readText(parser).toInt()
        parser.require(XmlPullParser.END_TAG, null, MAX_PLAYTIME.fieldName)
        return maxPlayTime
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readAge(parser: XmlPullParser): Int {
        parser.require(XmlPullParser.START_TAG, null, STARTING_AGE.fieldName)
        val age = readText(parser).toInt()
        parser.require(XmlPullParser.END_TAG, null, STARTING_AGE.fieldName)
        return age
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readDescription(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, null, DESCRIPTION.fieldName)
        val description = readText(parser)
        parser.require(XmlPullParser.END_TAG, null, DESCRIPTION.fieldName)
        return description
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readCategory(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, null, CATEGORY.fieldName)
        val category = readText(parser)
        parser.require(XmlPullParser.END_TAG, null, CATEGORY.fieldName)
        return category
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readExpansion(parser: XmlPullParser): Int {
        parser.require(XmlPullParser.START_TAG, null, EXPANSION.fieldName)
        val expansion = parser.getAttributeValue(null, GAME_ID.fieldName)
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            skip(parser)
        }

        return expansion.toInt()
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readImage(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, null, COVER.fieldName)
        val image = readText(parser)
        parser.require(XmlPullParser.END_TAG, null, COVER.fieldName)
        return image
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }
}