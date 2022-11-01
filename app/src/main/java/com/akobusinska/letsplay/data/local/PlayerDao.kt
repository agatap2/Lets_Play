package com.akobusinska.letsplay.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.akobusinska.letsplay.data.entities.PlayerWithPlays

@Dao
interface PlayerDao {

    @Transaction
    @Query("SELECT * FROM player_table")
    fun getPlayersWithPlays(): List<PlayerWithPlays>
}