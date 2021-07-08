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
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList


class CartRenewalAdaptor(cardItems: List<CartModel>?, val listener: CartFragmentListener) :
  RecyclerView.Adapter<CartRenewalAdaptor.renewalViewHolder>() {

  private var list = ArrayList<CartModel>()
  private lateinit var context: Context

  init {
    this.list = cardItems as ArrayList<CartModel>
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): renewalViewHolder {
    val itemView =
      LayoutInflater.from(parent.context).inflate(R.layout.cart_single_addons, parent, false)
    context = itemView.context
    return renewalViewHolder(itemView)
  }

  override fun getItemCount(): Int {
    return list.size
  }

  override fun onBindViewHolder(holder: renewalViewHolder, position: Int) {
    val data = list[position]
    Glide.with(context).load(data.link ?: "").placeholder(R.drawable.boost_360_insignia)
      .into(holder.image)
    holder.title.text = data.item_name
    if (data.min_purchase_months > 1) {
      holder.price.text = "₹${
        NumberFormat.getNumberInstance(Locale.ENGLISH).format(data.price)
      }/${data.min_purchase_months}month"
    } else {
      holder.price.text =
        "₹${NumberFormat.getNumberInstance(Locale.ENGLISH).format(data.price)}/month"
    }
    holder.MRPPrice.visibility = View.VISIBLE
    if (data.discount > 0) {
      holder.discount.visibility = View.VISIBLE
      holder.discount.text = "${data.discount} %"
    } else holder.discount.visibility = View.GONE
    holder.remove_addons.setOnClickListener {
      listener.actionClickRenewal(position, data, 1)
    }
    holder.view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

    holder.view.visibility = if (list.size - 1 == position) View.GONE else View.VISIBLE
  }

  fun renewalNotify(cardItems: List<CartModel>) {
    val initPosition = list.size
    list.clear()
    list.addAll(cardItems)
    notifyItemRangeInserted(initPosition, list.size)
  }

  class renewalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var image = itemView.findViewById<ImageView>(R.id.addons_profile_image)!!
    var remove_addons = itemView.findViewById<ImageView>(R.id.addons_remove)!!
    var title = itemView.findViewById<TextView>(R.id.addons_title)!!
    var price = itemView.findViewById<TextView>(R.id.cart_item_price)!!
    var MRPPrice = itemView.findViewById<TextView>(R.id.cart_item_orig_cost)!!
    var discount = itemView.findViewById<TextView>(R.id.cart_item_discount)!!
    var view = itemView.findViewById<View>(R.id.cart_single_addons_bottom_view)!!

  }

  fun spannableString(holder: renewalViewHolder, value: Double, minMonth: Int) {
    val origCost: SpannableString = if (minMonth > 1) {
      SpannableString(
        "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH)
          .format(value) + "/" + minMonth + "months"
      )
    } else {
      SpannableString("₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(value) + "/month")
    }
    origCost.setSpan(StrikethroughSpan(), 0, origCost.length, 0)
    holder.MRPPrice.text = origCost
  }
}