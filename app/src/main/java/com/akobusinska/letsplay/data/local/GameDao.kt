package com.akobusinska.letsplay.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.akobusinska.letsplay.data.entities.GameType
import com.akobusinska.letsplay.data.entities.MyGame

@Dao
interface GameDao {

    @Query("SELECT * FROM my_game_table ORDER BY name ASC")
    fun getCollection(): LiveData<List<MyGame>>

    @Query("SELECT * FROM my_game_table WHERE gameType = :gameType ORDER BY name ASC")
    fun getFilteredCollection(gameType: GameType): LiveData<List<MyGame>>

    @Query("SELECT * FROM my_game_table WHERE " +
            "maxPlayers >= :numberOfPlayers AND " +
            "minPlayers <= :numberOfPlayers AND " +
            "minAge <= :age AND " +
            "(recommendedForMorePlayers != :excludeRecommendedForMore OR :excludeRecommendedForMore == 0) AND" +
            "((:playtime BETWEEN minPlaytime AND maxPlaytime) OR (maxPlaytime <= :playtime))" +
            "ORDER BY name ASC")
    fun getFilteredGamesCollection(
        numberOfPlayers: Int,
        playtime: Int,
        age: Int,
        excludeRecommendedForMore: Boolean
    ): LiveData<List<MyGame>>

    @Query("SELECT * FROM my_game_table WHERE id = :id")
    fun getGame(id: Int): LiveData<MyGame>

    @Query("SELECT * FROM my_game_table WHERE parentGame = :id")
    fun getExpansions(id: Int): LiveData<List<MyGame>>

    @Insert(onConflict = REPLACE)
    suspend fun insertGame(game: MyGame)

    @Update
    suspend fun updateGame(game: MyGame)

    @Delete
    suspend fun deleteGame(game: MyGame)
}