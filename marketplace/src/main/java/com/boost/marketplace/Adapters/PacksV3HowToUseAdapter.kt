package com.boost.marketplace.Adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.HowToActivate
import com.boost.marketplace.R

class PacksV3HowToUseAdapter(
    val activity: Activity,
    cryptoCurrencies: List<HowToActivate>?
) : RecyclerView.Adapter<PacksV3HowToUseAdapter.upgradeViewHolder>() {

    private var upgradeList = ArrayList<HowToActivate>()
    private lateinit var context: Context

    init {
        this.upgradeList = cryptoCurrencies as ArrayList<HowToActivate>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(
            R.layout.item_packsv3_how_to_use, parent, false
        )
        context = itemView.context
        return upgradeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return upgradeList.size
    }

    override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {
        holder.position.text = (position + 1).toString()
        when (position) {
            0 -> {
                holder.topLine.visibility = View.GONE
                holder.bottomLine.visibility = View.VISIBLE
            }
            5 - 1/*upgradeList.size - 1*/ -> {
                holder.topLine.visibility = View.VISIBLE
                holder.bottomLine.visibility = View.GONE
            }
            else -> {
                holder.topLine.visibility = View.VISIBLE
                holder.bottomLine.visibility = View.VISIBLE
            }
        }
        holder.title.text = upgradeList[position].question
        holder.desc.text = upgradeList[position].answer

    }

    fun addupdates(upgradeModel: List<HowToActivate>) {
        val initPosition = upgradeList.size
        upgradeList.clear()
        upgradeList.addAll(upgradeModel)
        notifyItemRangeInserted(initPosition, upgradeList.size)
    }

    class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var position = itemView.findViewById<TextView>(R.id.position)
        var title = itemView.findViewById<TextView>(R.id.title)
        var desc = itemView.findViewById<TextView>(R.id.desc)
        var topLine = itemView.findViewById<View>(R.id.top_line)
        var bottomLine = itemView.findViewById<View>(R.id.bottom_line)
    }
}