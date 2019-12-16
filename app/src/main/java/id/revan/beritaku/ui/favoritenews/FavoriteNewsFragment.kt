package id.revan.beritaku.ui.favoritenews

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
import id.revan.beritaku.data.model.News
import id.revan.beritaku.di.Injector
import id.revan.beritaku.shared.extensions.hide
import id.revan.beritaku.shared.extensions.show
import id.revan.beritaku.shared.view.NewsItem
import id.revan.beritaku.ui.base.BaseViewModelFactory
import kotlinx.android.synthetic.main.fragment_latest_news.*
import kotlinx.android.synthetic.main.layout_error.*
import javax.inject.Inject

class FavoriteNewsFragment : Fragment() {

    private lateinit var viewModel: FavoriteNewsViewModel
    private val adapter = GroupAdapter<GroupieViewHolder>()

    @Inject
    lateinit var viewModelFactory: BaseViewModelFactory<FavoriteNewsViewModel>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Injector.getApp(requireContext()).inject(this)

        viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(FavoriteNewsViewModel::class.java)
        viewModel.news.observe(this, newsObserver)

        rv_news.layoutManager = LinearLayoutManager(this.context)
        rv_news.adapter = adapter

        btn_try_again.hide()
    }

    private val newsObserver = Observer<List<News>> {
        if (it.isEmpty()) {
            iv_error.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_empty_state
                )
            )
            layout_error.show()
            tv_error_message.text = getString(R.string.empty_article_message)
            rv_news.hide()
            return@Observer
        }

        layout_error.hide()
        adapter.clear()
        it.map {
            adapter.add(NewsItem(it, true, {
                viewModel.removeFromFavorite(it.uuid)
                showSnackbar(getString(R.string.success_delete_from_favorite))
            }))
        }
        rv_news.show()
        if (it.isNotEmpty()) rv_news.scrollToPosition(0)
    }

    override fun onResume() {
        super.onResume()

        viewModel.getNews()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(this.requireView(), message, Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.ok_action), {}).show()
    }
}