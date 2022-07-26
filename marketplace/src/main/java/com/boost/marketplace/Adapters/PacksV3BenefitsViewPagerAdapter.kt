package com.boost.marketplace.Adapters

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.boost.marketplace.R

class PacksV3BenefitsViewPagerAdapter (
    val list: ArrayList<String>
) : RecyclerView.Adapter<PacksV3BenefitsViewPagerAdapter.PagerViewHolder>() {

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        val item = View.inflate(parent.context, R.layout.item_packs_v3_benefits, null)
        val lp = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        item.layoutParams = lp
        context = item.context
        return PagerViewHolder(item)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {

        holder.title.text = list[position]

        when ((position + 1) % 5) {
            0 -> holder.cardView.setCardBackgroundColor(Color.parseColor("#E1EFFF"))
            1 -> holder.cardView.setCardBackgroundColor(Color.parseColor("#FCEDF0"))
            2 -> holder.cardView.setCardBackgroundColor(Color.parseColor("#C7F3DD"))
            3 -> holder.cardView.setCardBackgroundColor(Color.parseColor("#D8E3FF"))
            4 -> holder.cardView.setCardBackgroundColor(Color.parseColor("#FFEAB3"))
            else -> holder.cardView.setCardBackgroundColor(Color.parseColor("#B3D6FF"))

        }
    }


    fun addupdates(listItems: List<String>) {
        val initPosition = list.size
        list.clear()
        list.addAll(listItems)
        notifyItemRangeInserted(initPosition, list.size)
    }

    class PagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.tv_benefit_title)
        val cardView = itemView.findViewById<CardView>(R.id.cardView)
    }
}