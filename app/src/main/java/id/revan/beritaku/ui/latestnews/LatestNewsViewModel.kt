package id.revan.beritaku.ui.latestnews

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.revan.beritaku.data.db.dao.FavoriteNewsDao
import id.revan.beritaku.data.model.FavoriteNews
import id.revan.beritaku.data.model.News
import id.revan.beritaku.data.repository.ArticleRepository
import id.revan.beritaku.data.state.ArticleListState
import id.revan.beritaku.domain.Output
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LatestNewsViewModel @Inject constructor(
    private val repository: ArticleRepository,
    private val newsDao: FavoriteNewsDao
) :
    ViewModel() {
    val articleListState = MutableLiveData<ArticleListState>()
    var page = 0
        private set
    private var hasReachedMax = false
    private val query = "indonesia"
    private val sort = "newest"

    fun refreshArticles() {
        page = 0
        hasReachedMax = false
        getNextArticles()
    }

    fun getNextArticles() {
        if (!hasReachedMax) {
            articleListState.value = ArticleListState(isLoading = true)
            viewModelScope.launch {
                val result = repository.searchArticle(query, page, sort)

                when (result) {
                    is Output.Success -> {
                        if (result.output.isNotEmpty()) {
                            page++
                        } else {
                            hasReachedMax = true
                        }
                        result.output.map {
                            val favoriteNews = newsDao.getNews(it.uuid)

                            if (favoriteNews != null) {
                                it.isFavorite = true
                            }
                        }
                        articleListState.postValue(ArticleListState(articles = result.output))
                    }
                    is Output.Error -> articleListState.postValue(
                        ArticleListState(
                            errorCode = result.code
                        )
                    )
                }
            }
        }
    }

    fun isLoading(): Boolean {
        val currentState = articleListState.value

        if (currentState != null) {
            return currentState.isLoading
        }
        return false
    }

    fun favArticle(news: News, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            val thumbnail = if (news.multimedia.isNotEmpty()) news.multimedia[0].url else ""
            val favoriteNews = FavoriteNews(
                uuid = news.uuid,
                snippet = news.snippet,
                webUrl = news.webUrl,
                pubDate = news.pubDate,
                thumbnail = thumbnail,
                title = news.headline.main
            )
            insertFavoriteToDb(favoriteNews)
            callback()
        }
    }

    private suspend fun insertFavoriteToDb(news: FavoriteNews) = withContext(Dispatchers.IO) {
        if (newsDao.getNews(news.uuid) != null) {
            newsDao.delete(news.uuid)
        } else {
            newsDao.insert(news)
        }
    }
}