package id.revan.beritaku.ui.newsdetail

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import id.revan.beritaku.R
import id.revan.beritaku.data.model.FavoriteNews
import id.revan.beritaku.data.model.News
import id.revan.beritaku.data.model.NewsMultimedia
import id.revan.beritaku.di.Injector
import id.revan.beritaku.helper.DateTimeHelper
import id.revan.beritaku.helper.NetworkHelper
import id.revan.beritaku.shared.extensions.GlideApp
import id.revan.beritaku.ui.base.BaseViewModelFactory
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_news_detail.*
import javax.inject.Inject

class NewsDetailActivity : AppCompatActivity() {

    companion object {
        const val NEWS = "news"
        const val IMAGES = "images"
    }

    private lateinit var viewModel: NewsDetailViewModel
    private lateinit var menuFavorite: MenuItem
    private lateinit var news: News
    private lateinit var images: List<NewsMultimedia>

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

        news = intent.getParcelableExtra(NEWS)
        images = intent.getParcelableArrayListExtra(IMAGES)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val thumbnail = if (images.isNotEmpty()) "https://www.nytimes.com/${images[0].url}" else ""
        val pubDate = DateTimeHelper.convertTimestampToReadableTime(news.pubDate)
        val author = news.author
        val authorName = if (author.name != null) author.name.replace("By ", "") else ""

        if (thumbnail.isNotEmpty()) {
            GlideApp.with(this).load(thumbnail).centerCrop().into(iv_thumbnail)
        }
        tv_title.text = news.headline.main
        tv_author.text = if (authorName.isEmpty()) news.source else "$authorName - ${news.source}"
        tv_date.text = "$pubDate WIB"
        tv_description.text = news.leadParagraph

        viewModel.getNews(news.uuid)
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
                viewModel.updateToDb(news, images, {
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
        if (it != null) {
            menuFavorite.icon = ContextCompat.getDrawable(this, R.drawable.ic_favorite)
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(window.decorView.rootView, message, Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.ok_action), {}).show()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }
}
