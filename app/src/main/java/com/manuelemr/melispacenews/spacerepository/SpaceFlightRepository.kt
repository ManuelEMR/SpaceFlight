package com.manuelemr.melispacenews.spacerepository

class SpaceFlightRepository(private val api: SpaceFlightApi) {

//    init {
//        val contentType = "application/json".toMediaType()
//        val json = Json { ignoreUnknownKeys = true }
//        val retrofit = Retrofit.Builder()
//            .baseUrl("https://api.spaceflightnewsapi.net/v4/")
//            .addConverterFactory(json.asConverterFactory(contentType))
//            .client(OkHttpClient.Builder().build())
//            .build()
//    }

    suspend fun searchArticles(search: String): List<Article> {
        return api.searchArticles(search).results
    }

    suspend fun getArticleById(id: Int): Article {
        return api.getArticleById(id)
    }
}
