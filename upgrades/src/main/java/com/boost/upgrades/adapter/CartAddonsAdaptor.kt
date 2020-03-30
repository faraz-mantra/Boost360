package com.boost.upgrades.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.upgrades.R
import com.boost.upgrades.data.model.CartModel
import com.boost.upgrades.data.model.WidgetModel
import com.boost.upgrades.interfaces.CartFragmentListener
import com.bumptech.glide.Glide


class CartAddonsAdaptor(cardItems: List<CartModel>?, val listener: CartFragmentListener) :
    RecyclerView.Adapter<CartAddonsAdaptor.upgradeViewHolder>() {

    private var list = ArrayList<CartModel>()
    private lateinit var context: Context

    init {
        this.list = cardItems as ArrayList<CartModel>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(
            R.layout.cart_single_addons, parent, false
        )
        context = itemView.context

        return upgradeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {
        Glide.with(context).load(list.get(position).link).into(holder.image)
        holder.title.setText(list.get(position).item_name)
        holder.price.setText("₹"+list.get(position).price+"/month")
        holder.MRPPrice.setText("₹"+list.get(position).MRPPrice+"/month")
        holder.discount.setText("- "+list.get(position).discount+"%")
        holder.remove_addons.setOnClickListener {
            listener.deleteCartAddonsItem(list.get(position).id)
        }
        if(list.size - 1 == position) {
            holder.view.visibility = View.GONE
        }
    }

    fun addupdates(cardItems: List<CartModel>) {
        val initPosition = list.size
        list.clear()
        list.addAll(cardItems)
        notifyItemRangeInserted(initPosition, list.size)
    }

    class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var image = itemView.findViewById<ImageView>(R.id.addons_profile_image)!!
        var remove_addons = itemView.findViewById<ImageView>(R.id.addons_remove)!!
        var title = itemView.findViewById<TextView>(R.id.addons_title)!!
        var price = itemView.findViewById<TextView>(R.id.cart_item_price)!!
        var MRPPrice = itemView.findViewById<TextView>(R.id.cart_item_orig_cost)!!
        var discount = itemView.findViewById<TextView>(R.id.cart_item_discount)!!
        var view = itemView.findViewById<View>(R.id.cart_single_addons_bottom_view)!!


        fun upgradeListItem(updateModel: WidgetModel) {

        }
    }
}