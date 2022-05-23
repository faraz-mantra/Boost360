package com.boost.marketplace.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.AllTestimonial
import com.boost.marketplace.R
import java.util.*

class ReviewViewPagerAdapter(val list: ArrayList<AllTestimonial>) :
    RecyclerView.Adapter<ReviewViewPagerAdapter.PagerVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerVH {
        val item = View.inflate(parent.context, R.layout.snippet_items, null)
        val lp = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        item.layoutParams = lp
        return PagerVH(item)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addupdates(newList: List<AllTestimonial>) {
        val initPosition = list.size
        list.clear()
        list.addAll(newList)
        notifyItemRangeInserted(initPosition, list.size)
    }

    override fun onBindViewHolder(holder: PagerVH, position: Int) {
        holder.name.text = list[position].name
        holder.businessType.text = list[position].title
        holder.desc.text = list[position].text

    }

    class PagerVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name = itemView.findViewById<TextView>(R.id.name)
        var businessType = itemView.findViewById<TextView>(R.id.textView16)
        var desc = itemView.findViewById<TextView>(R.id.review_description)
    }

}