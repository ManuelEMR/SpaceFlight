package com.manuelemr.melispacenews.spacerepository

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

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

class SpaceFlightApiStub: SpaceFlightApi {
    override suspend fun searchArticles(offset: Int, limit: Int, search: String?): ArticleResponse {
        return ArticleResponse(
            count = 10,
            results = testArticles
        )
    }

    override suspend fun getArticleById(id: Int): Article {
        return Article(
            id,
            "Test Article $id",
            url = "https://api.spaceflightnewsapi.net/v4/docs/#/articles/articles_list",
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSxGnS5AO1k3OpZnSm3tRpSE6QMwI6nD1wZAcbPrTsNa9FZQcBttmU_WNWdTX2IpZu2Dmca9g",
            summary = "Test Summary $id",
            publishedAt = ZonedDateTime.now().plusDays(id.toLong()).toString(),
            newsSite = "Test news site"
        )
    }

    companion object {
        val testArticles = List(10) { index ->
            Article(
                index,
                "Test Article $index",
                url = "https://api.spaceflightnewsapi.net/v4/docs/#/articles/articles_list",
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSxGnS5AO1k3OpZnSm3tRpSE6QMwI6nD1wZAcbPrTsNa9FZQcBttmU_WNWdTX2IpZu2Dmca9g",
                summary = "Test Summary $index",
                publishedAt = ZonedDateTime.now().plusDays(index.toLong()).format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")),
                newsSite = "Test news site"
            )
        }
    }
}