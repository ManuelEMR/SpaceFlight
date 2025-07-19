package com.manuelemr.melispacenews.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.manuelemr.melispacenews.R
import com.manuelemr.melispacenews.spacerepository.Article

@Composable
fun ArticleRow(
    modifier: Modifier = Modifier,
    article: Article
) {
    Box(
        modifier = modifier.heightIn(60.dp)
            .clip(RoundedCornerShape(15.dp))
    ) {
        
        var maxHeight by remember {
            mutableIntStateOf(60)
        }

        val density = LocalDensity.current
        val maxHeightDp by remember {
            derivedStateOf {
                with(density) {
                    maxHeight.toDp()
                }
            }
        }

        AsyncImage(
            model = article.imageUrl,
            contentDescription = stringResource(R.string.search_article_image_description),
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth().height(maxHeightDp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { proxy ->
                    maxHeight = proxy.size.height
                }
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.5f),
                            Color.Black.copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(8.dp)
        ) {
            Text(
                text = article.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )

            article.summary?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                article.publishedAtFormatted?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                }

                article.newsSite?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SpaceFlightRowPreview() {
    ArticleRow(
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