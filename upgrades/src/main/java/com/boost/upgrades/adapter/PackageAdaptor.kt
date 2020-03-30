package com.boost.upgrades.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.boost.upgrades.R
import com.boost.upgrades.data.model.WidgetModel


class PackageAdaptor(cryptoCurrencies: List<WidgetModel>?) : RecyclerView.Adapter<PackageAdaptor.upgradeViewHolder>() {

    private var upgradeList = ArrayList<WidgetModel>()
    private lateinit var context : Context
    init { this.upgradeList = cryptoCurrencies as ArrayList<WidgetModel> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(
            R.layout.upgrade_list_item, parent, false)
        context = itemView.context

        return upgradeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {

        if(position == 4){
            holder.view.visibility = View.GONE
        }
    }

    fun addupdates(upgradeModel: List<WidgetModel>) {
        val initPosition = upgradeList.size
        upgradeList.clear()
        upgradeList.addAll(upgradeModel)
        notifyItemRangeInserted(initPosition, upgradeList.size)
    }

    class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val view = itemView.findViewById<View>(R.id.view)!!

    }
}