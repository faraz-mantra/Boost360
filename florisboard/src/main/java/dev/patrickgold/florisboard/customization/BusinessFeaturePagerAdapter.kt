package dev.patrickgold.florisboard.customization

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import dev.patrickgold.florisboard.R


class BusinessFeaturePagerAdapter : PagerAdapter() {

    interface BusinessFeatureListener {
        fun onServiceClicked(serviceId: String)
    }

    val tabs: List<String> = BusinessFeatureEnum.values().map { it.name }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(container.context)
        // show UI for respected tab
        val view = when (BusinessFeatureEnum.values()[position]) {
            BusinessFeatureEnum.UPDATES -> inflater.inflate(R.layout.business_updates, container, false)
            BusinessFeatureEnum.INVENTORY -> inflater.inflate(R.layout.business_inventory, container, false)
            BusinessFeatureEnum.PHOTOS -> inflater.inflate(R.layout.business_photos, container, false)
            BusinessFeatureEnum.DETAILS -> inflater.inflate(R.layout.business_details, container, false)
        }
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int = tabs.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean = view === `object`

    override fun getPageTitle(position: Int): CharSequence = tabs[position]
}