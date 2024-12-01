package com.akobusinska.letsplay.data.remote

import com.akobusinska.letsplay.data.xml.BoardGame
import com.akobusinska.letsplay.data.xml.BoardGamesSearchResult
import com.akobusinska.letsplay.utils.xmlParsers.XmlParser
import com.akobusinska.letsplay.utils.xmlParsers.XmlParser.SearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class GameService {

    @Throws(XmlPullParserException::class, IOException::class)
    suspend fun loadGamesListXmlFromNetwork(gameName: String): List<*> {
        val urlString = "https://boardgamegeek.com/xmlapi/search?search=${
            gameName.replace(
                " ",
                "%20"
            )
        }"

        return withContext(Dispatchers.IO) {
            downloadXML(urlString)?.use { stream ->
                XmlParser().parseGamesList(stream, SearchResult.IDS)
            } ?: emptyList<BoardGamesSearchResult>()
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    suspend fun loadGameDetailsXmlFromNetwork(ids: List<String>): List<*> {

        val gameIds = ids.joinToString(",")
        val urlString = "https://boardgamegeek.com/xmlapi/boardgame/$gameIds"

        return withContext(Dispatchers.IO) {
            downloadXML(urlString)?.use { stream ->
                XmlParser().parseGamesList(stream, SearchResult.DETAILS)
            } ?: emptyList<BoardGame>()
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    suspend fun loadCollectionXmlFromNetwork(userName: String): List<*>? {

        val urlString = "https://boardgamegeek.com/xmlapi/collection/$userName?own=1"

        return withContext(Dispatchers.IO) {
            downloadXML(urlString)?.use { stream ->
                XmlParser().parseUserCollection(stream)
            }
        }
    }

}

@Throws(IOException::class)
private fun downloadXML(urlString: String): InputStream? {
    val url = URL(urlString)
    var connection: HttpURLConnection?

    for (i in 1..10) {
        try {
            connection = (url.openConnection() as? HttpURLConnection)
            connection?.readTimeout = 10000
            connection?.connectTimeout = 15000
            connection?.requestMethod = "GET"
            connection?.doInput = true
            connection?.connect()

            if (connection?.responseCode == 200) {
                return connection.inputStream
            } else {
                println("CODE: " + connection?.responseCode)
                connection?.disconnect()
                Thread.sleep(5000)
            }
        } catch (e: Exception) {
            println("Message: " + e.message)
            return null
        }
    }

    return null
}


