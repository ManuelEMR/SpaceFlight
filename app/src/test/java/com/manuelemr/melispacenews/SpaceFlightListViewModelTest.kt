package com.manuelemr.melispacenews

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.manuelemr.melispacenews.R
import com.manuelemr.melispacenews.spacerepository.Article
import com.manuelemr.melispacenews.spacerepository.SpaceFlightRepository
import com.manuelemr.melispacenews.ui.search.ErrorViewState
import com.manuelemr.melispacenews.ui.search.SpaceFlightListViewModel
import com.manuelemr.melispacenews.ui.search.SpaceFlightViewState
import com.manuelemr.melispacenews.ui.utils.ResourceProvider
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class SpaceFlightListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private val mockRepository = mockk<SpaceFlightRepository>()
    private val mockResourceProvider = mockk<ResourceProvider>()
    private lateinit var viewModel: SpaceFlightListViewModel

    private val testArticles = listOf(
        Article(id = 1, title = "Test Article 1", url = "", summary = "Summary 1"),
        Article(id = 2, title = "Test Article 2", url = "", summary = "Summary 2"),
        Article(id = 3, title = "Test Article 3", url = "", summary = "Summary 3")
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Default mock behaviors
        every { mockResourceProvider.getString(R.string.failed_to_fetch_articles) } returns "Failed to fetch articles"
        every { mockResourceProvider.getString(R.string.failed_to_fetch_more_articles) } returns "Failed to fetch more articles"

        coEvery { mockRepository.searchArticles(any(), any()) } returns testArticles

        viewModel = SpaceFlightListViewModel(mockRepository, mockResourceProvider)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be correct`() = runTest {
        // Given & When
        val initialState = viewModel.viewState.first()
        val initialErrorState = viewModel.errorState.first()

        // Then
        assertEquals(SpaceFlightViewState(), initialState)
        assertNull(initialErrorState)
    }

    @Test
    fun `fetchArticles should clear error state and trigger paging invalidation`() = runTest {
        // Given
        viewModel.setErrorState(ErrorViewState("Test error", false, true))

        // When
        viewModel.fetchArticles()
        advanceUntilIdle()

        // Then
        assertNull(viewModel.errorState.first())
        coVerify { mockRepository.searchArticles("") }
    }

    @Test
    fun `onSearchQueryChanged should update search query in state`() = runTest {
        // Given
        val searchQuery = "space exploration"

        // When
        viewModel.onSearchQueryChanged(searchQuery)

        // Then
        assertEquals(searchQuery, viewModel.viewState.first().searchQuery)
    }

    @Test
    fun `search query debounce should trigger repository call after delay`() = runTest {
        // Given
        val searchQuery = "mars"

        // When
        // do this to skip initial call to searchArticles
        advanceTimeBy(700)
        coVerify(exactly = 1) { mockRepository.searchArticles("", 0) }

        viewModel.onSearchQueryChanged(searchQuery)
        advanceTimeBy(300) // Less than debounce time

        // Then - should not have called repository yet
        coVerify(exactly = 0) { mockRepository.searchArticles(searchQuery, 0) } // Only initial call

        // When
        advanceTimeBy(300) // Total 600ms, more than debounce time

        // Then - should have called repository with new query
        coVerify { mockRepository.searchArticles(searchQuery, page = 0) }
    }

    @Test
    fun `multiple rapid search queries should debounce correctly`() = runTest {
        // When
        viewModel.onSearchQueryChanged("a")
        advanceTimeBy(100)
        viewModel.onSearchQueryChanged("ab")
        advanceTimeBy(100)
        viewModel.onSearchQueryChanged("abc")
        advanceTimeBy(600) // Only the last query should trigger after debounce

        // Then
        coVerify { mockRepository.searchArticles("abc") }
        coVerify(exactly = 0) { mockRepository.searchArticles("a") }
        coVerify(exactly = 0) { mockRepository.searchArticles("ab") }
    }

    @Test
    fun `loadMoreIfNeeded should trigger next page when at last item`() = runTest {
        // Given
        val articles = testArticles
        viewModel.setViewState(viewModel.viewState.first().copy(articles = articles))
        val lastArticle = articles.last()

        // When
        // skip initial call
        advanceTimeBy(700)
        coVerify(exactly = 1) { mockRepository.searchArticles("", 0) }

        viewModel.loadMoreIfNeeded(lastArticle)
        advanceUntilIdle()

        // Then
        coVerify { mockRepository.searchArticles("") } // Next page
    }

    @Test
    fun `loadMoreIfNeeded should not trigger when not at last item`() = runTest {
        // Given
        val articles = testArticles
        viewModel.setViewState(viewModel.viewState.first().copy(articles = articles))
        val firstArticle = articles.first()

        // When
        advanceTimeBy(700)
        coVerify(exactly = 1) { mockRepository.searchArticles("", 0) }

        viewModel.loadMoreIfNeeded(firstArticle)
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { mockRepository.searchArticles(any(), any()) } // Only initial call
    }

    @Test
    fun `requestNextPage should handle network error for first page`() = runTest {
        // Given
        val exception = IOException("Network error")
        clearMocks(mockRepository)
        coEvery { mockRepository.searchArticles("", 0) } throws exception
        val onResult = mockk<(List<Article>) -> Unit>(relaxed = true)

        // When
        // skip first call
        advanceTimeBy(700)
        coVerify(exactly = 1) { mockRepository.searchArticles("", 0) }

        viewModel.requestNextPage(0, onResult)
        advanceUntilIdle()

        // Debug: Verify the exception was actually thrown
        coVerify { mockRepository.searchArticles( "", 0) }

        // Then
        val errorState = viewModel.errorState.first()
        assertNotNull(errorState)
        assertEquals("Failed to fetch articles", errorState?.errorMessage)
        assertFalse(errorState?.showAsSnackbar ?: true)
        assertFalse(viewModel.viewState.first().isLoading)
    }

    @Test
    fun `requestNextPage should handle network error for subsequent pages`() = runTest {
        // Given
        val exception = IOException("Network error")
        clearMocks(mockRepository)
        coEvery { mockRepository.searchArticles("", 1) } throws exception
        val onResult = mockk<(List<Article>) -> Unit>(relaxed = true)

        // When
        // skip first call
        advanceTimeBy(700)
        coVerify(exactly = 1) { mockRepository.searchArticles("", 0) }

        viewModel.requestNextPage(1, onResult)
        advanceUntilIdle()

        // Then
        val errorState = viewModel.errorState.first()
        assertNotNull(errorState)
        assertEquals("Failed to fetch more articles", errorState?.errorMessage)
        assertTrue(errorState?.showAsSnackbar ?: false)
        assertTrue(errorState?.isIndefinite ?: false)
        assertFalse(viewModel.viewState.first().isLoading)
    }

    @Test
    fun `requestNextPage should clear previous error state`() = runTest {
        // Given
        viewModel.setErrorState(ErrorViewState("Previous error", false, true))
        val onResult = mockk<(List<Article>) -> Unit>(relaxed = true)

        // When
        viewModel.requestNextPage(0, onResult)

        // Then
        assertNull(viewModel.errorState.first())
    }

    @Test
    fun `onItemsUpdated should update articles in state`() = runTest {
        // Given
        val newArticles = testArticles

        // When
        viewModel.onItemsUpdated(newArticles)

        // Then
        assertEquals(newArticles, viewModel.viewState.first().articles)
    }

    @Test
    fun `onNoMoreItems should update hasMoreItems flag`() = runTest {
        // Given & When
        viewModel.onNoMoreItems(false)

        // Then - should not trigger more page requests
        val lastArticle = testArticles.last()
        viewModel.setViewState(viewModel.viewState.first().copy(articles = testArticles))
        viewModel.loadMoreIfNeeded(lastArticle)
        advanceUntilIdle()

        // Should only have the initial call, no additional pagination
        coVerify(exactly = 1) { mockRepository.searchArticles(any(), any()) }
    }

    @Test
    fun `onInvalidate should reset state correctly`() = runTest {
        // Given
        viewModel.setViewState(viewModel.viewState.first().copy(
            articles = testArticles,
            searchQuery = "test"
        ))

        // When
        viewModel.onInvalidate()

        // Then
        val state = viewModel.viewState.first()
        assertTrue(state.articles.isEmpty())
        assertEquals("test", state.searchQuery) // Should keep search query
    }

    @Test
    fun `cancellation of previous request when new search is triggered`() = runTest {
        // Given
        var firstCallCompleted = false
        var secondCallCompleted = false

        coEvery { mockRepository.searchArticles("first") } coAnswers {
            delay(1000)
            firstCallCompleted = true
            testArticles
        }

        coEvery { mockRepository.searchArticles( "second") } coAnswers {
            delay(100)
            secondCallCompleted = true
            testArticles
        }

        // When
        viewModel.onSearchQueryChanged("first")
        advanceTimeBy(600) // Trigger first search

        viewModel.onSearchQueryChanged("second")
        advanceTimeBy(600) // Trigger second search

        advanceUntilIdle()

        // Then
        assertFalse("First call should be cancelled", firstCallCompleted)
        assertTrue("Second call should complete", secondCallCompleted)
    }
}