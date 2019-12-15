package id.revan.beritaku.shared.view

import android.content.Intent
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.revan.beritaku.R
import id.revan.beritaku.data.model.News
import id.revan.beritaku.helper.DateTimeHelper
import id.revan.beritaku.shared.extensions.GlideApp
import id.revan.beritaku.shared.extensions.hide
import id.revan.beritaku.shared.extensions.show
import id.revan.beritaku.ui.newsdetail.NewsDetailActivity
import kotlinx.android.synthetic.main.item_row_news.view.*


class NewsItem(
    private val news: News,
    private val isActionEnabled: Boolean = false,
    private val callback: (news: News) -> Unit = {}
) : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val itemView = viewHolder.itemView
        val context = viewHolder.itemView.context
        val arrayListMultimedia = news.multimedia as ArrayList

        itemView.tv_date.text = DateTimeHelper.convertTimestampToLocalTime(news.pubDate)
        itemView.tv_snippet.text = news.snippet
        itemView.tv_title.text = news.headline.main
        if (news.multimedia.isNotEmpty()) {
            GlideApp.with(context).load("https://www.nytimes.com/${news.multimedia[0].url}")
                .centerCrop()
                .into(itemView.iv_thumbnail)
        }
        itemView.layout_news.setOnClickListener {
            val intent = Intent(context, NewsDetailActivity::class.java)
            intent.putExtra(NewsDetailActivity.NEWS, news)
            intent.putExtra(NewsDetailActivity.IMAGES, arrayListMultimedia)
            context.startActivity(intent)
        }
        if (isActionEnabled) {
            itemView.iv_favorite.show()
        } else {
            itemView.iv_favorite.hide()
        }
        itemView.iv_favorite.setOnClickListener {
            callback(news)
        }
    }


    override fun getLayout() = R.layout.item_row_news
}