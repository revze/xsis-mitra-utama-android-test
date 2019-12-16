package id.revan.beritaku.data.state

import id.revan.beritaku.data.model.News

data class NewsPagerState(
    var isLoading: Boolean = false,
    var currentIndex: Int = 0,
    var news: List<News> = mutableListOf()
)