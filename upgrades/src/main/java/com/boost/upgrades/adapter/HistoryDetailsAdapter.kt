package com.boost.upgrades.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.upgrades.R
import com.boost.upgrades.data.api_model.GetPurchaseOrder.WidgetPack


class HistoryDetailsAdapter(itemList: List<WidgetPack>?) :
        RecyclerView.Adapter<HistoryDetailsAdapter.upgradeViewHolder>(){

    private var list = ArrayList<WidgetPack>()
    private lateinit var context: Context

    init {
        this.list = itemList as ArrayList<WidgetPack>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(
                R.layout.history_single_item, parent, false
        )
        context = itemView.context

        return upgradeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {

        holder.widgetName.setText(list.get(position).Name)
    }


    fun addupdates(upgradeModel: List<WidgetPack>) {
        val initPosition = list.size
        list.clear()
        list.addAll(upgradeModel)
        notifyItemRangeInserted(initPosition, list.size)
    }

    class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val widgetName = itemView.findViewById<TextView>(R.id.widget_name)

        var context: Context = itemView.context
//
//
//        fun upgradeListItem(updateModel: UpdatesModel) {
//
//        }
    }
}