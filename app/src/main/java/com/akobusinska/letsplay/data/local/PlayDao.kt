package com.akobusinska.letsplay.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.akobusinska.letsplay.data.entities.PlayWithPlayers

@Dao
interface PlayDao {

    @Transaction
    @Query("SELECT * FROM plays_table")
    fun getPlaysWithPlayers(): List<PlayWithPlayers>
}