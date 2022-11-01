package com.akobusinska.letsplay.data.local

import androidx.room.Database
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
}