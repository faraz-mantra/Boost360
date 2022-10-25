package com.boost.marketplace.Adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.cart.utils.Utils.priceCalculatorForYear
import com.boost.cart.utils.Utils.yearlyOrMonthlyOrEmptyValidity
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.marketplace.R
import com.boost.marketplace.interfaces.AddonsListener
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.*


class CompareItemAdapter(
    cryptoCurrencies: List<FeaturesModel>?,
    val addonsListener: AddonsListener,
    val activity: Activity,
    var minMonth: Int = 1
) : RecyclerView.Adapter<CompareItemAdapter.upgradeViewHolder>() {

    private var upgradeList = ArrayList<FeaturesModel>()
    private lateinit var context: Context

    init {
        this.upgradeList = cryptoCurrencies as ArrayList<FeaturesModel>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(
            R.layout.item_pack_list, parent, false
        )
        context = itemView.context
        return upgradeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return upgradeList.size
    }

    override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {

        holder.name.setText(upgradeList.get(position).name)
        val cryptocurrencyItem = upgradeList[position]
        holder.upgradeListItem(holder, cryptocurrencyItem, activity, minMonth)
        Glide.with(context).load(upgradeList.get(position).primary_image).into(holder.image)
        holder.itemView.setOnClickListener {
            addonsListener.onAddonsClicked(upgradeList.get(position))
        }
    }

    fun addupdates(upgradeModel: List<FeaturesModel>, noOfMonth: Int) {
        minMonth = noOfMonth
        val initPosition = upgradeList.size
        upgradeList.clear()
        upgradeList.addAll(upgradeModel)
        val index =
            upgradeList.indexOf(upgradeList.find { it.boost_widget_key == "FESTIVEPOSTERS" })
        if (index > 0) {
            upgradeList.removeAt(index)
        }

        notifyItemRangeInserted(initPosition, upgradeList.size)
    }

    class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<ImageView>(R.id.imageView2)!!
        val name = itemView.findViewById<TextView>(R.id.title)!!
        val price = itemView.findViewById<TextView>(R.id.price)!!

        fun upgradeListItem(
            holder: upgradeViewHolder,
            updateModel: FeaturesModel,
            activity: Activity,
            minMonth: Int
        ) {
            val discount = 100 - updateModel.discount_percent
            var price = (discount * (updateModel.price * minMonth)) / 100
            price = priceCalculatorForYear(price, updateModel.widget_type?:"", activity)
            holder.price.text = "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(price) + yearlyOrMonthlyOrEmptyValidity(updateModel.widget_type?:"", activity, minMonth)
        }
    }
}