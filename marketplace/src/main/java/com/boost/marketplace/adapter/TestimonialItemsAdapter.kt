package com.boost.marketplace.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.AllTestimonial
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Testimonial
import com.boost.marketplace.R
import java.util.ArrayList

class TestimonialItemsAdapter(val list: ArrayList<Testimonial>) :
    RecyclerView.Adapter<TestimonialItemsAdapter.PagerVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerVH {
        val item = View.inflate(parent.context, R.layout.pager_item, null)
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

    fun addupdates(newList: List<Testimonial>) {
        val initPosition = list.size
        list.clear()
        list.addAll(newList)
        notifyItemRangeInserted(initPosition, list.size)
    }

    override fun onBindViewHolder(holder: PagerVH, position: Int) {
        holder.name.text = list[position].name
    }

    class PagerVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name = itemView.findViewById<TextView>(R.id.name)

    }

}