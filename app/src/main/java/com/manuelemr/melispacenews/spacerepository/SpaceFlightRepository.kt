package com.manuelemr.melispacenews.spacerepository

class SpaceFlightRepository(private val api: SpaceFlightApi) {

    suspend fun searchArticles(search: String): List<Article> {
        return api.searchArticles(search).results
    }

    suspend fun getArticleById(id: Int): Article {
        return api.getArticleById(id)
    }
}
