package com.boost.upgrades.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.boost.upgrades.R
import com.boost.upgrades.data.model.WidgetModel

class DetailsAdapter(cryptoCurrencies: List<WidgetModel>?) : RecyclerView.Adapter<DetailsAdapter.upgradeViewHolder>() {

    private var upgradeList = ArrayList<WidgetModel>()
    private lateinit var context : Context
    init { this.upgradeList = cryptoCurrencies as ArrayList<WidgetModel> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(
            R.layout.snippet_items, parent, false)
        context = itemView.context

        return upgradeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {

    }

    fun addupdates(upgradeModel: List<WidgetModel>) {
        val initPosition = upgradeList.size
        upgradeList.clear()
        upgradeList.addAll(upgradeModel)
        notifyItemRangeInserted(initPosition, upgradeList.size)
    }

    class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun upgradeListItem(updateModel: WidgetModel) {

        }
    }
}