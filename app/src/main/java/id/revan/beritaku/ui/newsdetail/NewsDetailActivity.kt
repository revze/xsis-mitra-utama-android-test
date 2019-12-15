package id.revan.beritaku.ui.newsdetail

import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import id.revan.beritaku.R
import id.revan.beritaku.shared.extensions.hide
import id.revan.beritaku.shared.extensions.show
import kotlinx.android.synthetic.main.activity_news_detail.*
import kotlinx.android.synthetic.main.layout_error.*
import kotlinx.android.synthetic.main.layout_loader.*

class NewsDetailActivity : AppCompatActivity() {

    companion object {
        const val ID = "id"
        const val WEB_URL = "web_url"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)

        val webUrl = intent.getStringExtra(WEB_URL)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        wv_news_detail.settings.javaScriptEnabled = true
        wv_news_detail.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                layout_error.hide()
                layout_loader.hide()
                wv_news_detail.show()
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)

                layout_error.hide()
                layout_loader.show()
                wv_news_detail.hide()
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)

                layout_error.show()
                layout_loader.hide()
                wv_news_detail.hide()
            }

            override fun onReceivedHttpError(
                view: WebView?,
                request: WebResourceRequest?,
                errorResponse: WebResourceResponse?
            ) {
                super.onReceivedHttpError(view, request, errorResponse)

                layout_error.show()
                layout_loader.hide()
                wv_news_detail.hide()
            }
        }
        wv_news_detail.loadUrl(webUrl)

        btn_try_again.setOnClickListener {
            wv_news_detail.loadUrl(webUrl)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.news_detail_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.favorite_menu -> {
                Toast.makeText(this, "Favorite menu", Toast.LENGTH_LONG).show()
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
}
