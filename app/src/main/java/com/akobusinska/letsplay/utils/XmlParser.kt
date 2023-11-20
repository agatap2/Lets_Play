package com.akobusinska.letsplay.utils

import android.util.Xml
import com.akobusinska.letsplay.data.xml.BoardGame
import com.akobusinska.letsplay.data.xml.BoardGamesSearchResult
import com.akobusinska.letsplay.data.xml.IBoardGames
import com.akobusinska.letsplay.data.xml.Tags.BOARD_GAME
import com.akobusinska.letsplay.data.xml.Tags.BOARD_GAMES_LIST
import com.akobusinska.letsplay.data.xml.Tags.COVER
import com.akobusinska.letsplay.data.xml.Tags.DESCRIPTION
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
import java.io.InputStream

class XmlParser {

    enum class SearchResult { IDS, DETAILS }

    fun parseGamesList(inputStream: InputStream, searchResult: SearchResult): List<*> {
        inputStream.use { inputStream ->
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()

            return readBoardGames(parser, searchResult)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readBoardGames(parser: XmlPullParser, searchResult: SearchResult): List<*> {
        val entries = mutableListOf<IBoardGames>()

        parser.require(XmlPullParser.START_TAG, null, BOARD_GAMES_LIST.fieldName)

        while (parser.next() != XmlPullParser.END_TAG) {

            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            if (parser.name == BOARD_GAME.fieldName) {
                if (searchResult == SearchResult.IDS)
                    entries.add(readBoardGame(parser))
                else
                    entries.add(readBoardGameDetails(parser))
            } else {
                skip(parser)
            }
        }

        return entries
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readBoardGame(parser: XmlPullParser): BoardGamesSearchResult {
        parser.require(XmlPullParser.START_TAG, null, BOARD_GAME.fieldName)

        val objectId = parser.getAttributeValue(null, GAME_ID.fieldName)
        var name = ""

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                NAME.fieldName -> name = readBoardGameName(parser)
                else -> skip(parser)
            }
        }

        return BoardGamesSearchResult(objectId, name)
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
    private fun readBoardGameDetails(parser: XmlPullParser): BoardGame {
        var minPlayers: Int? = null
        var maxPlayers: Int? = null
        var minPlayTime: Int? = null
        var maxPlayTime: Int? = null
        var age: Int? = null
        var name: String? = null
        var description: String? = null
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
                COVER.fieldName -> image = readImage(parser)
                else -> skip(parser)
            }
        }

        return BoardGame(
            minPlayers,
            maxPlayers,
            minPlayTime,
            maxPlayTime,
            age,
            name,
            description,
            image
        )
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