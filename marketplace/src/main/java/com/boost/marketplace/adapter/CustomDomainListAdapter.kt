package com.boost.marketplace.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.dbcenterapi.data.api_model.CustomDomain.Domain
import com.boost.marketplace.R
import com.boost.marketplace.ui.details.domain.CustomDomainActivity


class CustomDomainListAdapter(
    val activity: CustomDomainActivity, itemList: List<Domain>?,
    // val listener: HomeListener
) : RecyclerView.Adapter<CustomDomainListAdapter.upgradeViewHolder>() {

    private var upgradeList = ArrayList<Domain>()
    private lateinit var context: Context
    var selectedPosition: Int = -1

    init {
        this.upgradeList = itemList as ArrayList<Domain>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(
            R.layout.layout_suggested_domain_item, parent, false
        )
        context = itemView.context

        return upgradeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return upgradeList.size
    }

    override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {
        holder.title.text = upgradeList.get(position).name
//        holder.dummy1.visibility=if (position == upgradeList.lastIndex) {
//            View.GONE
//        } else {
//            View.VISIBLE
//        }
        holder.tv_recommend.visibility = if (position == 0) {
            View.VISIBLE
        } else {
            View.GONE
        }

        if (selectedPosition == position)
            holder.itemView.setBackgroundResource(R.color.colorAccent2);
        else
            holder.itemView.setBackgroundResource(R.color.white);
        holder.itemView.setOnClickListener {
            selectedPosition = position;
            notifyDataSetChanged();
        }

    }

    fun addupdates(availableDomains: List<Domain>) {
        val initPosition = upgradeList.size
        upgradeList.clear()
        upgradeList.addAll(availableDomains)
        notifyItemRangeInserted(initPosition, upgradeList.size)
    }

    class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title = itemView.findViewById<TextView>(R.id.tv_title)
        var tv_recommend = itemView.findViewById<TextView>(R.id.tv_recommend)
        var dummy1 = itemView.findViewById<View>(R.id.dummy1)
    }

}