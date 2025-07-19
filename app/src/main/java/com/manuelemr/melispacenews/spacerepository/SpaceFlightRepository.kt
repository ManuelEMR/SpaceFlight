package com.manuelemr.melispacenews.spacerepository

class SpaceFlightRepository(private val api: SpaceFlightApi) {
    private val pageSize = 20

    suspend fun searchArticles(search: String? = null, page: Int = 0): List<Article> {
        return api.searchArticles(
            offset = page * pageSize,
            search = search
        ).results
    }

    suspend fun getArticleById(id: Int): Article {
        return api.getArticleById(id)
    }
}
