package id.revan.beritaku.ui.newsdetail

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
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
import id.revan.beritaku.helper.NetworkHelper
import id.revan.beritaku.shared.extensions.hide
import id.revan.beritaku.shared.extensions.show
import id.revan.beritaku.ui.base.BaseViewModelFactory
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_news_detail.*
import kotlinx.android.synthetic.main.layout_error.*
import kotlinx.android.synthetic.main.layout_loader.*
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
        val webUrl = news?.webUrl
        images = intent.getParcelableArrayListExtra(IMAGES)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        wv_news_detail.settings.javaScriptEnabled = true
        wv_news_detail.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                layout_loader.hide()
                if (networkHelper.hasNetwork(this@NewsDetailActivity)) {
                    layout_error.hide()
                    wv_news_detail.show()
                } else {
                    layout_error.show()
                    wv_news_detail.hide()
                }
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)

                layout_error.hide()
                layout_loader.show()
                wv_news_detail.hide()
            }
        }
        wv_news_detail.loadUrl(webUrl)

        btn_try_again.setOnClickListener {
            wv_news_detail.loadUrl(webUrl)
        }

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

    override fun onBackPressed() {
        if (wv_news_detail.canGoBack()) {
            wv_news_detail.goBack()
            return
        }
        super.onBackPressed()
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
