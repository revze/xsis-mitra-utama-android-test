package id.revan.beritaku.ui.newsdetail

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.google.android.material.snackbar.Snackbar
import id.revan.beritaku.R
import id.revan.beritaku.data.model.FavoriteNews
import id.revan.beritaku.data.model.News
import id.revan.beritaku.data.state.NewsPagerState
import id.revan.beritaku.di.Injector
import id.revan.beritaku.helper.NetworkHelper
import id.revan.beritaku.shared.extensions.hide
import id.revan.beritaku.shared.extensions.show
import id.revan.beritaku.shared.view.CustomViewPagerAdapter
import id.revan.beritaku.ui.base.BaseViewModelFactory
import id.revan.beritaku.ui.newsdetail.pager.NewsDetailFragment
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_news_detail.*
import kotlinx.android.synthetic.main.layout_loader.*
import javax.inject.Inject

class NewsDetailActivity : AppCompatActivity() {

    companion object {
        const val NEWS_LIST = "news_list"
        const val NEWS_POSITION = "news_position"
    }

    private lateinit var viewModel: NewsDetailViewModel
    private lateinit var menuFavorite: MenuItem
    private lateinit var news: News
    private val filteredNewsList = mutableListOf<News>()
    private val fragments = mutableListOf<Fragment>()
    private lateinit var viewPagerAdapter: CustomViewPagerAdapter

    @Inject
    lateinit var viewModelFactory: BaseViewModelFactory<NewsDetailViewModel>

    @Inject
    lateinit var networkHelper: NetworkHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)

        Injector.getApp(this).inject(this)

        viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(NewsDetailViewModel::class.java)
        viewModel.favoriteNews.observe(this, favoriteNewsObserver)
        viewModel.newsPagerState.observe(this, newsPagerStateObserver)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val newsList = intent.getParcelableArrayListExtra<News>(NEWS_LIST)
        val position = intent.getIntExtra(NEWS_POSITION, 0)

        viewPagerAdapter = CustomViewPagerAdapter(supportFragmentManager, fragments)
        view_pager.adapter = viewPagerAdapter
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                news = filteredNewsList[position]
                viewModel.getNews(news.uuid)
            }
        })

        if (newsList != null) {
            viewModel.getNewsPager(position, newsList)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.news_detail_menu, menu)
        menuFavorite = menu.findItem(R.id.favorite_menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.favorite_menu -> {
                viewModel.updateToDb(news = news, callback = {
                    menuFavorite.icon = ContextCompat.getDrawable(
                        this,
                        if (it) R.drawable.ic_favorite else R.drawable.ic_favorite_border_white
                    )
                    showSnackbar(
                        if (it) getString(R.string.success_add_to_favorite) else getString(
                            R.string.success_delete_from_favorite
                        )
                    )
                })
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private val favoriteNewsObserver = Observer<FavoriteNews> {
        if (this::menuFavorite.isInitialized) {
            menuFavorite.isVisible = true
            menuFavorite.icon = ContextCompat.getDrawable(
                this,
                if (it != null) R.drawable.ic_favorite else R.drawable.ic_favorite_border_white
            )
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(window.decorView.rootView, message, Snackbar.LENGTH_LONG).show()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    private val newsPagerStateObserver = Observer<NewsPagerState> {
        if (it.isLoading) {
            layout_loader.show()
            view_pager.hide()
            return@Observer
        }

        filteredNewsList.addAll(it.news)
        it.news.map {
            fragments.add(NewsDetailFragment.newInstance(it))
        }
        viewPagerAdapter.notifyDataSetChanged()
        view_pager.offscreenPageLimit = fragments.size
        view_pager.currentItem = it.currentIndex
        layout_loader.hide()
        view_pager.show()
        news = it.news[it.currentIndex]
        viewModel.getNews(news.uuid)
    }
}
