package id.revan.beritaku.ui.latestnews

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.revan.beritaku.data.repository.ArticleRepository
import id.revan.beritaku.data.state.ArticleListState
import id.revan.beritaku.domain.Output
import kotlinx.coroutines.launch
import javax.inject.Inject

class LatestNewsViewModel @Inject constructor(private val repository: ArticleRepository) :
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
}