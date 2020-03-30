package com.boost.upgrades.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.upgrades.R
import com.boost.upgrades.interfaces.PaymentListener


class WalletAdapter(itemList: ArrayList<String>, val listener: PaymentListener) :
    RecyclerView.Adapter<WalletAdapter.upgradeViewHolder>() {

    private var list = ArrayList<String>()
    private lateinit var context: Context

    init {
        this.list = itemList
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
        if(list.size - 1 == position) {
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

    }
}