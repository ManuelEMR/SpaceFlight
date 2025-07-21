package com.manuelemr.melispacenews.ui.articledetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.manuelemr.melispacenews.R
import com.manuelemr.melispacenews.spacerepository.Article

@Composable
fun ArticleDetail(
    modifier: Modifier = Modifier,
    article: Article
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AsyncImage(
            model = article.imageUrl,
            contentDescription = stringResource(R.string.search_article_image_description),
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth().height(200.dp)
        )

        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = article.title,
            style = MaterialTheme.typography.headlineMedium,
        )

        article.summary?.let {
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = it,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            article.publishedAtFormatted?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                )
            }

            article.newsSite?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ArticleDetailPreview() {
    ArticleDetail(
        article = Article(
            12,
            "Test Article",
            url = "https://api.spaceflightnewsapi.net/v4/docs/#/articles/articles_list",
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSxGnS5AO1k3OpZnSm3tRpSE6QMwI6nD1wZAcbPrTsNa9FZQcBttmU_WNWdTX2IpZu2Dmca9g",
            summary = "Test Summary",
            publishedAt = "2025-07-19T13:28:19.438Z",
            newsSite = "Test news site"
        )
    )
}