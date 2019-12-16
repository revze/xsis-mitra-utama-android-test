package id.revan.beritaku.ui.newsdetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.revan.beritaku.data.db.dao.FavoriteNewsDao
import id.revan.beritaku.data.model.FavoriteNews
import id.revan.beritaku.data.model.News
import id.revan.beritaku.data.state.NewsPagerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NewsDetailViewModel @Inject constructor(private val newsDao: FavoriteNewsDao) : ViewModel() {
    val favoriteNews = MutableLiveData<FavoriteNews>()
    val newsPagerState = MutableLiveData<NewsPagerState>()

    fun getNews(uuid: String) {
        viewModelScope.launch {
            favoriteNews.postValue(newsDao.getNews(uuid))
        }
    }

    fun updateToDb(
        news: News,
        callback: (isFavorite: Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val result = updateToDb(news)

            callback(result)
        }
    }

    private suspend fun updateToDb(news: News): Boolean {
        var isFavorite = false
        withContext(Dispatchers.IO) {
            if (newsDao.getNews(news.uuid) != null) {
                newsDao.delete(news.uuid)
            } else {
                val multimedia = if (news.multimedia.isNotEmpty()) news.multimedia[0].url else ""
                newsDao.insert(
                    FavoriteNews(
                        webUrl = news.webUrl,
                        snippet = news.snippet,
                        pubDate = news.pubDate,
                        uuid = news.uuid,
                        title = news.headline.main,
                        thumbnail = multimedia,
                        author = news.author.name,
                        leadParagraph = news.leadParagraph,
                        source = news.source
                    )
                )

                isFavorite = true
            }
        }

        return isFavorite
    }

    fun getNewsPager(position: Int, newsList: List<News>) {
        newsPagerState.value = NewsPagerState(isLoading = true)

        viewModelScope.launch {
            val filteredNewsList = mutableListOf<News>()
            var targetPage = if (newsList.size < 10) newsList.size else 10
            var startPrevPosition = position
            var startNextPosition = position
            val filteredPrevNewsList = mutableListOf<News>()
            val filteredNextNewsList = mutableListOf<News>()

            while (targetPage > 1) {
                startPrevPosition--
                startNextPosition++

                if (startPrevPosition >= 0) {
                    filteredPrevNewsList.add(newsList[startPrevPosition])
                    targetPage--
                }
                if (startNextPosition <= newsList.size - 1) {
                    filteredNextNewsList.add(newsList[startNextPosition])
                    targetPage--
                }
            }

            filteredPrevNewsList.reverse()
            filteredNewsList.addAll(filteredPrevNewsList)
            filteredNewsList.add(newsList[position])
            val newPosition = filteredNewsList.lastIndex
            filteredNewsList.addAll(filteredNextNewsList)

            newsPagerState.postValue(
                NewsPagerState(
                    currentIndex = newPosition,
                    news = filteredNewsList
                )
            )
        }
    }
}