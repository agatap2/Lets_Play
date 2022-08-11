package com.akobusinska.letsplay.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.akobusinska.letsplay.data.entities.MyGame

@TypeConverters(Converters::class)
@Database(entities = [MyGame::class], version = 3, exportSchema = false)
abstract class GamesDatabase : RoomDatabase() {

    abstract val gameDao: GameDao
}