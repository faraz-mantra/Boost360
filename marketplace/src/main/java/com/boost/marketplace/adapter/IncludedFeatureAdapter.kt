package com.boost.marketplace.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.FrequentlyAskedQuestion
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.IncludedFeature
import com.boost.marketplace.R

class IncludedFeatureAdapter(
    val activity: Activity,
    cryptoCurrencies: List<IncludedFeature>?
) : RecyclerView.Adapter<IncludedFeatureAdapter.upgradeViewHolder>() {

    private var upgradeList = ArrayList<IncludedFeature>()
    private lateinit var context: Context

    init {
        this.upgradeList = cryptoCurrencies as ArrayList<IncludedFeature>
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
//        holder.title.text = upgradeList[position].question
//        holder.desc.text = upgradeList[position].answer
        holder.title.setOnClickListener {
            if(holder.desc.isVisible) {
                holder.desc.visibility = View.GONE
            }else{
                holder.desc.visibility = View.VISIBLE
            }
        }
        holder.downArrow.setOnClickListener {
            if(holder.desc.isVisible) {
                holder.downArrow.setImageResource(R.drawable.ic_down_arrow_pack_details)
                holder.desc.visibility = View.GONE
            }else{
                holder.downArrow.setImageResource(R.drawable.ic_up_arrow_with_bg)
                holder.desc.visibility = View.VISIBLE
            }
        }
    }

    fun addupdates(upgradeModel: List<IncludedFeature>) {
        val initPosition = upgradeList.size
        upgradeList.clear()
        upgradeList.addAll(upgradeModel)
        notifyItemRangeInserted(initPosition, upgradeList.size)
    }

    class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var position = itemView.findViewById<TextView>(R.id.position)
        var title = itemView.findViewById<TextView>(R.id.title)
        var downArrow = itemView.findViewById<ImageView>(R.id.arrow_btn)
        var desc = itemView.findViewById<TextView>(R.id.desc)
    }
}