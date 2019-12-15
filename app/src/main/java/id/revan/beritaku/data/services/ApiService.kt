package id.revan.beritaku.data.services

import id.revan.beritaku.data.response.NewsResponse
import id.revan.beritaku.helper.constants.Endpoint
import id.revan.beritaku.helper.constants.Request
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET(Endpoint.SEARCH_ARTICLE)
    suspend fun searchArticle(
        @Query(Request.API_KEY) apiKey: String,
        @Query(Request.QUERY) query: String,
        @Query(Request.PAGE) page: Int,
        @Query(Request.SORT) sort: String
    ): NewsResponse
}