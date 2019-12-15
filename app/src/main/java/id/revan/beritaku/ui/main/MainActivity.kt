package id.revan.beritaku.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import id.revan.beritaku.R
import id.revan.beritaku.shared.view.CustomViewPagerAdapter
import id.revan.beritaku.ui.favoritenews.FavoriteNewsFragment
import id.revan.beritaku.ui.latestnews.LatestNewsFragment
import id.revan.beritaku.ui.searchnews.SearchNewsActivity
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        view_pager.adapter = CustomViewPagerAdapter(
            supportFragmentManager,
            mutableListOf(LatestNewsFragment(), FavoriteNewsFragment())
        )
        nav_view.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_latest -> view_pager.currentItem = 0
                R.id.navigation_favorite -> view_pager.currentItem = 1
            }
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search_menu -> {
                val searchIntent = Intent(this, SearchNewsActivity::class.java)
                startActivity(searchIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }
}
