package com.manuelemr.melispacenews.ui.search

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarDefaults.InputField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
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

    var isSearchBarActive by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    AnimatedContent(isSearchBarActive) { value ->
                        if (value) {
                            TextField(
                                modifier = Modifier.fillMaxWidth()
                                    .border(1.dp, Color.Gray, RoundedCornerShape(50.dp)),
                                value = "",
                                onValueChange = {
//                                viewModel.onSearchQueryChanged(it)
                                },
                                placeholder = {
                                    Text(
                                        text = stringResource(R.string.search_bar_hint),
                                        color = Color.Gray
                                    )
                                },
                                shape = RoundedCornerShape(50.dp),
                                colors = TextFieldDefaults.colors().copy(
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                )
                            )
                        } else {
                            Text(stringResource(R.string.search_space_flight_news_title))
                        }
                    }

                },
                actions = {
                    AnimatedContent(isSearchBarActive) { value ->
                        if (value) {
                            TextButton(onClick = {
                                isSearchBarActive = isSearchBarActive.not()
                            }) {
                                Text(
                                    text = stringResource(R.string.search_bar_cancel),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        } else {
                            IconButton(onClick = {
                                isSearchBarActive = isSearchBarActive.not()
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_search),
                                    contentDescription = stringResource(R.string.search_icon_description),
                                )
                            }
                        }
                    }
                }
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

@SuppressLint("ViewModelConstructorInComposable")
@Preview
@Composable
private fun SpaceFlightViewPreview() {
    SpaceFlightView(
        viewModel = SpaceFlightListViewModelMock()
    )
}