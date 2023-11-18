package com.akobusinska.letsplay.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface GameService {

    @GET("search")
    suspend fun getSearchResult(
        @Query("search") name: String
    )
}