package com.boost.marketplace.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
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
            R.layout.pack_details_testimonials, parent, false
        )
        context = itemView.context

        return upgradeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return upgradeList.size
    }

    override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {
        holder.title.text = upgradeList[position].feature_code

        when ((position + 1) % 5) {
            0 -> holder.cardView.setCardBackgroundColor(Color.parseColor("#FFEFDA"))
            1 -> holder.cardView.setCardBackgroundColor(Color.parseColor("#D8E3FF"))
            2 -> holder.cardView.setCardBackgroundColor(Color.parseColor("#CDFAE3"))
            3 -> holder.cardView.setCardBackgroundColor(Color.parseColor("#C9E2FF"))
            4 -> holder.cardView.setCardBackgroundColor(Color.parseColor("#D8E3FF"))
            else -> holder.cardView.setCardBackgroundColor(Color.parseColor("#FFDCE4"))
        }

    }

    fun addupdates(upgradeModel: List<IncludedFeature>) {
        val initPosition = upgradeList.size
        upgradeList.clear()
        upgradeList.addAll(upgradeModel)
        notifyItemRangeInserted(initPosition, upgradeList.size)
    }

    class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title = itemView.findViewById<TextView>(R.id.title)
        var cardView = itemView.findViewById<CardView>(R.id.cardView)
    }
}