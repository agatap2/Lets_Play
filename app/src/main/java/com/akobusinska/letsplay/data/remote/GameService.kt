package com.akobusinska.letsplay.data.remote

import com.akobusinska.letsplay.BuildConfig
import com.akobusinska.letsplay.data.json.GamesList
import retrofit2.http.GET
import retrofit2.http.Query

interface GameService {

    @GET("search")
    suspend fun getSearchResult(
        @Query("name") name: String,
        @Query("client_id") clientId: String = BuildConfig.CLIENT_ID): GamesList
}