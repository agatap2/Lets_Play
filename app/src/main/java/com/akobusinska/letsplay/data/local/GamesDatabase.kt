package com.akobusinska.letsplay.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.akobusinska.letsplay.data.entities.MyGame

@Database(entities = [MyGame::class], version = 1, exportSchema = false)
abstract class GamesDatabase : RoomDatabase() {

    abstract val gameDao: GameDao
}