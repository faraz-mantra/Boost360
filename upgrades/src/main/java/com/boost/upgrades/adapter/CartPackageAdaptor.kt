package com.boost.upgrades.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.boost.upgrades.R
import com.boost.upgrades.data.model.WidgetModel


class CartPackageAdaptor(cryptoCurrencies: List<WidgetModel>?) : RecyclerView.Adapter<CartPackageAdaptor.upgradeViewHolder>() {

    private var upgradeList = ArrayList<WidgetModel>()
    private lateinit var context : Context
    init { this.upgradeList = cryptoCurrencies as ArrayList<WidgetModel> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(
            R.layout.cart_single_package, parent, false)
        context = itemView.context

        return upgradeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return 2
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