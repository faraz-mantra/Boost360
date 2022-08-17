package com.boost.marketplace.Adapters

import android.app.Application
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.dbcenterapi.data.api_model.GetPurchaseOrderV2.WidgetDetail
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.marketplace.R
import com.boost.marketplace.infra.utils.Utils1
import com.boost.marketplace.ui.Invoice.InvoiceActivity
import com.bumptech.glide.Glide
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.Long
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Int

class HistoryOrdersChildAdapter(itemList: ArrayList<WidgetDetail>?) :
    RecyclerView.Adapter<HistoryOrdersChildAdapter.upgradeViewHolder>() {

    private var list = ArrayList<WidgetDetail>()
    private lateinit var context: Context

    init {
        this.list = itemList as ArrayList<WidgetDetail>
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
        val tempCreatedOnDate = list.get(position).CreatedOn
        val createdOnDate = Date(Long.parseLong(tempCreatedOnDate.substring(6, tempCreatedOnDate.length - 2)))
        val default_validity_months = list.get(position).Expiry.Value
        val calendarDates = Calendar.getInstance()
        calendarDates.time = createdOnDate
        calendarDates.add(Calendar.MONTH, default_validity_months)
        val isExpired = Utils1.isExpired(calendarDates.time)
        val nowFormat = SimpleDateFormat("dd MMM yy")
        nowFormat.setTimeZone(Calendar.getInstance().getTimeZone())
        holder.validity.setText("Valid till " + nowFormat.format(calendarDates.time))

        val dataString = list.get(position).CreatedOn
        val date = Date(Long.parseLong(dataString.substring(6, dataString.length - 2)))
        val dateFormat = SimpleDateFormat("'Time' hh:mm a")
        holder.itemDate.setText(dateFormat.format(date))

        holder.button.setOnClickListener {
            val intent = Intent(context, InvoiceActivity::class.java)
            intent.putExtra("link", list.get(position).InvoiceLink)
            context.startActivity(intent)
        }

        CompositeDisposable().add(
            AppDatabase.getInstance(Application())!!
                .featuresDao()
                .getFeaturesItemByFeatureCode(list.get(position).WidgetKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                           holder.title1.text = it.name
                           Glide.with(context).load(it.primary_image).into(holder.single_paidaddon_image)

                }, {
                    it.printStackTrace()
                })
        )
     //   holder.single_paidaddon_image.setColorFilter(Color.rgb(123, 123, 123), android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    fun addupdates(purchaseResult: List<WidgetDetail>) {
        val initPosition = list.size
        list.clear()
        list.addAll(purchaseResult)
        notifyItemRangeInserted(initPosition, list.size)
    }

    class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var title1 = itemView.findViewById<TextView>(R.id.title1)!!
        var itemDate = itemView.findViewById<TextView>(R.id.item_orderId)!!
        var button = itemView.findViewById<Button>(R.id.btn_invoice)
        var validity = itemView.findViewById<TextView>(R.id.validity)
        var single_paidaddon_image = itemView.findViewById<ImageView>(R.id.single_paidaddon_image)
        var context: Context = itemView.context
    }
}
