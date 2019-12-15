package id.revan.beritaku.ui.searchnews

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import id.revan.beritaku.R
import id.revan.beritaku.data.model.Keyword
import id.revan.beritaku.data.state.SearchArticleState
import id.revan.beritaku.di.Injector
import id.revan.beritaku.helper.constants.StatusCode
import id.revan.beritaku.shared.extensions.hide
import id.revan.beritaku.shared.extensions.hideKeyboard
import id.revan.beritaku.shared.extensions.show
import id.revan.beritaku.shared.extensions.showSoftkeyboard
import id.revan.beritaku.shared.listener.EndlessScrollListener
import id.revan.beritaku.shared.view.KeywordHistoryItem
import id.revan.beritaku.shared.view.NewsItem
import id.revan.beritaku.shared.view.PagingLoaderItem
import id.revan.beritaku.ui.base.BaseViewModelFactory
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_search_news.*
import kotlinx.android.synthetic.main.layout_error.*
import kotlinx.android.synthetic.main.layout_loader.*
import javax.inject.Inject

class SearchNewsActivity : AppCompatActivity() {

    private val newsAdapter = GroupAdapter<GroupieViewHolder>()
    private val keywordAdapter = GroupAdapter<GroupieViewHolder>()
    private val loaderItem = PagingLoaderItem()

    private lateinit var viewModel: SearchNewsViewModel

    @Inject
    lateinit var viewModelFactory: BaseViewModelFactory<SearchNewsViewModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_news)

        Injector.getApp(this).inject(this)

        viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(SearchNewsViewModel::class.java)
        viewModel.searchArticleState.observe(this, searchArticleStateObserver)
        viewModel.keywords.observe(this, keywordsObserver)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        edt_search.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (edt_search.text.toString().isBlank()) {
                    Toast.makeText(
                        this,
                        getString(R.string.empty_keyword_error_message),
                        Toast.LENGTH_LONG
                    ).show()
                } else if (!viewModel.isLoading()) {
                    viewModel.searchArticle(edt_search.text.toString().trim())
                    hideKeyboard()
                }
            }

            true
        }
        edt_search.setOnClickListener {
            viewModel.getKeywords()
            layout_search_result.hide()
            layout_keywords_history.show()
        }

        val layoutManager = LinearLayoutManager(this)
        rv_news.layoutManager = layoutManager
        rv_news.adapter = newsAdapter
        rv_news.addOnScrollListener(object : EndlessScrollListener(layoutManager) {
            override fun loadMoreItems() {
                if (!viewModel.isLoading()) {
                    viewModel.getNextArticles()
                }
            }
        })

        rv_keyword_history.layoutManager = LinearLayoutManager(this)
        rv_keyword_history.adapter = keywordAdapter

        btn_try_again.setOnClickListener {
            viewModel.searchArticle(edt_search.text.toString().trim())
        }

        layout_search_result.hide()
        layout_keywords_history.show()
        edt_search.showSoftkeyboard()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private val searchArticleStateObserver = Observer<SearchArticleState> {
        if (it.isLoading) {
            if (viewModel.page == 0) newsAdapter.clear()
            if (viewModel.page > 0) {
                newsAdapter.add(loaderItem)
                return@Observer
            }
            layout_keywords_history.hide()
            layout_search_result.show()
            layout_loader.show()
            layout_error.hide()
            rv_news.hide()
            return@Observer
        }

        if (it.errorCode != StatusCode.NO_ERROR) {
            val errorMessage =
                if (it.errorCode == StatusCode.NETWORK_ERROR) getString(R.string.no_internet_error_message) else getString(
                    R.string.general_error_message
                )

            if (viewModel.page > 0) {
                newsAdapter.remove(loaderItem)
                showSnackbar(errorMessage)
                return@Observer
            }

            layout_loader.hide()
            iv_error.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_failed_state
                )
            )
            layout_error.show()
            tv_error_message.text = errorMessage
            rv_news.hide()
            return@Observer
        }

        it.articles.map {
            newsAdapter.add(NewsItem(it))
        }
        if (viewModel.page == 1) rv_news.scrollToPosition(0)
        if (viewModel.page > 1) newsAdapter.remove(loaderItem)
        layout_loader.hide()

        if (viewModel.page == 0 && it.articles.isEmpty()) {
            iv_error.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_empty_state
                )
            )
            layout_error.show()
            tv_error_message.text = getString(R.string.article_not_found_message)
            rv_news.hide()
        } else {
            layout_error.hide()
            rv_news.show()
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(window.decorView.rootView, message, Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.ok_action), {}).show()
    }

    private val keywordsObserver = Observer<List<Keyword>> {
        keywordAdapter.clear()
        it.map {
            keywordAdapter.add(KeywordHistoryItem(it.name, {
                layout_search_result.show()
                layout_keywords_history.hide()
                hideKeyboard()
                edt_search.setText(it)
                viewModel.searchArticle(edt_search.text.toString().trim())
            }))
        }
    }

    override fun onBackPressed() {
        if (viewModel.page > 0 && layout_keywords_history.isVisible) {
            layout_keywords_history.hide()
            layout_search_result.show()
            return
        }
        super.onBackPressed()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }
}
