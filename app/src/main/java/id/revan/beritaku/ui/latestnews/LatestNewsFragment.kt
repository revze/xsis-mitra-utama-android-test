package id.revan.beritaku.ui.latestnews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import id.revan.beritaku.R
import id.revan.beritaku.data.state.ArticleListState
import id.revan.beritaku.di.Injector
import id.revan.beritaku.helper.constants.StatusCode
import id.revan.beritaku.shared.extensions.hide
import id.revan.beritaku.shared.extensions.show
import id.revan.beritaku.shared.listener.EndlessScrollListener
import id.revan.beritaku.shared.view.NewsItem
import id.revan.beritaku.shared.view.PagingLoaderItem
import id.revan.beritaku.ui.base.BaseViewModelFactory
import kotlinx.android.synthetic.main.fragment_latest_news.*
import kotlinx.android.synthetic.main.layout_error.*
import kotlinx.android.synthetic.main.layout_loader.*
import javax.inject.Inject

class LatestNewsFragment : Fragment() {

    private lateinit var viewModel: LatestNewsViewModel
    private val adapter = GroupAdapter<GroupieViewHolder>()
    private val loaderItem = PagingLoaderItem()

    @Inject
    lateinit var viewModelFactory: BaseViewModelFactory<LatestNewsViewModel>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_latest_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Injector.getApp(requireContext()).inject(this)

        viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(LatestNewsViewModel::class.java)
        viewModel.articleListState.observe(this, articleListStateObserver)

        swr_news.setOnRefreshListener {
            viewModel.refreshArticles()
        }

        val layoutManager = LinearLayoutManager(this.context)
        rv_news.layoutManager = layoutManager
        rv_news.adapter = adapter
        rv_news.addOnScrollListener(object : EndlessScrollListener(layoutManager) {
            override fun loadMoreItems() {
                if (!viewModel.isLoading()) {
                    viewModel.getNextArticles()
                }
            }
        })

        btn_try_again.setOnClickListener {
            viewModel.getNextArticles()
        }

        viewModel.getNextArticles()
    }

    private val articleListStateObserver = Observer<ArticleListState> {
        if (it.isLoading) {
            swr_news.isRefreshing = false
            swr_news.isEnabled = false
            if (viewModel.page == 0) adapter.clear()
            if (viewModel.page > 0) {
                adapter.add(loaderItem)
                return@Observer
            }
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
                adapter.remove(loaderItem)
                showSnackbar(errorMessage)
                return@Observer
            }

            layout_loader.hide()
            iv_error.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_failed_state
                )
            )
            layout_error.show()
            tv_error_message.text = errorMessage
            rv_news.hide()
            return@Observer
        }

        swr_news.isEnabled = true
        it.articles.map {
            adapter.add(NewsItem(it))
        }
        if (viewModel.page > 1) adapter.remove(loaderItem)
        layout_loader.hide()

        if (viewModel.page == 0 && it.articles.isEmpty()) {
            iv_error.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_empty_state
                )
            )
            layout_error.show()
            tv_error_message.text = getString(R.string.empty_article_message)
            rv_news.hide()
        } else {
            layout_error.hide()
            rv_news.show()
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(this.requireView(), message, Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.ok_action), {}).show()
    }
}