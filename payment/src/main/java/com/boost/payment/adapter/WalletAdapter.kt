package com.boost.payment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.payment.R
import com.boost.payment.interfaces.PaymentListener
import com.bumptech.glide.Glide
import com.razorpay.Razorpay


class WalletAdapter(
  razorpay: Razorpay,
  itemList: ArrayList<String>,
  val listener: PaymentListener
) :
  RecyclerView.Adapter<WalletAdapter.upgradeViewHolder>() {

  private var list = ArrayList<String>()
  private lateinit var context: Context
  private lateinit var razorpay: Razorpay

  init {
    this.list = itemList
    this.razorpay = razorpay
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
    val itemView = LayoutInflater.from(parent?.context).inflate(
      R.layout.wallet_item, parent, false
    )
    context = itemView.context

    return upgradeViewHolder(itemView)
  }

  override fun getItemCount(): Int {
    return list.size
  }

  override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {

    holder.title.setText(list.get(position))
    holder.itemView.setOnClickListener {
      listener.walletSelected(list.get(position))
    }
    Glide.with(context).load(razorpay.getWalletSqLogoUrl(list.get(position))).into(holder.image)
    if (list.size - 1 == position) {
      holder.view.visibility = View.GONE
    }
  }

  fun addupdates(items: ArrayList<String>) {
    val initPosition = list.size
    list.clear()
    list.addAll(items)
    notifyItemRangeInserted(initPosition, list.size)
  }

  class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var view = itemView.findViewById<View>(R.id.wallet_view_dummy)!!
    var title = itemView.findViewById<TextView>(R.id.wallet_payment_title)!!
    var image = itemView.findViewById<ImageView>(R.id.wallet_profile_image)!!

  }
}