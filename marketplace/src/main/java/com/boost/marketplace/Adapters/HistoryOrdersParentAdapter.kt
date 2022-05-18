package com.boost.marketplace.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boost.dbcenterapi.data.api_model.GetPurchaseOrderV2.WidgetDetail
import com.boost.marketplace.R
import com.boost.marketplace.interfaces.HistoryFragmentListener
import java.lang.Long
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Int

class HistoryOrdersParentAdapter (itemList: List<WidgetDetail>?, val listener: HistoryFragmentListener) :
    RecyclerView.Adapter<HistoryOrdersParentAdapter.upgradeViewHolder>() {

    private var list = ArrayList<WidgetDetail>()
    private lateinit var context: Context

    init {
        this.list = itemList as ArrayList<WidgetDetail>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(
            R.layout.item_history_orders, parent, false
        )
        context = itemView.context
        return upgradeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {

        val dataString = list.get(position).CreatedOn
        val date = Date(Long.parseLong(dataString.substring(6, dataString.length - 2)))
        val dateFormat = SimpleDateFormat("dd-MMM-yyyy ")
        holder.itemDate.setText(dateFormat.format(date))

        val layoutManager1 = LinearLayoutManager(holder.recycler.context)
        layoutManager1.orientation = LinearLayoutManager.VERTICAL
        val sectionLayout = HistoryOrdersChildAdapter(list)
        holder.recycler.setAdapter(sectionLayout)
        holder.recycler.setLayoutManager(layoutManager1)
    }

    fun addupdates(purchaseResult: List<WidgetDetail>) {
        val initPosition = list.size
        list.clear()
        list.addAll(purchaseResult)
        notifyItemRangeInserted(initPosition, list.size)
    }

    class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemDate = itemView.findViewById<TextView>(R.id.titleDay)!!
        var recycler=itemView.findViewById<RecyclerView>(R.id.order_child_history_recycler)
        var context: Context = itemView.context
    }
}