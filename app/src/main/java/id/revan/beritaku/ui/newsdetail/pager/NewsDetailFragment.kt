package id.revan.beritaku.ui.newsdetail.pager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import id.revan.beritaku.R
import id.revan.beritaku.data.model.News
import id.revan.beritaku.helper.DateTimeHelper
import id.revan.beritaku.shared.extensions.GlideApp
import kotlinx.android.synthetic.main.news_detail_fragment.*

class NewsDetailFragment : Fragment() {

    companion object {
        const val NEWS = "news"

        fun newInstance(news: News): NewsDetailFragment {
            val fragment = NewsDetailFragment()
            val bundle = Bundle()
            bundle.putParcelable(NEWS, news)
            fragment.arguments = bundle

            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.news_detail_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val args = arguments
        val news: News? = args?.getParcelable(NEWS)

        if (news != null) {
            val thumbnail =
                if (news.multimedia.isNotEmpty()) "https://www.nytimes.com/${news.multimedia[0].url}" else ""
            val pubDate = DateTimeHelper.convertTimestampToReadableTime(news.pubDate)
            val author = news.author
            val authorName = if (author.name != null) author.name.replace("By ", "") else ""

            if (thumbnail.isNotEmpty()) {
                GlideApp.with(this).load(thumbnail).centerCrop().into(iv_thumbnail)
            }
            tv_title.text = news.headline.main
            tv_author.text =
                if (authorName.isEmpty()) news.source else "$authorName - ${news.source}"
            tv_date.text = "$pubDate WIB"
            tv_description.text = news.leadParagraph
        }
    }
}
