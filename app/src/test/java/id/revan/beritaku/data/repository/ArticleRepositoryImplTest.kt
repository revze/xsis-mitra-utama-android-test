package id.revan.beritaku.data.repository

import id.revan.beritaku.BuildConfig
import id.revan.beritaku.data.model.News
import id.revan.beritaku.data.response.NewsResponse
import id.revan.beritaku.data.response.Response
import id.revan.beritaku.data.services.ApiService
import id.revan.beritaku.domain.Output
import id.revan.beritaku.helper.constants.StatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.HttpException
import java.io.IOException

@RunWith(JUnit4::class)
@ExperimentalCoroutinesApi
class ArticleRepositoryImplTest {

    private lateinit var SUT: ArticleRepositoryImpl

    @Mock
    private lateinit var apiService: ApiService

    private val testDispatcher = TestCoroutineDispatcher()

    private val QUERY = "indonesia"
    private val PAGE = 1
    private val SORT = "newest"

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(testDispatcher)
        SUT = ArticleRepositoryImpl(apiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun searchArticle_success_listReturned() = runBlockingTest {
        // Arrange
        `when`(apiService.searchArticle(BuildConfig.API_KEY, QUERY, PAGE, SORT)).thenReturn(
            NewsResponse(
                Response(mutableListOf())
            )
        )

        // Act
        val result = SUT.searchArticle(QUERY, PAGE, SORT)

        // Assert
        val expected = Output.Success(mutableListOf<News>())
        assertEquals(expected, result)
    }

    @Test
    fun searchArticle_networkError_errorReturned() = runBlockingTest {
        // Arrange
        `when`(apiService.searchArticle(BuildConfig.API_KEY, QUERY, PAGE, SORT)).thenAnswer {
            throw IOException()
        }

        // Act
        val result = SUT.searchArticle(QUERY, PAGE, SORT)

        // Assert
        val expected = Output.Error<List<News>>(StatusCode.NETWORK_ERROR)
        assertEquals(expected, result)
    }

    @Test
    fun searchArticle_generalError_errorReturned() = runBlockingTest {
        // Arrange
        `when`(apiService.searchArticle(BuildConfig.API_KEY, QUERY, PAGE, SORT)).thenAnswer {
            throw Exception("Unknown error")
        }

        // Act
        val result = SUT.searchArticle(QUERY, PAGE, SORT)

        // Assert
        val expected = Output.Error<List<News>>(StatusCode.GENERAL_ERROR)
        assertEquals(expected, result)
    }

    @Test
    fun searchArticle_gatewayTimeoutError_errorReturned() = runBlockingTest {
        // Arrange
        `when`(apiService.searchArticle(BuildConfig.API_KEY, QUERY, PAGE, SORT)).thenAnswer {
            throw HttpException(
                retrofit2.Response.error<NewsResponse>(
                    504,
                    ResponseBody.create(null, "")
                )
            )
        }

        // Act
        val result = SUT.searchArticle(QUERY, PAGE, SORT)

        // Assert
        val expected = Output.Error<List<News>>(StatusCode.NETWORK_ERROR)
        assertEquals(expected, result)
    }

    @Test
    fun searchArticle_httpError_errorReturned() = runBlockingTest {
        // Arrange
        `when`(apiService.searchArticle(BuildConfig.API_KEY, QUERY, PAGE, SORT)).thenAnswer {
            throw HttpException(
                retrofit2.Response.error<NewsResponse>(
                    401,
                    ResponseBody.create(null, "")
                )
            )
        }

        // Act
        val result = SUT.searchArticle(QUERY, PAGE, SORT)

        // Assert
        val expected = Output.Error<List<News>>(StatusCode.GENERAL_ERROR)
        assertEquals(expected, result)
    }
}