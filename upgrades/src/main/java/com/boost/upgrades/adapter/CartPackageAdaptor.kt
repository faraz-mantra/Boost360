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
import com.boost.upgrades.data.model.CartModel
import com.boost.upgrades.interfaces.CartFragmentListener


class CartPackageAdaptor(list: List<CartModel>?, val listener: CartFragmentListener) : RecyclerView.Adapter<CartPackageAdaptor.upgradeViewHolder>() {

    private var bundlesList = ArrayList<CartModel>()
    private lateinit var context: Context

    init {
        this.bundlesList = list as ArrayList<CartModel>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(
                R.layout.cart_single_package, parent, false)
        context = itemView.context

        return upgradeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return bundlesList.size
    }

    override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {
        holder.name.setText(bundlesList.get(position).item_name)
        val price = bundlesList.get(position).price * bundlesList.get(position).min_purchase_months
        val MRPPrice = bundlesList.get(position).MRPPrice * bundlesList.get(position).min_purchase_months
        if (bundlesList.get(position).min_purchase_months > 1) {
            holder.price.setText("₹" + price.toString() + "/" + bundlesList.get(position).min_purchase_months + "month")
        } else {
            holder.price.setText("₹" + price.toString() + "/month")
        }
        if(price != MRPPrice) {
            spannableString(holder, MRPPrice, bundlesList.get(position).min_purchase_months)
            holder.orig_cost.visibility = View.VISIBLE
        }else{
            holder.orig_cost.visibility = View.GONE
        }
        if (bundlesList.get(position).discount > 0) {
            holder.discount.setText(bundlesList.get(position).discount.toString() + "%")
            holder.discount.visibility = View.VISIBLE
        } else {
            holder.discount.visibility = View.GONE
        }
        holder.removePackage.setOnClickListener {
            listener.deleteCartAddonsItem(bundlesList.get(position).boost_widget_key)
        }
        holder.view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        if(bundlesList.size - 1 == position) {
            holder.view.visibility = View.GONE
        }
    }

    fun addupdates(upgradeModel: List<CartModel>) {
        val initPosition = bundlesList.size
        bundlesList.clear()
        bundlesList.addAll(upgradeModel)
        notifyItemRangeInserted(initPosition, bundlesList.size)
    }

    class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.package_title)
        val price = itemView.findViewById<TextView>(R.id.package_price)
        val orig_cost = itemView.findViewById<TextView>(R.id.package_orig_cost)
        val discount = itemView.findViewById<TextView>(R.id.package_discount)
        val image = itemView.findViewById<ImageView>(R.id.package_profile_image)
        val removePackage = itemView.findViewById<ImageView>(R.id.package_close)
        var view = itemView.findViewById<View>(R.id.cart_single_package_bottom_view)!!
    }

    fun spannableString(holder: upgradeViewHolder, value: Double, minMonth: Int) {
        val origCost: SpannableString
        if (minMonth > 1) {
            val originalCost = value
            origCost = SpannableString("₹" + originalCost + "/" + minMonth + "month")
        } else {
            origCost = SpannableString("₹" + value + "/month")
        }

        origCost.setSpan(
                StrikethroughSpan(),
                0,
                origCost.length,
                0
        )
        holder.orig_cost.setText(origCost)
    }
}