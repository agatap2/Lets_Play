package com.akobusinska.letsplay.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.akobusinska.letsplay.R
import com.akobusinska.letsplay.data.entities.CollectionOwner
import com.akobusinska.letsplay.data.entities.CollectionOwnerGameCrossRef
import com.akobusinska.letsplay.data.entities.MyGame
import com.akobusinska.letsplay.data.entities.Play
import com.akobusinska.letsplay.data.entities.PlayWithPlaysCrossRef
import com.akobusinska.letsplay.data.entities.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@TypeConverters(Converters::class)
@Database(
    entities = [MyGame::class, Play::class, Player::class, PlayWithPlaysCrossRef::class, CollectionOwner::class, CollectionOwnerGameCrossRef::class],
    version = 20,
    exportSchema = false
)
abstract class GamesDatabase : RoomDatabase() {

    abstract val gameDao: GameDao
    abstract val collectionOwnerDao: CollectionOwnerDao
    abstract val collectionOwnerWithGamesDao: CollectionOwnerWithGamesDao

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
                        .addCallback(object : Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                val collectionOwnerDao = instance?.collectionOwnerDao
                                CoroutineScope(Dispatchers.IO).launch {
                                    collectionOwnerDao?.insertCollectionOwner(
                                        CollectionOwner(
                                            0,
                                            "Default",
                                            context.resources.getString(R.string.default_collection)
                                        )
                                    )
                                }
                            }
                        })
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return instance
        }
    }
}