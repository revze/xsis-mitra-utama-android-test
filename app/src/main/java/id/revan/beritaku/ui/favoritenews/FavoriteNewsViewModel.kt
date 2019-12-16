package id.revan.beritaku.ui.favoritenews

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.revan.beritaku.data.db.dao.FavoriteNewsDao
import id.revan.beritaku.data.model.News
import id.revan.beritaku.data.model.NewsAuthor
import id.revan.beritaku.data.model.NewsHeadline
import id.revan.beritaku.data.model.NewsMultimedia
import id.revan.beritaku.data.state.FavoriteArticleState
import kotlinx.coroutines.launch
import javax.inject.Inject

class FavoriteNewsViewModel @Inject constructor(private val newsDao: FavoriteNewsDao) :
    ViewModel() {

    val newsState = MutableLiveData<FavoriteArticleState>()

    fun getNews() {
        newsState.value = FavoriteArticleState(isLoading = true)

        viewModelScope.launch {
            val newsDb = newsDao.getAll()
            val favoriteNews = mutableListOf<News>()

            newsDb.map {
                val multimedia = if (it.thumbnail.isEmpty()) mutableListOf() else mutableListOf(
                    NewsMultimedia(url = it.thumbnail)
                )
                favoriteNews.add(
                    News(
                        uuid = it.uuid,
                        pubDate = it.pubDate,
                        snippet = it.snippet,
                        webUrl = it.webUrl,
                        headline = NewsHeadline(main = it.title),
                        multimedia = multimedia,
                        leadParagraph = it.leadParagraph,
                        author = NewsAuthor(it.author),
                        source = it.source
                    )
                )
            }

            newsState.postValue(FavoriteArticleState(articles = favoriteNews))
        }
    }

    fun removeFromFavorite(uuid: String) {
        viewModelScope.launch {
            newsDao.delete(uuid)
            getNews()
        }
    }
}