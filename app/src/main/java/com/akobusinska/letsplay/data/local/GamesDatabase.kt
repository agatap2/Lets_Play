package com.akobusinska.letsplay.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.akobusinska.letsplay.data.entities.MyGame
import com.akobusinska.letsplay.data.entities.Play
import com.akobusinska.letsplay.data.entities.PlayWithPlaysCrossRef
import com.akobusinska.letsplay.data.entities.Player

@TypeConverters(Converters::class)
@Database(
    entities = [MyGame::class, Play::class, Player::class, PlayWithPlaysCrossRef::class],
    version = 6,
    exportSchema = false
)
abstract class GamesDatabase : RoomDatabase() {

    abstract val gameDao: GameDao

    companion object {
        @Volatile
        private var instance: GamesDatabase? = null

        fun getInstance(context: Context): GamesDatabase? {
            if (instance == null) {
                synchronized(GamesDatabase::class.java) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        GamesDatabase::class.java,
                        "games_database"
                    )
                        //.addCallback(StartingGames(context))
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return instance
        }
    }
}