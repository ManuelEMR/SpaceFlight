package com.manuelemr.melispacenews.spacerepository

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Data Classes **/
@Parcelize
@Serializable
data class Article(
    val id: Int,
    val title: String,
    val url: String,
    @SerialName("image_url")val imageUrl: String? = null,
    val summary: String? = null,
    @SerialName("published_at") val publishedAt: String? = null,
    @SerialName("news_site") val newsSite: String? = null,
): Parcelable {
    val publishedAtFormatted: String?
        get() = publishedAt?.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss", Locale.getDefault()))
}

@Serializable
data class ArticleResponse(
    val count: Int,
    val next: String? = null,
    val previous: String? = null,
    val results: List<Article>
)