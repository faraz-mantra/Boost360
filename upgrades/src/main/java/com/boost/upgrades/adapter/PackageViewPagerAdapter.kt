package com.boost.upgrades.adapter

import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.boost.upgrades.R
import com.boost.upgrades.data.model.UpdatesModel
import com.boost.upgrades.interfaces.HomeListener
import kotlinx.android.synthetic.main.package_item.view.*
import java.util.*

class PackageViewPagerAdapter(
    val list: ArrayList<UpdatesModel>, val homeListener: HomeListener)
    : RecyclerView.Adapter<PackageViewPagerAdapter.PagerViewHolder>(), View.OnClickListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        val item = View.inflate(parent.getContext(), R.layout.package_item, null)
        val lp = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        item.layoutParams = lp
        item.setOnClickListener(this)
        return PagerViewHolder(item)
    }

    override fun getItemCount(): Int{
        return 5//list.size
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) = holder.itemView.run {
        spannableString(this)
//        image_title.setText(list.get(position).title)
//        image_description.setText(list.get(position).description)
//        page_images.setImageResource(list.get(position).image!!)
    }

    class PagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    }

    override fun onClick(v: View?) {
        homeListener.onPackageClicked(v)
    }

    fun spannableString(v: View) {
        val origCost = SpannableString("₹2,559/month")

        origCost.setSpan(
            StrikethroughSpan(),
            0,
            origCost.length,
            0
        )
        v.orig_cost.setText(origCost)
    }
}