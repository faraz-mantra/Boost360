package com.boost.marketplace.Adapters

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
import com.boost.marketplace.R

class PacksFaqAdapter(
    val activity: Activity,
    cryptoCurrencies: List<FrequentlyAskedQuestion>?
) : RecyclerView.Adapter<PacksFaqAdapter.upgradeViewHolder>() {

    private var upgradeList = ArrayList<FrequentlyAskedQuestion>()
    private lateinit var context: Context

    init {
        this.upgradeList = cryptoCurrencies as ArrayList<FrequentlyAskedQuestion>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(
            R.layout.item_packs_faq, parent, false
        )
        context = itemView.context

        return upgradeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return upgradeList.size
    }

    override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {
        holder.title.text = upgradeList[position].question
        holder.desc.text = upgradeList[position].answer

        holder.dummy1.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        if (upgradeList.size - 1 == position) {
            holder.dummy1.visibility = View.GONE
        }
        if (position==0){
            holder.desc.visibility=View.VISIBLE
            holder.downArrow.setImageResource(R.drawable.packs_arrow_up)
        }
        holder.title.setOnClickListener {
            if(holder.desc.isVisible) {
                holder.desc.visibility = View.GONE
                holder.downArrow.setImageResource(R.drawable.packs_arrow)
            }else{
                holder.desc.visibility = View.VISIBLE
                holder.downArrow.setImageResource(R.drawable.packs_arrow_up)
            }
        }
        holder.downArrow.setOnClickListener {
            if(holder.desc.isVisible) {
                holder.downArrow.setImageResource(R.drawable.packs_arrow)
                holder.desc.visibility = View.GONE
            }else{
                holder.downArrow.setImageResource(R.drawable.packs_arrow_up)
                holder.desc.visibility = View.VISIBLE
            }
        }
    }

    fun addupdates(upgradeModel: List<FrequentlyAskedQuestion>) {
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
        var dummy1 = itemView.findViewById<View>(R.id.dummy1)
    }


}