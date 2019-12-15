package id.revan.beritaku.data.repository

import id.revan.beritaku.data.model.News
import id.revan.beritaku.domain.Output

interface ArticleRepository {
    suspend fun searchArticle(query: String, page: Int, sort: String = ""): Output<List<News>>
}