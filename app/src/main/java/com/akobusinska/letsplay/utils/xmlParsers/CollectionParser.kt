package com.akobusinska.letsplay.utils.xmlParsers

import com.akobusinska.letsplay.data.xml.BoardGamesSearchResult
import com.akobusinska.letsplay.data.xml.Tags
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

class CollectionParser {

    @Throws(XmlPullParserException::class, IOException::class)
    fun readCollection(parser: XmlPullParser): BoardGamesSearchResult {

        parser.require(XmlPullParser.START_TAG, null, Tags.COLLECTION_ITEM.fieldName)
        val objectId = parser.getAttributeValue(null, Tags.GAME_ID.fieldName)

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            skip(parser)
        }

        return BoardGamesSearchResult(objectId)
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