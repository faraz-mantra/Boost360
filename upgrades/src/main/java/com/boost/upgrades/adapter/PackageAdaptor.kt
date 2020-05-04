package com.boost.upgrades.adapter

import android.content.Context
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.upgrades.R
import com.boost.upgrades.data.api_model.GetAllFeatures.response.Bundles
import com.boost.upgrades.data.model.FeaturesModel
import com.bumptech.glide.Glide


class PackageAdaptor(cryptoCurrencies: List<FeaturesModel>?, bundleData: Bundles) : RecyclerView.Adapter<PackageAdaptor.upgradeViewHolder>() {

    private var upgradeList = ArrayList<FeaturesModel>()
    var bundleData: Bundles
    private lateinit var context: Context

    init {
        this.upgradeList = cryptoCurrencies as ArrayList<FeaturesModel>
        this.bundleData = bundleData
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(
                R.layout.package_details_items, parent, false)
        context = itemView.context

        return upgradeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return upgradeList.size
    }

    override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {

        holder.name.setText(upgradeList.get(position).name)
        holder.title.setText(upgradeList.get(position).target_business_usecase)

        updateView(holder,position)
        Glide.with(context).load(upgradeList.get(position).primary_image).into(holder.image)

        if (position == upgradeList.size - 1) {
            holder.view.visibility = View.INVISIBLE
        }
    }

    fun addupdates(upgradeModel: List<FeaturesModel>) {
        val initPosition = upgradeList.size
        upgradeList.clear()
        upgradeList.addAll(upgradeModel)
        notifyItemRangeInserted(initPosition, upgradeList.size)
    }

    class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val view = itemView.findViewById<View>(R.id.view)!!
        val image = itemView.findViewById<ImageView>(R.id.imageView2)!!
        val name = itemView.findViewById<TextView>(R.id.details)!!
        val title = itemView.findViewById<TextView>(R.id.title)!!
        val price = itemView.findViewById<TextView>(R.id.upgrade_list_price)!!
        val origCost = itemView.findViewById<TextView>(R.id.upgrade_list_orig_cost)!!
        val discount = itemView.findViewById<TextView>(R.id.upgrade_list_discount)!!

    }

    fun updateView(holder: upgradeViewHolder, position: Int){
        var mrpPrice = 0.0
        var grandTotal = 0.0
        for (item in bundleData.included_features){
            if (item.feature_code.equals(upgradeList.get(position).boost_widget_key)){
                val total = (upgradeList.get(position).price - ((upgradeList.get(position).price * item.feature_price_discount_percent) / 100.0))
                grandTotal += total
                mrpPrice += upgradeList.get(position).price
                if(item.feature_price_discount_percent > 0){
                    holder.discount.setText(item.feature_price_discount_percent.toString()+"%")
                    holder.discount.visibility = View.VISIBLE
                }else{
                    holder.discount.visibility = View.GONE
                }
                holder.price.setText("₹"+grandTotal)
                if (grandTotal != mrpPrice) {
                    spannableString(holder, mrpPrice)
                    holder.origCost.visibility = View.VISIBLE
                } else {
                    holder.origCost.visibility = View.GONE
                }
                break
            }
        }
    }

    fun spannableString(holder: upgradeViewHolder, value: Double) {
        val origCost = SpannableString("₹" + value)

        origCost.setSpan(
                StrikethroughSpan(),
                0,
                origCost.length,
                0
        )
        holder.origCost.setText(origCost)
    }
}