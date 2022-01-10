package com.boost.marketplace.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boost.dbcenterapi.data.api_model.GetPurchaseOrder.Result
import com.boost.marketplace.R
import com.boost.marketplace.interfaces.HistoryFragmentListener
import com.boost.marketplace.ui.History_Orders.HistoryOrdersActivity
import java.lang.Long
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HistoryOrdersParentAdapter (itemList: List<Result>?, val listener: HistoryFragmentListener) :
    RecyclerView.Adapter<HistoryOrdersParentAdapter.upgradeViewHolder>() {

    private var list = ArrayList<Result>()
    private lateinit var context: Context

    init {
        this.list = itemList as ArrayList<Result>
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

        val itemLists = StringBuilder()
//        if (list.get(position).orderId != null) {
//            holder.orderId.setText("Order id #" + list.get(position).orderId!!.replace("order_", ""))
//        }
        if (list.get(position).purchasedPackageDetails.WidgetPacks.size > 1) {
            for (item in 0 until list.get(position).purchasedPackageDetails.WidgetPacks.size) {
                itemLists.append(list.get(position).purchasedPackageDetails.WidgetPacks.get(item).Name)
                if (item != list.get(position).purchasedPackageDetails.WidgetPacks.size - 1) {
                    itemLists.append(", ")
                }
            }
//          //  holder.itemCount.setText("+" + (list.size - 1) + " more")
//         //   holder.itemCount.visibility = View.VISIBLE
//        } else {
//            itemLists.append(list.get(position).purchasedPackageDetails.WidgetPacks.get(0).Name)
//            holder.itemCount.visibility = View.GONE
        }
      //  holder.itemLists.setText(itemLists)

        val dataString = list.get(position).CreatedOn
        val date = Date(Long.parseLong(dataString.substring(6, dataString.length - 2)))
        val dateFormat = SimpleDateFormat("dd-MMM-yyyy (HH:mm)")
        holder.itemDate.setText(dateFormat.format(date))
        /*val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val output = SimpleDateFormat("dd-MMM-yyyy (hh:mm a)")

        var d: Date? = null
        try {
            d = input.parse(list.get(position).CreatedOn)
            holder.itemDate.setText(output.format(d))
        } catch (e: ParseException) {
            e.printStackTrace()
        }*/


//        holder.amount.setText(
//            "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(list.get(position).paidAmount)
//        )
        holder.itemView.setOnClickListener {
            listener.viewHistoryItem(list.get(position))
        }

        val layoutManager1 = LinearLayoutManager(holder.recycler.context)
       layoutManager1.orientation = LinearLayoutManager.VERTICAL

        val sectionLayout = HistoryOrdersChildAdapter(list)
        holder.recycler.setAdapter(sectionLayout)
        holder.recycler.setLayoutManager(layoutManager1)


    }


    fun addupdates(purchaseResult: List<Result>) {
        val initPosition = list.size
        list.clear()
        list.addAll(purchaseResult)
        notifyItemRangeInserted(initPosition, list.size)
    }

    class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

//        var orderId = itemView.findViewById<TextView>(R.id.item_orderId)!!
//        var amount = itemView.findViewById<TextView>(R.id.history_item_amount)!!
//        var itemLists = itemView.findViewById<TextView>(R.id.item_lists)!!
//        var itemCount = itemView.findViewById<TextView>(R.id.item_count)!!
        var itemDate = itemView.findViewById<TextView>(R.id.titleDay)!!
        var recycler=itemView.findViewById<RecyclerView>(R.id.order_child_history_recycler)

        var context: Context = itemView.context
//
//
//        fun upgradeListItem(updateModel: UpdatesModel) {
//
//        }
    }
}