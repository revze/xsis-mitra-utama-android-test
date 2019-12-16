package id.revan.beritaku.ui.searchnews

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.revan.beritaku.data.db.dao.KeywordDao
import id.revan.beritaku.data.model.Keyword
import id.revan.beritaku.data.model.News
import id.revan.beritaku.data.repository.ArticleRepository
import id.revan.beritaku.data.state.SearchArticleState
import id.revan.beritaku.domain.Output
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchNewsViewModel @Inject constructor(
    private val repository: ArticleRepository,
    private val keywordDao: KeywordDao
) :
    ViewModel() {
    val searchArticleState = MutableLiveData<SearchArticleState>()
    var page = 0
        private set
    private var hasReachedMax = false
    private val sort = "newest"
    var query = ""
        private set
    val keywords = MutableLiveData<List<Keyword>>().apply {
        getKeywords()
    }
    val newsList = mutableListOf<News>()

    fun searchArticle(query: String) {
        page = 0
        hasReachedMax = false
        this.query = query
        newsList.clear()
        getNextArticles()
        viewModelScope.launch {
            val keyword = keywordDao.getKeyword(query)
            if (keyword != null) {
                keywordDao.delete(keyword)
            }
            keywordDao.insert(Keyword(name = query))
        }
    }

    fun getNextArticles() {
        if (!hasReachedMax) {
            searchArticleState.value = SearchArticleState(isLoading = true)
            viewModelScope.launch {
                val result = repository.searchArticle(query, page, sort)

                when (result) {
                    is Output.Success -> {
                        if (result.output.isNotEmpty()) {
                            page++
                        } else {
                            hasReachedMax = true
                        }
                        newsList.addAll(result.output)
                        searchArticleState.postValue(SearchArticleState(articles = result.output))
                    }
                    is Output.Error -> searchArticleState.postValue(
                        SearchArticleState(
                            errorCode = result.code
                        )
                    )
                }
            }
        }
    }

    fun isLoading(): Boolean {
        val currentState = searchArticleState.value

        if (currentState != null) {
            return currentState.isLoading
        }
        return false
    }

    fun getKeywords() {
        viewModelScope.launch {
            keywords.postValue(keywordDao.getAll())
        }
    }
}