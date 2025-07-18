package com.manuelemr.melispacenews.spacerepository

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Data Classes **/
@Serializable
data class Article(
    val id: Int,
    val title: String,
    val url: String,
    @SerialName("image_url")val imageUrl: String? = null,
    val summary: String? = null,
    @SerialName("published_at") val publishedAt: String? = null
)

@Serializable
data class ArticleResponse(
    val count: Int,
    val next: String? = null,
    val previous: String? = null,
    val results: List<Article>
)