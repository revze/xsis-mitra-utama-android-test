package id.revan.beritaku.shared.view

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class CustomViewPagerAdapter(
    private val fragmentManager: FragmentManager,
    private val fragments: List<Fragment>
) : FragmentPagerAdapter(
    fragmentManager,
    FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    override fun getItem(position: Int) = fragments[position]

    override fun getCount() = fragments.size

}