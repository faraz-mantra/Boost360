package com.boost.marketplace.Adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.marketplace.R

class PacksAddonsV3Adapter(
    cryptoCurrencies: List<FeaturesModel>?,
    val activity: Activity
) : RecyclerView.Adapter<PacksAddonsV3Adapter.upgradeViewHolder>() {

    private var upgradeList = ArrayList<FeaturesModel>()
    var minMonth = 1
    private lateinit var context: Context

    init {
        this.upgradeList = cryptoCurrencies as ArrayList<FeaturesModel>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(
            R.layout.item_packsv3_addons, parent, false
        )
        context = itemView.context
        return upgradeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return upgradeList.size
    }

    override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {

        holder.name.setText(upgradeList.get(2).name)
        val cryptocurrencyItem = upgradeList[position]
     //   holder.upgradeListItem(holder, cryptocurrencyItem, activity)
//        Glide.with(context).load(upgradeList.get(position).primary_image).into(holder.image)
//        holder.itemView.setOnClickListener {
//            addonsListener.onAddonsClicked(upgradeList.get(position))
//        }
    }

    fun addupdates(upgradeModel: List<FeaturesModel>, noOfMonth: Int) {
        minMonth = noOfMonth
        val initPosition = upgradeList.size
        upgradeList.clear()
        upgradeList.addAll(upgradeModel)
        notifyItemRangeInserted(initPosition, upgradeList.size)
    }

    class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.addon_title)!!
//        val name = itemView.findViewById<TextView>(R.id.title)!!
//        val price = itemView.findViewById<TextView>(R.id.price)!!

    }
}