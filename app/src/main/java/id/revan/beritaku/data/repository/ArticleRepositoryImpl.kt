package id.revan.beritaku.data.repository

import id.revan.beritaku.BuildConfig
import id.revan.beritaku.data.model.News
import id.revan.beritaku.data.services.ApiService
import id.revan.beritaku.domain.Output
import id.revan.beritaku.helper.constants.StatusCode
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ArticleRepositoryImpl @Inject constructor(private val apiServices: ApiService) :
    ArticleRepository {
    override suspend fun searchArticle(query: String, page: Int, sort: String): Output<List<News>> {
        return try {
            val result = apiServices.searchArticle(BuildConfig.API_KEY, query, page, sort)
            Output.Success(result.response.docs)
        } catch (e: IOException) {
            Output.Error(StatusCode.NETWORK_ERROR)
        } catch (e: HttpException) {
            if (e.code() == 504) {
                Output.Error(StatusCode.NETWORK_ERROR)
            } else {
                Output.Error(StatusCode.GENERAL_ERROR)
            }
        } catch (e: Exception) {
            Output.Error(StatusCode.GENERAL_ERROR)
        }
    }
}