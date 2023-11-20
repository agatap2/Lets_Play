package com.akobusinska.letsplay.data.remote

import com.akobusinska.letsplay.data.xml.BoardGame
import com.akobusinska.letsplay.data.xml.BoardGamesSearchResult
import com.akobusinska.letsplay.utils.XmlParser
import com.akobusinska.letsplay.utils.XmlParser.SearchResult
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.net.URL

class GameService {

    @Throws(XmlPullParserException::class, IOException::class)
    fun loadGamesListXmlFromNetwork(gameName: String): List<*> {
        val urlString = "https://boardgamegeek.com/xmlapi/search?search=${
            gameName.replace(
                " ",
                "%20"
            )
        }"

        return downloadXML(urlString)?.use { stream ->
            XmlParser().parseGamesList(stream, SearchResult.IDS)
        } ?: emptyList<BoardGamesSearchResult>()
    }

    @Throws(XmlPullParserException::class, IOException::class)
    fun loadGameDetailsXmlFromNetwork(ids: List<String>): List<*> {

        val gameIds = ids.joinToString(",")
        val urlString = "https://boardgamegeek.com/xmlapi/boardgame/$gameIds"

        return downloadXML(urlString)?.use { stream ->
            XmlParser().parseGamesList(stream, SearchResult.DETAILS)
        } ?: emptyList<BoardGame>()
    }

    @Throws(IOException::class)
    private fun downloadXML(urlString: String): InputStream? {
        val url = URL(urlString)

        return (url.openConnection() as? java.net.HttpURLConnection)?.run {
            readTimeout = 10000
            connectTimeout = 15000
            requestMethod = "GET"
            doInput = true
            connect()
            inputStream
        }
    }
}