package com.boost.upgrades.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.upgrades.R
import com.boost.upgrades.data.api_model.GetPurchaseOrder.Result
import com.boost.upgrades.interfaces.HistoryFragmentListener
import com.google.gson.Gson
import java.lang.Long
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HistoryAdapter(itemList: List<Result>?,val listener: HistoryFragmentListener) :
        RecyclerView.Adapter<HistoryAdapter.upgradeViewHolder>(), View.OnClickListener {

    private var list = ArrayList<Result>()
    private lateinit var context: Context

    init {
        this.list = itemList as ArrayList<Result>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(
                R.layout.history_item, parent, false
        )
        context = itemView.context


        itemView.setOnClickListener(this)
        return upgradeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {

        if(list.get(position).orderId != null) {
            holder.itemOrderId.setText("Order id #" + list.get(position).orderId!!.replace("order_", ""))
        }
        val itemLists = StringBuilder()
        itemLists.append(list.get(position).purchasedPackageDetails.WidgetPacks.get(0).Name)
        if(list.get(position).purchasedPackageDetails.WidgetPacks.size > 1) {
            itemLists.append("+" + list.get(position).purchasedPackageDetails.WidgetPacks.size + "more")
        }
        holder.itemLists.setText(itemLists)

        val dataString = list.get(position).CreatedOn
        val date = Date(Long.parseLong(dataString.substring(6, dataString.length - 2)))
        val dateFormat = SimpleDateFormat("dd-MMM-yyyy (hh:mm a)")
        holder.itemDate.setText(dateFormat.format(date))
        holder.viewButton.setOnClickListener {
                listener.viewHistoryItem(list.get(position))
        }
    }

    override fun onClick(v: View?) {

    }

    fun addupdates(purchaseResult: List<Result>) {
        val initPosition = list.size
        list.clear()
        list.addAll(purchaseResult)
        notifyItemRangeInserted(initPosition, list.size)
    }

    class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var viewButton = itemView.findViewById<TextView>(R.id.history_viewButton)!!
        var itemOrderId = itemView.findViewById<TextView>(R.id.item_orderId)!!
        var itemLists = itemView.findViewById<TextView>(R.id.item_lists)!!
        var itemDate = itemView.findViewById<TextView>(R.id.item_date)!!

        var context: Context = itemView.context
//
//
//        fun upgradeListItem(updateModel: UpdatesModel) {
//
//        }
    }
}