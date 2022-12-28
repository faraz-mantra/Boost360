package com.boost.marketplace.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.marketplace.R
import com.boost.marketplace.interfaces.PackDetailsListener
import com.bumptech.glide.Glide

class PackDetailsFeatureAdapter(
    val activity: Activity,
    cryptoCurrencies: List<FeaturesModel>?,val listener:PackDetailsListener
) : RecyclerView.Adapter<PackDetailsFeatureAdapter.upgradeViewHolder>() {

    private var upgradeList = ArrayList<FeaturesModel>()
    var minMonth = 1
    private lateinit var context: Context

    init {
        this.upgradeList = cryptoCurrencies as ArrayList<FeaturesModel>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(
            R.layout.item_pack_details, parent, false
        )
        context = itemView.context

        return upgradeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return upgradeList.size
    }

    override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {

        holder.name.setText(upgradeList.get(position).description_title)
        holder.title.setText(upgradeList.get(position).name)

        Glide.with(context).load(upgradeList.get(position).primary_image).into(holder.image)
        holder.view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        if (position == upgradeList.size - 1) {
            holder.view.visibility = View.INVISIBLE
        }

        holder.itemView.setOnClickListener {
            listener.onclick(upgradeList[position])
        }
    }

    fun addupdates(upgradeModel: List<FeaturesModel>) {
        val initPosition = upgradeList.size
        upgradeList.clear()
        upgradeList.addAll(upgradeModel)
        notifyItemRangeInserted(initPosition, upgradeList.size)
    }

    class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val view = itemView.findViewById<View>(R.id.dummy_view)!!
        val image = itemView.findViewById<ImageView>(R.id.imageView2)!!
        val name = itemView.findViewById<TextView>(R.id.desc)!!
        val title = itemView.findViewById<TextView>(R.id.title)!!
        val arrow_icon = itemView.findViewById<RelativeLayout>(R.id.arrow_icon)!!

    }

}