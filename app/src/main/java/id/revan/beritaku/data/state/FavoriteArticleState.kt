package id.revan.beritaku.data.state

import id.revan.beritaku.data.model.News

data class FavoriteArticleState(
    var isLoading: Boolean = false,
    var articles: List<News> = mutableListOf()
)