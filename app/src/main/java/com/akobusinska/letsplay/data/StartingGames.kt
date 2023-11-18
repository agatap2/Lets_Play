package com.akobusinska.letsplay.data

import android.content.Context
import android.util.Log
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.akobusinska.letsplay.R
import com.akobusinska.letsplay.data.entities.MyGame
import com.akobusinska.letsplay.data.local.GamesDatabase
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader

open class StartingGames(private val context: Context) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        CoroutineScope(Dispatchers.IO).launch {
            fillWithStartingGames(context)
        }
    }

    private suspend fun fillWithStartingGames(context: Context) {
        val dao = GamesDatabase.getInstance(context)?.gameDao
        val gson = Gson()
        var jsonObject: JSONObject
        var myGame: MyGame

        try {
            val games = loadJSONArray(context)
            for (game in 0 until games.length()) {
                jsonObject = games.getJSONObject(game)
                myGame = gson.fromJson(jsonObject.toString(), MyGame::class.java)
                dao?.insertGame(myGame)
            }
        } catch (e: JSONException) {
            Log.d("fillWithStartingGames:", e.toString())
        }
    }

    private fun loadJSONArray(context: Context): JSONArray {
        val inputStream = context.resources.openRawResource(R.raw.games)
        BufferedReader(inputStream.reader()).use {
            return JSONArray(it.readText())
        }
    }
}