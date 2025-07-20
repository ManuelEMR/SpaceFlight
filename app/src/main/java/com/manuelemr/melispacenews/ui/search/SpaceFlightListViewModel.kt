package com.manuelemr.melispacenews.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuelemr.melispacenews.spacerepository.Article
import com.manuelemr.melispacenews.spacerepository.SpaceFlightApiStub
import com.manuelemr.melispacenews.spacerepository.SpaceFlightRepository
import com.manuelemr.melispacenews.spacerepository.SpaceFlightRepositoryStub
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.switchMap
import kotlinx.coroutines.launch
import kotlin.time.Duration

data class SpaceFlightViewState(
    val articles: List<Article> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

open class SpaceFlightListViewModel(
    private val spaceFlightRepository: SpaceFlightRepository
): ViewModel() {

    protected val _viewState = MutableStateFlow(SpaceFlightViewState())
    val viewState: StateFlow<SpaceFlightViewState> = _viewState

    init {
        setupBindings()
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun setupBindings() {
        viewModelScope.launch {
            _viewState.map { it.searchQuery }
                .distinctUntilChanged()
                .debounce(500)
                .onEach {
                    _viewState.value = _viewState.value.copy(isLoading = true)
                }
                .flatMapLatest {
                    flow {
                        emit(spaceFlightRepository.searchArticles(page = 0, search = it))
                    }
                }
                .collectLatest { articles ->
                    _viewState.value = _viewState.value.copy(articles = articles, isLoading = false)
                }
        }
    }

    fun fetchArticles() {
        viewModelScope.launch {
            try {
                _viewState.value = _viewState.value.copy(isLoading = true)
                val articles = spaceFlightRepository.searchArticles(page = 0, search = _viewState.value.searchQuery)

                _viewState.value = _viewState.value.copy(articles = articles)
            } catch (e: Exception) {
                Log.e("SpaceFlightListViewModel", "Error fetching articles", e)
            } finally {
                _viewState.value = _viewState.value.copy(isLoading = false)
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _viewState.value = _viewState.value.copy(searchQuery = query)
    }
}



class SpaceFlightListViewModelMock: SpaceFlightListViewModel(SpaceFlightRepositoryStub()) {
    init {
        _viewState.value = SpaceFlightViewState(articles = SpaceFlightApiStub.testArticles)
    }
}