package id.revan.beritaku.shared.view

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import id.revan.beritaku.R

class PagingLoaderItem : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

    }

    override fun getLayout() = R.layout.item_row_paging_loader
}