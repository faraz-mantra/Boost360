package com.boost.marketplace.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.dbcenterapi.data.api_model.CustomDomain.Domain
import com.boost.marketplace.R
import com.boost.marketplace.interfaces.DomainListener
import com.boost.marketplace.ui.details.domain.CustomDomainActivity


class CustomDomainListAdapter1(
    val activity: CustomDomainActivity,
    itemList: List<Domain>?,
    listen: DomainListener
) : RecyclerView.Adapter<CustomDomainListAdapter1.upgradeViewHolder>() {

    private var upgradeList = ArrayList<Domain>()
    private lateinit var context: Context
    var selectedPosition: Int = -1
    private lateinit var listener: DomainListener

    init {
        this.upgradeList = itemList as ArrayList<Domain>
        listener = listen
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(
            R.layout.layout_suggested_domain_item1, parent, false
        )
        context = itemView.context

        return upgradeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return upgradeList.size
    }

    override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {
        holder.title.text = upgradeList.get(position).name

        if (selectedPosition == position)
            holder.itemView.setBackgroundResource(R.color.colorAccent2);
        else
            holder.itemView.setBackgroundResource(R.color.white);
        holder.itemView.setOnClickListener {
            selectedPosition = position;
            notifyDataSetChanged();
            listener.onSearchedDomain(upgradeList.get(position))
        }
        holder.btn_Select.setOnClickListener {
            listener.onSearchedDomain(upgradeList.get(position))
        }

    }

    fun addupdates(availableDomains: List<Domain>) {
        upgradeList = availableDomains as ArrayList<Domain>
        notifyDataSetChanged()
//        val initPosition = upgradeList.size
//        upgradeList.clear()
//        upgradeList.addAll(availableDomains)
//        notifyItemRangeInserted(initPosition, upgradeList.size)
    }

    class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title = itemView.findViewById<TextView>(R.id.tv_11)
        var tv_recommend = itemView.findViewById<TextView>(R.id.tv_recommend)
        var dummy1 = itemView.findViewById<View>(R.id.dummy1)
        var btn_Select = itemView.findViewById<Button>(R.id.btn_Select)
    }

}