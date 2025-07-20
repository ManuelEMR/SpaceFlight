package com.manuelemr.melispacenews.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuelemr.melispacenews.spacerepository.Article
import com.manuelemr.melispacenews.spacerepository.SpaceFlightApiStub
import com.manuelemr.melispacenews.spacerepository.SpaceFlightRepository
import com.manuelemr.melispacenews.spacerepository.SpaceFlightRepositoryStub
import com.manuelemr.melispacenews.ui.utils.PagingHandler
import com.manuelemr.melispacenews.ui.utils.PagingHandlerDelegate
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class SpaceFlightViewState(
    val articles: List<Article> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

open class SpaceFlightListViewModel(
    private val spaceFlightRepository: SpaceFlightRepository
): ViewModel(), PagingHandlerDelegate<Article> {

    protected val _viewState = MutableStateFlow(SpaceFlightViewState())
    val viewState: StateFlow<SpaceFlightViewState> = _viewState

    private var hasMoreItems = true
    private val pager = PagingHandler(this)
    private var pagingJob: Job? = null

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
                .collectLatest { articles ->
                    pager.invalidate()
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

    fun loadMoreIfNeeded(row: Article) {
        val index = viewState.value.articles.indexOf(row)
        if (viewState.value.articles.isNotEmpty() && index >= _viewState.value.articles.size - 1) {
            getNextPage()
        }
    }

    private fun getNextPage() {
        if (hasMoreItems) {
            pager.fetchNextPage()
        }
    }

    // region PagingHandlerDelegate
    override fun requestNextPage(
        page: Int,
        onResult: (List<Article>) -> Unit
    ) {
        pagingJob?.cancel()
        pagingJob = viewModelScope.launch {
            try {
                _viewState.value = _viewState.value.copy(isLoading = true)
                onResult(spaceFlightRepository.searchArticles(page = page, search = _viewState.value.searchQuery))
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    Log.e("SpaceFlightListViewModel", "Error fetching articles", e)
                }
            } finally {
                _viewState.value = _viewState.value.copy(isLoading = false)
            }
        }
    }

    override fun onItemsUpdated(items: List<Article>) {
        _viewState.value = _viewState.value.copy(articles = items)
    }

    override fun onNoMoreItems(hasItems: Boolean) {
        hasMoreItems = hasItems
    }

    override fun onInvalidate() {
        _viewState.value = _viewState.value.copy(articles = emptyList())
        hasMoreItems = true
    }
    // endregion
}



class SpaceFlightListViewModelMock: SpaceFlightListViewModel(SpaceFlightRepositoryStub()) {
    init {
        _viewState.value = SpaceFlightViewState(articles = SpaceFlightApiStub.testArticles)
    }
}