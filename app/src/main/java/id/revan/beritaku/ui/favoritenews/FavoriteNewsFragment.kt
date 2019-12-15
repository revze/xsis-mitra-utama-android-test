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
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import id.revan.beritaku.R
import id.revan.beritaku.shared.extensions.hide
import id.revan.beritaku.shared.extensions.show
import kotlinx.android.synthetic.main.fragment_latest_news.*
import kotlinx.android.synthetic.main.layout_error.*
import kotlinx.android.synthetic.main.layout_loader.*

class FavoriteNewsFragment : Fragment() {

    private lateinit var favoriteNewsViewModel: FavoriteNewsViewModel
    private val adapter = GroupAdapter<GroupieViewHolder>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        favoriteNewsViewModel =
            ViewModelProviders.of(this).get(FavoriteNewsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_favorite_news, container, false)
        favoriteNewsViewModel.text.observe(this, Observer {
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv_news.layoutManager = LinearLayoutManager(this.context)
        rv_news.adapter = adapter

        layout_loader.hide()
        iv_error.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_empty_state
            )
        )
        layout_error.show()
        tv_error_message.text = getString(R.string.empty_article_message)
        rv_news.hide()
    }
}