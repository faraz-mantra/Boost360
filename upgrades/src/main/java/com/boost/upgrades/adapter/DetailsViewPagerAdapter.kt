package com.boost.upgrades.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.boost.upgrades.R
import com.boost.upgrades.data.model.UpdatesModel
import java.util.*

class DetailsViewPagerAdapter(val list: ArrayList<UpdatesModel>) : RecyclerView.Adapter<DetailsViewPagerAdapter.PagerVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerVH {
        val item = View.inflate(parent.getContext(), R.layout.snippet_items, null)
        val lp = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        item.layoutParams = lp
        return PagerVH(item)
    }

    override fun getItemCount(): Int{
        return 5//list.size
    }

    override fun onBindViewHolder(holder: PagerVH, position: Int) = holder.itemView.run {
//        image_title.setText(list.get(position).title)
//        image_description.setText(list.get(position).description)
//        page_images.setImageResource(list.get(position).image!!)
    }

    class PagerVH(itemView: View) : RecyclerView.ViewHolder(itemView)
}