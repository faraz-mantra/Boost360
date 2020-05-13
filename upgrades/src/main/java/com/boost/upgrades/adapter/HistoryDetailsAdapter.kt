package com.boost.upgrades.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.upgrades.R
import com.boost.upgrades.data.api_model.GetPurchaseOrder.WidgetPack
import com.boost.upgrades.data.model.FeaturesModel
import com.bumptech.glide.Glide


class HistoryDetailsAdapter(itemList: List<FeaturesModel>?) :
        RecyclerView.Adapter<HistoryDetailsAdapter.upgradeViewHolder>(){

    private var list = ArrayList<FeaturesModel>()
    private lateinit var context: Context

    init {
        this.list = itemList as ArrayList<FeaturesModel>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(
                R.layout.history_single_item, parent, false
        )
        context = itemView.context

        return upgradeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {

        holder.widgetName.setText(list.get(position).name)
        Glide.with(context).load(list.get(position).primary_image).into(holder.image)
        if(list.get(position).target_business_usecase!=null){
            holder.businessType.setText(list.get(position).target_business_usecase)
            holder.businessType.visibility = View.VISIBLE
        }else{
            holder.businessType.visibility = View.GONE
        }
        holder.view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        if(position == list.size-1){
            holder.view.visibility = View.GONE
        }
    }


    fun addupdates(upgradeModel: List<FeaturesModel>) {
        val initPosition = list.size
        list.clear()
        list.addAll(upgradeModel)
        notifyItemRangeInserted(initPosition, list.size)
    }

    class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val widgetName = itemView.findViewById<TextView>(R.id.widget_name)
        val businessType = itemView.findViewById<TextView>(R.id.business_type)
        val image = itemView.findViewById<ImageView>(R.id.history_detail_image)
        val view = itemView.findViewById<View>(R.id.history_single_item_view)

        var context: Context = itemView.context
//
//
//        fun upgradeListItem(updateModel: UpdatesModel) {
//
//        }
    }
}