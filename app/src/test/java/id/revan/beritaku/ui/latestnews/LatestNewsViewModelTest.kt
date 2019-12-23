package id.revan.beritaku.ui.latestnews

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import id.revan.beritaku.data.model.News
import id.revan.beritaku.data.model.NewsAuthor
import id.revan.beritaku.data.model.NewsHeadline
import id.revan.beritaku.data.repository.ArticleRepository
import id.revan.beritaku.data.state.ArticleListState
import id.revan.beritaku.domain.Output
import id.revan.beritaku.helper.constants.StatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@RunWith(JUnit4::class)
@ExperimentalCoroutinesApi
class LatestNewsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var SUT: LatestNewsViewModel

    @Mock
    private lateinit var repository: ArticleRepository

    @Mock
    private lateinit var articleListStateObserver: Observer<ArticleListState>

    @Captor
    private lateinit var argumentCaptor: ArgumentCaptor<ArticleListState>

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(testDispatcher)
        SUT = LatestNewsViewModel(repository)
        SUT.articleListState.observeForever(articleListStateObserver)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun getNextArticles_success_listReturned() = runBlockingTest {
        // Arrange
        val list = mutableListOf(
            News(
                uuid = "123",
                snippet = "",
                headline = NewsHeadline(main = ""),
                multimedia = mutableListOf(),
                webUrl = "",
                pubDate = "",
                author = NewsAuthor(""),
                leadParagraph = "",
                source = ""
            )
        )
        `when`(repository.searchArticle("", 0, "newest")).thenReturn(
            Output.Success(
                list
            )
        )

        // Act
        SUT.refreshArticles()

        // Assert
        val expectedLoadingState = ArticleListState(isLoading = true)
        val expectedSuccessState = ArticleListState(articles = list)
        val expectedPageAfterSuccess = 1
        val expectedHasReachedMax = false

        argumentCaptor.run {
            verify(articleListStateObserver, times(2)).onChanged(capture())
            val (loadingState, successState) = allValues
            assertEquals(expectedLoadingState, loadingState)
            assertEquals(expectedSuccessState, successState)
            assertEquals(expectedPageAfterSuccess, SUT.page)
            assertEquals(expectedHasReachedMax, SUT.hasReachedMax)
        }
    }

    @Test
    fun getNextArticles_successEmptyList_listReturned() = runBlockingTest {
        // Arrange
        `when`(repository.searchArticle("", 0, "newest")).thenReturn(
            Output.Success(
                mutableListOf()
            )
        )

        // Act
        SUT.refreshArticles()

        // Assert
        val expectedLoadingState = ArticleListState(isLoading = true)
        val expectedSuccessState = ArticleListState(articles = mutableListOf())
        val expectedPageAfterSuccess = 0
        val expectedHasReachedMax = true

        argumentCaptor.run {
            verify(articleListStateObserver, times(2)).onChanged(capture())
            val (loadingState, successState) = allValues
            assertEquals(expectedLoadingState, loadingState)
            assertEquals(expectedSuccessState, successState)
            assertEquals(expectedPageAfterSuccess, SUT.page)
            assertEquals(expectedHasReachedMax, SUT.hasReachedMax)
        }
    }

    @Test
    fun getNextArticles_networkError_errorReturned() = runBlockingTest {
        // Arrange
        `when`(repository.searchArticle("", 0, "newest")).thenReturn(
            Output.Error(
                StatusCode.NETWORK_ERROR
            )
        )

        // Act
        SUT.refreshArticles()

        // Assert
        val expectedLoadingState = ArticleListState(isLoading = true)
        val expectedErrorState = ArticleListState(errorCode = StatusCode.NETWORK_ERROR)
        val expectedPageAfterError = 0
        val expectedHasReachedMax = false

        argumentCaptor.run {
            verify(articleListStateObserver, times(2)).onChanged(capture())
            val (loadingState, errorState) = allValues
            assertEquals(expectedLoadingState, loadingState)
            assertEquals(expectedErrorState, errorState)
            assertEquals(expectedPageAfterError, SUT.page)
            assertEquals(expectedHasReachedMax, SUT.hasReachedMax)
        }
    }

    @Test
    fun getNextArticles_generalError_errorReturned() = runBlockingTest {
        // Arrange
        `when`(repository.searchArticle("", 0, "newest")).thenReturn(
            Output.Error(
                StatusCode.GENERAL_ERROR
            )
        )

        // Act
        SUT.refreshArticles()

        // Assert
        val expectedLoadingState = ArticleListState(isLoading = true)
        val expectedErrorState = ArticleListState(errorCode = StatusCode.GENERAL_ERROR)
        val expectedPageAfterError = 0
        val expectedHasReachedMax = false

        argumentCaptor.run {
            verify(articleListStateObserver, times(2)).onChanged(capture())
            val (loadingState, errorState) = allValues
            assertEquals(expectedLoadingState, loadingState)
            assertEquals(expectedErrorState, errorState)
            assertEquals(expectedPageAfterError, SUT.page)
            assertEquals(expectedHasReachedMax, SUT.hasReachedMax)
        }
    }

    @Test
    fun isLoading_loading_trueReturned() {
        // Arrange
        SUT.articleListState.value = ArticleListState(isLoading = true)

        // Act
        val result = SUT.isLoading()

        // Assert
        assertEquals(true, result)
    }

    @Test
    fun isLoading_success_falseReturned() {
        // Arrange
        SUT.articleListState.value = ArticleListState()

        // Act
        val result = SUT.isLoading()

        // Assert
        assertEquals(false, result)
    }

    @Test
    fun isLoading_error_falseReturned() {
        // Arrange
        SUT.articleListState.value = ArticleListState(errorCode = StatusCode.GENERAL_ERROR)

        // Act
        val result = SUT.isLoading()

        // Assert
        assertEquals(false, result)
    }

    @Test
    fun isLoading_null_falseReturned() {
        // Arrange
        SUT.articleListState.value = null

        // Act
        val result = SUT.isLoading()

        // Assert
        assertEquals(false, result)
    }
}