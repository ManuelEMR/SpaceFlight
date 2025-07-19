package com.manuelemr.melispacenews.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuelemr.melispacenews.spacerepository.Article
import com.manuelemr.melispacenews.spacerepository.SpaceFlightApiStub
import com.manuelemr.melispacenews.spacerepository.SpaceFlightRepository
import com.manuelemr.melispacenews.spacerepository.SpaceFlightRepositoryStub
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SpaceFlightViewState(
    val articles: List<Article> = emptyList(),
    val searchQuery: String = ""
)

open class SpaceFlightListViewModel(
    private val spaceFlightRepository: SpaceFlightRepository
): ViewModel() {

    protected val _viewState = MutableStateFlow(SpaceFlightViewState())
    val viewState: StateFlow<SpaceFlightViewState> = _viewState

    fun fetchArticles() {
        viewModelScope.launch {
            try {
                val articles = spaceFlightRepository.searchArticles(page = 0)

                _viewState.value = _viewState.value.copy(articles = articles)
            } catch (e: Exception) {
                Log.e("SpaceFlightListViewModel", "Error fetching articles", e)
            }
        }
    }
}



class SpaceFlightListViewModelMock: SpaceFlightListViewModel(SpaceFlightRepositoryStub()) {
    init {
        _viewState.value = SpaceFlightViewState(articles = SpaceFlightApiStub.testArticles)
    }
}