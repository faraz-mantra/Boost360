package com.boost.cart.adapter

import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.cart.CartActivity
import com.boost.cart.R
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.bumptech.glide.Glide
import kotlin.collections.ArrayList

class NewAddonsAdapter(
        cryptoCurrencies: List<FeaturesModel>?
) : RecyclerView.Adapter<NewAddonsAdapter.upgradeViewHolder>() {

    private var upgradeList = ArrayList<FeaturesModel>()
    var minMonth = 1
    private lateinit var context: Context

    init {
        this.upgradeList = cryptoCurrencies as ArrayList<FeaturesModel>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(
                R.layout.item_pack_features, parent, false
        )
        context = itemView.context

        return upgradeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return upgradeList.size
    }

    override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {

        holder.name.setText(upgradeList.get(position).name)

        Glide.with(context).load(upgradeList.get(position).primary_image).into(holder.image)


//        holder.itemView.setOnClickListener {
//            val details = DetailsFragment.newInstance()
//            val args = Bundle()
//            args.putString("itemId", upgradeList.get(position).feature_code)
//            args.putBoolean("packageView", true)
//            details.arguments = args
//
//            activity.addFragment(details, Constants.DETAILS_FRAGMENT)
//        }
    }

//    fun addupdates(upgradeModel: List<FeaturesModel>, noOfMonth: Int) {
//        minMonth = noOfMonth
//        val initPosition = upgradeList.size
//        upgradeList.clear()
//        upgradeList.addAll(upgradeModel)
//        notifyItemRangeInserted(initPosition, upgradeList.size)
//    }

    class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<ImageView>(R.id.imageView2)!!
        val name = itemView.findViewById<TextView>(R.id.title)!!

    }


}