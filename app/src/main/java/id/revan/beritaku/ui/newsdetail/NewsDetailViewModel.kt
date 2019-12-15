package id.revan.beritaku.ui.newsdetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.revan.beritaku.data.db.dao.FavoriteNewsDao
import id.revan.beritaku.data.model.FavoriteNews
import id.revan.beritaku.data.model.News
import id.revan.beritaku.data.model.NewsMultimedia
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NewsDetailViewModel @Inject constructor(private val newsDao: FavoriteNewsDao) : ViewModel() {
    val favoriteNews = MutableLiveData<FavoriteNews>()

    fun getNews(uuid: String) {
        viewModelScope.launch {
            favoriteNews.postValue(newsDao.getNews(uuid))
        }
    }

    fun updateToDb(
        news: News,
        images: List<NewsMultimedia>,
        callback: (isFavorite: Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val result = updateToDb(news, images)

            callback(result)
        }
    }

    private suspend fun updateToDb(news: News, images: List<NewsMultimedia>): Boolean {
        var isFavorite = false
        withContext(Dispatchers.IO) {
            if (newsDao.getNews(news.uuid) != null) {
                newsDao.delete(news.uuid)
            } else {
                val multimedia = if (images.isNotEmpty()) images[0].url else ""
                newsDao.insert(
                    FavoriteNews(
                        webUrl = news.webUrl,
                        snippet = news.snippet,
                        pubDate = news.pubDate,
                        uuid = news.uuid,
                        title = news.headline.main,
                        thumbnail = multimedia
                    )
                )

                isFavorite = true
            }
        }

        return isFavorite
    }
}