package com.akobusinska.letsplay.utils.xmlParsers

import android.util.Xml
import com.akobusinska.letsplay.data.xml.IBoardGames
import com.akobusinska.letsplay.data.xml.Tags.BOARD_GAME
import com.akobusinska.letsplay.data.xml.Tags.BOARD_GAMES_LIST
import com.akobusinska.letsplay.data.xml.Tags.COLLECTION
import com.akobusinska.letsplay.data.xml.Tags.COLLECTION_ITEM
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
        val boardGamesIdsParser = BoardGamesIdsParser()
        val boardGameDetailsParser = BoardGameDetailsParser()

        parser.require(XmlPullParser.START_TAG, null, BOARD_GAMES_LIST.fieldName)

        while (parser.next() != XmlPullParser.END_TAG) {

            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            if (parser.name == BOARD_GAME.fieldName) {
                if (searchResult == SearchResult.IDS) {
                    entries.add(boardGamesIdsParser.readBoardGame(parser))
                } else
                    entries.add(boardGameDetailsParser.readBoardGameDetails(parser))
            } else {
                skip(parser)
            }
        }

        return entries
    }

    fun parseUserCollection(inputStream: InputStream): List<*> {
        inputStream.use { inputStream ->
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()

            return readCollection(parser)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readCollection(parser: XmlPullParser): List<*> {
        val entries = mutableListOf<IBoardGames>()

        parser.require(XmlPullParser.START_TAG, null, COLLECTION.fieldName)

        while (parser.next() != XmlPullParser.END_TAG) {

            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            if (parser.name == COLLECTION_ITEM.fieldName) {
                entries.add(CollectionParser().readCollection(parser))

            } else {
                skip(parser)
            }
        }

        return entries
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