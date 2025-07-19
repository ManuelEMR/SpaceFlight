package com.manuelemr.melispacenews.spacerepository

open class SpaceFlightRepository(protected val api: SpaceFlightApi) {
    private val pageSize = 20

    open suspend fun searchArticles(search: String? = null, page: Int = 0): List<Article> {
        return api.searchArticles(
            offset = page * pageSize,
            search = search
        ).results
    }

    open suspend fun getArticleById(id: Int): Article {
        return api.getArticleById(id)
    }
}

class SpaceFlightRepositoryStub: SpaceFlightRepository(SpaceFlightApiStub()) {
    override suspend fun searchArticles(search: String?, page: Int): List<Article> {
        return api.searchArticles(offset = 0, search = search).results
    }
}