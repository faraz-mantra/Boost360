package com.boost.marketplace.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.marketplace.R
import com.bumptech.glide.Glide

class NeedMoreFeatureAdapter(
    val activity: Activity,
    cryptoCurrencies: List<FeaturesModel>?
) : RecyclerView.Adapter<NeedMoreFeatureAdapter.upgradeViewHolder>() {

    private var upgradeList = ArrayList<FeaturesModel>()
    private lateinit var context: Context

    init {
        this.upgradeList = cryptoCurrencies as ArrayList<FeaturesModel>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(
            R.layout.item_more_feature, parent, false
        )
        context = itemView.context

        return upgradeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return upgradeList.size
    }

    override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {
        Glide.with(context).load(upgradeList.get(position).primary_image).into(holder.icon)
    }

    fun addupdates(upgradeModel: List<FeaturesModel>) {
        val initPosition = upgradeList.size
        upgradeList.clear()
        upgradeList.addAll(upgradeModel)
        notifyItemRangeInserted(initPosition, upgradeList.size)
    }

    class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var icon = itemView.findViewById<ImageView>(R.id.icon)
    }
}