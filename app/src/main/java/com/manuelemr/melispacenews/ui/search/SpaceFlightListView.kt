package com.manuelemr.melispacenews.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.manuelemr.melispacenews.R
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpaceFlightView(
    modifier: Modifier = Modifier,
    viewModel: SpaceFlightListViewModel = koinViewModel()
) {

    LaunchedEffect(true) {
        viewModel.fetchArticles()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.search_space_flight_news_title)) }
            )
        }
    ) { paddingValues ->
        val state by viewModel.viewState.collectAsState()

        LazyColumn(
            modifier = modifier.padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(state.articles, key = { it }) { article ->
                ArticleRow(
                    article = article,
                )
            }
        }
    }
}

@Preview
@Composable
private fun SpaceFlightViewPreview() {
    SpaceFlightView()
}