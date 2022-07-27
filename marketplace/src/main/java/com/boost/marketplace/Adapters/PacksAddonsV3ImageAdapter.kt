package com.boost.marketplace.Adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.dbcenterapi.data.api_model.packageAddonsCompares.AddonsPacksIn
import com.boost.dbcenterapi.data.api_model.packageAddonsCompares.PackageAddonsCompares
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.marketplace.R

class PacksAddonsV3ImageAdapter(
    cryptoCurrencies: List<AddonsPacksIn>?,
    val activity: Activity
) : RecyclerView.Adapter<PacksAddonsV3ImageAdapter.upgradeViewHolder>() {

    private var compareList = ArrayList<AddonsPacksIn>()
    private lateinit var context: Context

    init {
        this.compareList = cryptoCurrencies as ArrayList<AddonsPacksIn>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(
            R.layout.item_packsv3_image_addons, parent, false
        )
        context = itemView.context
        return upgradeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return compareList.size
    }

    override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {
        if(compareList.get(position).packageStatus)
            holder.image.visibility = View.VISIBLE
        else
            holder.image.visibility = View.INVISIBLE
    }

    fun addupdates(upgradeModel: List<AddonsPacksIn>) {
        compareList.clear()
        compareList.addAll(upgradeModel)
        notifyDataSetChanged()
    }

    class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<ImageView>(R.id.imageStatus)!!
    }
}