package com.manuelemr.melispacenews.spacerepository

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SpaceFlightApi {
    @GET("articles/")
    suspend fun searchArticles(
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 20,
        @Query("search") search: String? = null
    ): ArticleResponse

    @GET("articles/{id}/")
    suspend fun getArticleById(
        @Path("id") id: Int
    ): Article
}