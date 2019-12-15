package id.revan.beritaku.data.state

import id.revan.beritaku.data.model.News
import id.revan.beritaku.helper.constants.StatusCode

data class ArticleListState (
    var isLoading: Boolean = false,
    var errorCode: Int = StatusCode.NO_ERROR,
    var articles: List<News> = mutableListOf()
)