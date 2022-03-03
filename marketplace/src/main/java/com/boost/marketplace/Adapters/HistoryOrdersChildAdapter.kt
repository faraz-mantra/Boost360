package com.boost.marketplace.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.dbcenterapi.data.api_model.GetPurchaseOrder.Result
import com.boost.marketplace.R
import com.boost.marketplace.ui.Invoice.InvoiceActivity
import java.lang.Long
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Int

class HistoryOrdersChildAdapter(itemList: List<Result>?,) :
    RecyclerView.Adapter<HistoryOrdersChildAdapter.upgradeViewHolder>() {

    private var list = ArrayList<Result>()
    private lateinit var context: Context

    init {
        this.list = itemList as ArrayList<Result>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(
            R.layout.item_child_history_orders, parent, false
        )
        context = itemView.context
        return upgradeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {

      //  val itemLists = StringBuilder()
//        if (list.get(position).orderId != null) {
//            holder.orderId.setText("Order id #" + list.get(position).orderId!!.replace("order_", ""))
//        }
//        if (list.get(position).purchasedPackageDetails.WidgetPacks.size > 1) {
//            for (item in 0 until list.get(position).purchasedPackageDetails.WidgetPacks.size) {
//                itemLists.append(list.get(position).purchasedPackageDetails.WidgetPacks.get(item).Name)
//                if (item != list.get(position).purchasedPackageDetails.WidgetPacks.size - 1) {
//                    itemLists.append(", ")
//                }
//            }
////            holder.itemCount.setText("+" + (list.size - 1) + " more")
////            holder.itemCount.visibility = View.VISIBLE
////        } else {
////            itemLists.append(list.get(position).purchasedPackageDetails.WidgetPacks.get(0).Name)
////            holder.itemCount.visibility = View.GONE
//        }
          holder.itemLists.text=list.get(position).NameOfWidget

        val dataString = list.get(position).CreatedOn
        val date = Date(Long.parseLong(dataString.substring(6, dataString.length - 2)))
        val dateFormat = SimpleDateFormat("'Time' hh:mm a")
        holder.itemDate.setText(dateFormat.format(date))
        holder.validity.text= list.get(position).totalMonthsValidity.toString()
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
        holder.button.setOnClickListener {
            val intent = Intent(context, InvoiceActivity::class.java)
            intent.putExtra("link", list!!.get(position).invoiceUrl)
            context.startActivity(intent)
        }
    }


    fun addupdates(purchaseResult: List<Result>) {
        val initPosition = list.size
        list.clear()
        list.addAll(purchaseResult)
        notifyItemRangeInserted(initPosition, list.size)
    }

    class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    //    var orderId = itemView.findViewById<TextView>(R.id.item_orderId)!!
     //   var amount = itemView.findViewById<TextView>(R.id.history_item_amount)!!
        var itemLists = itemView.findViewById<TextView>(R.id.title)!!
     //   var itemCount = itemView.findViewById<TextView>(R.id.item_count)!!
        var itemDate = itemView.findViewById<TextView>(R.id.item_orderId)!!
        var button=itemView.findViewById<Button>(R.id.btn_invoice)
        var validity=itemView.findViewById<TextView>(R.id.validity)

        var context: Context = itemView.context


//        fun upgradeListItem(updateModel: UpdatesModel) {
//
//        }
    }
}