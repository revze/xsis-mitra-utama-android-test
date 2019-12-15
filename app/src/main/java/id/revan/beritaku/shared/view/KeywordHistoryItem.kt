package id.revan.beritaku.shared.view

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.revan.beritaku.R
import kotlinx.android.synthetic.main.item_row_keyword_history.view.*


class KeywordHistoryItem(
    private val keyword: String,
    private val callback: (keyword: String) -> Unit
) : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val itemView = viewHolder.itemView
        itemView.tv_keyword.text = keyword
        itemView.tv_keyword.setOnClickListener {
            callback(keyword)
        }
    }

    override fun getLayout() = R.layout.item_row_keyword_history
}