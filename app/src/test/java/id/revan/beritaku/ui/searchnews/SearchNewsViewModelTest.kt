package id.revan.beritaku.ui.searchnews

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import id.revan.beritaku.data.db.dao.KeywordDao
import id.revan.beritaku.data.model.Keyword
import id.revan.beritaku.data.model.News
import id.revan.beritaku.data.model.NewsAuthor
import id.revan.beritaku.data.model.NewsHeadline
import id.revan.beritaku.data.repository.ArticleRepository
import id.revan.beritaku.data.state.SearchArticleState
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
import org.mockito.*
import org.mockito.Mockito.*

@RunWith(JUnit4::class)
@ExperimentalCoroutinesApi
class SearchNewsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var SUT: SearchNewsViewModel

    @Mock
    private lateinit var repository: ArticleRepository

    @Mock
    private lateinit var keywordDao: KeywordDao

    @Mock
    private lateinit var searchArticleStateObserver: Observer<SearchArticleState>

    @Mock
    private lateinit var keywordsObserver: Observer<List<Keyword>>

    @Captor
    private lateinit var argumentCaptor: ArgumentCaptor<SearchArticleState>

    private val testDispatcher = TestCoroutineDispatcher()

    private val QUERY = "india"

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(testDispatcher)
        SUT = SearchNewsViewModel(repository, keywordDao)
        SUT.searchArticleState.observeForever(searchArticleStateObserver)
        SUT.keywords.observeForever(keywordsObserver)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun searchArticle_success_listReturned() = runBlockingTest {
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
        `when`(repository.searchArticle(QUERY, 0, "newest")).thenReturn(
            Output.Success(
                list
            )
        )

        // Act
        SUT.searchArticle(QUERY)

        // Assert
        val expectedLoadingState = SearchArticleState(isLoading = true)
        val expectedSuccessState = SearchArticleState(articles = list)
        val expectedPageAfterSuccess = 1
        val expectedHasReachedMax = false

        argumentCaptor.run {
            verify(searchArticleStateObserver, times(2)).onChanged(capture())
            val (loadingState, successState) = allValues
            assertEquals(expectedLoadingState, loadingState)
            assertEquals(expectedSuccessState, successState)
            assertEquals(expectedPageAfterSuccess, SUT.page)
            assertEquals(expectedHasReachedMax, SUT.hasReachedMax)
        }
    }

    @Test
    fun searchArticle_successListEmpty_listReturned() = runBlockingTest {
        // Arrange
        val list = mutableListOf<News>()
        `when`(repository.searchArticle(QUERY, 0, "newest")).thenReturn(
            Output.Success(
                list
            )
        )

        // Act
        SUT.searchArticle(QUERY)

        // Assert
        val expectedLoadingState = SearchArticleState(isLoading = true)
        val expectedSuccessState = SearchArticleState(articles = list)
        val expectedPageAfterSuccess = 0
        val expectedHasReachedMax = true

        argumentCaptor.run {
            verify(searchArticleStateObserver, times(2)).onChanged(capture())
            val (loadingState, successState) = allValues
            assertEquals(expectedLoadingState, loadingState)
            assertEquals(expectedSuccessState, successState)
            assertEquals(expectedPageAfterSuccess, SUT.page)
            assertEquals(expectedHasReachedMax, SUT.hasReachedMax)
        }
    }

    @Test
    fun searchArticle_networkError_errorReturned() = runBlockingTest {
        // Arrange
        `when`(repository.searchArticle(QUERY, 0, "newest")).thenReturn(
            Output.Error(
                StatusCode.NETWORK_ERROR
            )
        )

        // Act
        SUT.searchArticle(QUERY)

        // Assert
        val expectedLoadingState = SearchArticleState(isLoading = true)
        val expectedErrorState = SearchArticleState(errorCode = StatusCode.NETWORK_ERROR)
        val expectedPageAfterSuccess = 0
        val expectedHasReachedMax = false

        argumentCaptor.run {
            verify(searchArticleStateObserver, times(2)).onChanged(capture())
            val (loadingState, successState) = allValues
            assertEquals(expectedLoadingState, loadingState)
            assertEquals(expectedErrorState, successState)
            assertEquals(expectedPageAfterSuccess, SUT.page)
            assertEquals(expectedHasReachedMax, SUT.hasReachedMax)
        }
    }

    @Test
    fun searchArticle_generalError_errorReturned() = runBlockingTest {
        // Arrange
        `when`(repository.searchArticle(QUERY, 0, "newest")).thenReturn(
            Output.Error(
                StatusCode.GENERAL_ERROR
            )
        )

        // Act
        SUT.searchArticle(QUERY)

        // Assert
        val expectedLoadingState = SearchArticleState(isLoading = true)
        val expectedErrorState = SearchArticleState(errorCode = StatusCode.GENERAL_ERROR)
        val expectedPageAfterSuccess = 0
        val expectedHasReachedMax = false

        argumentCaptor.run {
            verify(searchArticleStateObserver, times(2)).onChanged(capture())
            val (loadingState, successState) = allValues
            assertEquals(expectedLoadingState, loadingState)
            assertEquals(expectedErrorState, successState)
            assertEquals(expectedPageAfterSuccess, SUT.page)
            assertEquals(expectedHasReachedMax, SUT.hasReachedMax)
        }
    }

    @Test
    fun isLoading_loading_trueReturned() {
        // Arrange
        SUT.searchArticleState.value = SearchArticleState(isLoading = true)

        // Act
        val result = SUT.isLoading()

        // Assert
        assertEquals(true, result)
    }

    @Test
    fun isLoading_success_falseReturned() {
        // Arrange
        SUT.searchArticleState.value = SearchArticleState()

        // Act
        val result = SUT.isLoading()

        // Assert
        assertEquals(false, result)
    }

    @Test
    fun isLoading_error_falseReturned() {
        // Arrange
        SUT.searchArticleState.value = SearchArticleState(errorCode = StatusCode.GENERAL_ERROR)

        // Act
        val result = SUT.isLoading()

        // Assert
        assertEquals(false, result)
    }

    @Test
    fun isLoading_null_falseReturned() {
        // Arrange
        SUT.searchArticleState.value = null

        // Act
        val result = SUT.isLoading()

        // Assert
        assertEquals(false, result)
    }

    @Test
    fun searchArticle_keywordNotNull_deleteKeywordObserved() = runBlockingTest {
        // Arrange
        val keyword = Keyword(name = QUERY)
        `when`(keywordDao.getKeyword(QUERY)).thenReturn(keyword)

        // Act
        SUT.searchArticle(QUERY)

        // Assert
        verify(keywordDao).delete(keyword)
    }
}