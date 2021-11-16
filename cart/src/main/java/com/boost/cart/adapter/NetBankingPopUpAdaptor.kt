package com.boost.cart.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.cart.R
import com.boost.cart.datamodule.SingleNetBankData
import com.boost.cart.interfaces.NetBankingListener

class NetBankingPopUpAdaptor(
  itemList: ArrayList<SingleNetBankData>,
  val listener: NetBankingListener
) : RecyclerView.Adapter<NetBankingPopUpAdaptor.upgradeViewHolder>(), View.OnClickListener {

  private var list = ArrayList<SingleNetBankData>()
  private lateinit var context: Context

  init {
    this.list = itemList
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
    val itemView = LayoutInflater.from(parent?.context).inflate(
      R.layout.netbanking_popup_item, parent, false
    )
    context = itemView.context


    itemView.setOnClickListener(this)
    return upgradeViewHolder(itemView)
  }

  override fun getItemCount(): Int {
    return list.size
  }

  override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {
    holder.bankName.setText(list.get(position).bankName)
  }

  override fun onClick(v: View?) {
    listener.popupSelectedBank(v!!)
  }

  fun addupdates(itemlist: List<SingleNetBankData>) {
    val initPosition = list.size
//        list = hash
    list.addAll(itemlist)
    notifyItemRangeInserted(initPosition, list.size)
  }

  class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var bankName = itemView.findViewById<TextView>(R.id.netbanking_bankname)!!

  }
}